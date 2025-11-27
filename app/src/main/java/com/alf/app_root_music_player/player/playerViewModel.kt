package com.alf.app_root_music_player.player

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alf.app_root_music_player.R
import com.alf.app_root_music_player.model.Music
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class playerViewModel : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    // Lista fixa de músicas
    private val musics = listOf(
        Music(
            "Winter",
            R.raw.winter_theme,
            coverResId = R.drawable.cover_winter),

        Music(
            "Overworld",
            R.raw.overworld_theme,
            coverResId = R.drawable.menu),
        Music(
            "Battle",
            R.raw.battle_theme,
            coverResId = R.drawable.batle_theme),
        Music(
            "Victory",
            R.raw.victory_theme,
            coverResId = R.drawable.victory_theme)
    )

    private val _uiState = MutableStateFlow(
        PlayerUiState(
            musics = musics,
            currentIndex = -1,
            isPlaying = false
        )
    )
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    // --- Funções de controle ---

    fun onMusicSelected(index: Int, context: Context) {
        if (index < 0 || index >= musics.size) return
        playMusic(index, context)
    }

    fun onPlayPauseClicked(context: Context) {
        val current = _uiState.value

        // Se nenhuma música está selecionada ainda → toca a primeira
        if (current.currentIndex == -1) {
            if (musics.isNotEmpty()) {
                playMusic(0, context)
            }
            return
        }

        val mp = mediaPlayer

        // Se o player não existe mais, recria e toca a música atual
        if (mp == null) {
            playMusic(current.currentIndex, context)
            return
        }

        // Se já existe player
        if (mp.isPlaying) {
            mp.pause()
            progressJob?.cancel()
            _uiState.value = current.copy(isPlaying = false)
        } else {

            mp.start()
            startProgressUpdates()
            _uiState.value = current.copy(isPlaying = true)
        }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()

        progressJob = viewModelScope.launch {
            while (true) {
                val mp = mediaPlayer ?: break
                if (!mp.isPlaying) break

                _uiState.value = _uiState.value.copy(
                    currentPosition = mp.currentPosition,
                    duration = mp.duration
                )
                delay(200)
            }
        }
    }

    fun onNextClicked(context: Context) {
        val current = _uiState.value
        if (musics.isEmpty()) return

        val nextIndex =
            if (current.currentIndex == -1) 0
            else (current.currentIndex + 1) % musics.size

        playMusic(nextIndex, context)
    }

    fun onPreviousClicked(context: Context) {
        val current = _uiState.value
        if (musics.isEmpty()) return

        val prevIndex = when {
            current.currentIndex == -1 -> 0
            current.currentIndex == 0 -> musics.size - 1
            else -> current.currentIndex - 1
        }

        playMusic(prevIndex, context)
    }

    private fun playMusic(index: Int, context: Context) {
        progressJob?.cancel()
        mediaPlayer?.release()

        val music = musics[index]

        mediaPlayer = MediaPlayer.create(context, music.resId).apply {
            // Loop
            isLooping = true
            start()
        }

        progressJob = viewModelScope.launch {
            while (true) {
                val mp = mediaPlayer ?: break

                _uiState.value = _uiState.value.copy(
                    currentIndex = index,
                    isPlaying = true,
                    currentPosition = mp.currentPosition,
                    duration = mp.duration
                )

                delay(200)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}