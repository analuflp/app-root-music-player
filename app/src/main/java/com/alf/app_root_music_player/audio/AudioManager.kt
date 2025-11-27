package com.alf.app_root_music_player.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.alf.app_root_music_player.data.model.SoundEffect
import com.alf.app_root_music_player.data.model.Soundtrack
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope

class AudioManager(private val context: Context, private val scope: CoroutineScope) {
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private var fadeJob: Job? = null
    private var isSeeking = false
    
    private val soundPool: SoundPool by lazy {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        SoundPool.Builder()
            .setMaxStreams(10) // Allow up to 10 simultaneous sound effects
            .setAudioAttributes(audioAttributes)
            .build()
    }
    
    private val soundEffectMap = mutableMapOf<String, Int>() // Map of sound effect ID to SoundPool ID
    private val playingSoundEffects = mutableMapOf<String, Int>() // Track currently playing sound effect IDs to their stream IDs
    
    private val _audioState = MutableStateFlow(AudioState())
    val audioState: StateFlow<AudioState> = _audioState.asStateFlow()
    
    /**
     * Plays a soundtrack with fade-in transition
     */
    fun playSoundtrack(soundtrack: Soundtrack) {
        scope.launch {
            // If there's a current soundtrack playing, fade it out first
            val currentState = _audioState.value
            if (currentState.isPlaying && currentState.currentSoundtrack != null) {
                fadeOutCurrent()
            }
            
            // Release previous MediaPlayer
            progressJob?.cancel()
            mediaPlayer?.release()
            mediaPlayer = null
            
            // Create and prepare new MediaPlayer
            if (soundtrack.audioResId == 0) {
                _audioState.value = AudioState(
                    currentSoundtrack = soundtrack,
                    isPlaying = false,
                    currentPosition = 0,
                    duration = 0
                )
                return@launch
            }
            
            mediaPlayer = MediaPlayer.create(context, soundtrack.audioResId)?.apply {
                isLooping = true
                setVolume(0f, 0f) // Start at 0 volume for fade-in
            }
            
            if (mediaPlayer == null) {
                _audioState.value = AudioState(
                    currentSoundtrack = soundtrack,
                    isPlaying = false,
                    currentPosition = 0,
                    duration = 0
                )
                return@launch
            }
            
            val duration = mediaPlayer?.duration ?: 0
            mediaPlayer?.start()
            
            // Fade in
            fadeIn()
            
            // Update state and start progress tracking
            _audioState.value = AudioState(
                currentSoundtrack = soundtrack,
                isPlaying = true,
                currentPosition = 0,
                duration = duration
            )
            
            startProgressUpdates()
        }
    }
    
    /**
     * Stops the current soundtrack with fade-out transition
     */
    @Suppress("UNUSED")
    fun stopSoundtrack() {
        scope.launch {
            fadeOutCurrent()
            progressJob?.cancel()
            mediaPlayer?.release()
            mediaPlayer = null
            _audioState.value = AudioState()
        }
    }
    
    /**
     * Pauses the current soundtrack
     */
    fun pauseSoundtrack() {
        mediaPlayer?.pause()
        progressJob?.cancel()
        _audioState.value = _audioState.value.copy(isPlaying = false)
    }
    
    /**
     * Resumes the current soundtrack
     */
    fun resumeSoundtrack() {
        mediaPlayer?.start()
        startProgressUpdates()
        _audioState.value = _audioState.value.copy(isPlaying = true)
    }
    
    /**
     * Seeks to a specific position in the current soundtrack
     */
    fun seekTo(position: Int) {
        val mp = mediaPlayer ?: return
        isSeeking = true
        mp.seekTo(position)
        _audioState.value = _audioState.value.copy(currentPosition = position)
        // Reset seeking flag after a short delay to allow MediaPlayer to update
        scope.launch {
            delay(100)
            isSeeking = false
        }
    }
    
    /**
     * Plays a sound effect (one-shot, doesn't pause soundtrack)
     * If the sound effect is already playing, stops it instead
     */
    fun playSoundEffect(soundEffect: SoundEffect) {
        // Check if this sound effect is already playing
        val existingStreamId = playingSoundEffects[soundEffect.id]
        if (existingStreamId != null) {
            // Stop the currently playing sound effect
            soundPool.stop(existingStreamId)
            playingSoundEffects.remove(soundEffect.id)
            return
        }
        
        // Check if resource ID is valid
        if (soundEffect.audioResId == 0) {
            return
        }
        
        scope.launch {
            val soundId = soundEffectMap.getOrPut(soundEffect.id) {
                soundPool.load(context, soundEffect.audioResId, 1)
            }
            
            // Wait a bit for the sound to load if it was just loaded
            delay(50)
            
            val streamId = soundPool.play(
                soundId,
                1.0f, // left volume
                1.0f, // right volume
                1, // priority
                0, // loop (0 = no loop)
                1.0f // rate
            )
            
            if (streamId > 0) {
                // Track that this sound effect is playing with its stream ID
                playingSoundEffects[soundEffect.id] = streamId
                
                // Get actual duration using MediaPlayer
                val duration = if (soundEffect.audioResId != 0) {
                    try {
                        val tempPlayer = MediaPlayer.create(context, soundEffect.audioResId)
                        val durationMs = tempPlayer?.duration ?: 3000
                        tempPlayer?.release()
                        durationMs
                    } catch (_: Exception) {
                        3000 // Fallback to 3 seconds
                    }
                } else {
                    3000 // Fallback if resource not found
                }
                
                // Remove from tracking when sound finishes
                launch {
                    delay(duration.toLong())
                    playingSoundEffects.remove(soundEffect.id)
                }
            }
        }
    }
    
    /**
     * Preloads sound effects for a game
     */
    fun preloadSoundEffects(soundEffects: List<SoundEffect>) {
        scope.launch {
            soundEffects.forEach { soundEffect ->
                if (!soundEffectMap.containsKey(soundEffect.id) && soundEffect.audioResId != 0) {
                    val soundId = soundPool.load(context, soundEffect.audioResId, 1)
                    soundEffectMap[soundEffect.id] = soundId
                }
            }
        }
    }
    
    /**
     * Unloads sound effects that are no longer needed
     */
    @Suppress("UNUSED")
    fun unloadSoundEffects(soundEffectIds: List<String>) {
        scope.launch {
            soundEffectIds.forEach { id ->
                soundEffectMap[id]?.let { soundId ->
                    soundPool.unload(soundId)
                    soundEffectMap.remove(id)
                }
            }
        }
    }
    
    private fun fadeIn() {
        fadeJob?.cancel()
        fadeJob = scope.launch {
            val mp = mediaPlayer ?: return@launch
            val fadeDuration = 500L // 500ms fade-in
            val steps = 20
            val stepDelay = fadeDuration / steps
            val volumeStep = 1.0f / steps
            
            for (i in 1..steps) {
                val volume = i * volumeStep
                mp.setVolume(volume, volume)
                delay(stepDelay)
            }
            mp.setVolume(1.0f, 1.0f) // Ensure max volume
        }
    }
    
    private suspend fun fadeOutCurrent() {
        fadeJob?.cancel()
        val mp = mediaPlayer ?: return
        val fadeDuration = 500L // 500ms fade-out
        val steps = 20
        val stepDelay = fadeDuration / steps
        val initialVolume = 1.0f
        val volumeStep = initialVolume / steps
        
        for (i in steps downTo 1) {
            val volume = i * volumeStep
            mp.setVolume(volume, volume)
            delay(stepDelay)
        }
        mp.setVolume(0f, 0f)
    }
    
    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (true) {
                val mp = mediaPlayer ?: break
                if (!mp.isPlaying) break
                
                // Don't update position if we're currently seeking
                if (!isSeeking) {
                    _audioState.value = _audioState.value.copy(
                        currentPosition = mp.currentPosition,
                        duration = mp.duration
                    )
                }
                delay(200)
            }
        }
    }
    
    /**
     * Releases all audio resources
     */
    fun release() {
        progressJob?.cancel()
        fadeJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
        soundPool.release()
        soundEffectMap.clear()
        _audioState.value = AudioState()
    }
}

