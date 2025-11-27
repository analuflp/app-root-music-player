package com.alf.app_root_music_player.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alf.app_root_music_player.audio.AudioManager
import com.alf.app_root_music_player.data.model.Game
import com.alf.app_root_music_player.data.model.SoundEffect
import com.alf.app_root_music_player.data.model.Soundtrack
import com.alf.app_root_music_player.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GamePlayerUiState(
    val game: Game? = null,
    val currentSoundtrack: Soundtrack? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val duration: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

class GamePlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GameRepository(application)
    private val audioManager = AudioManager(application, viewModelScope)
    
    private val _uiState = MutableStateFlow(GamePlayerUiState())
    val uiState: StateFlow<GamePlayerUiState> = _uiState.asStateFlow()
    
    init {
        // Observe audio state changes
        viewModelScope.launch {
            audioManager.audioState.collect { audioState ->
                _uiState.value = _uiState.value.copy(
                    currentSoundtrack = audioState.currentSoundtrack,
                    isPlaying = audioState.isPlaying,
                    currentPosition = audioState.currentPosition,
                    duration = audioState.duration
                )
            }
        }
    }
    
    fun loadGame(gameId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val game = repository.getGameById(gameId)
                if (game != null) {
                    _uiState.value = _uiState.value.copy(
                        game = game,
                        isLoading = false
                    )
                    // Preload sound effects
                    audioManager.preloadSoundEffects(game.soundEffects)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Game not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load game"
                )
            }
        }
    }
    
    fun selectSoundtrack(soundtrack: Soundtrack) {
        audioManager.playSoundtrack(soundtrack)
    }
    
    fun togglePlayPause() {
        val currentState = _uiState.value
        if (currentState.isPlaying) {
            audioManager.pauseSoundtrack()
        } else {
            if (currentState.currentSoundtrack != null) {
                audioManager.resumeSoundtrack()
            } else if (currentState.game?.soundtracks?.isNotEmpty() == true) {
                // If no soundtrack is playing, start the first one
                selectSoundtrack(currentState.game.soundtracks[0])
            }
        }
    }
    
    fun playSoundEffect(soundEffect: SoundEffect) {
        audioManager.playSoundEffect(soundEffect)
    }
    
    fun seekTo(position: Int) {
        audioManager.seekTo(position)
    }
    
    override fun onCleared() {
        super.onCleared()
        audioManager.release()
    }
}

