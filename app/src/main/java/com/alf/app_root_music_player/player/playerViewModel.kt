package com.alf.app_root_music_player.player

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import com.alf.app_root_music_player.R
import com.alf.app_root_music_player.model.Music
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class playerViewModel : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null

    // Lista fixa de músicas (poderia vir de um repositório)
    private val musics = listOf(
        Music(
            "Winter",
            R.raw.winter_theme,
            coverResId = R.drawable.cover_winter),

        Music(
            "Overworld",
            R.raw.overworld_theme,
            coverResId = R.drawable.menu),
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

        // Se nenhuma música foi selecionada ainda, começa pela primeira
        if (current.currentIndex == -1) {
            if (musics.isNotEmpty()) {
                playMusic(0, context)
            }
            return
        }

        val mp = mediaPlayer
        if (mp == null) {
            // Se por algum motivo o player foi liberado, recria e toca a atual
            playMusic(current.currentIndex, context)
        } else {
            if (mp.isPlaying) {
                mp.pause()
                _uiState.value = current.copy(isPlaying = false)
            } else {
                mp.start()
                _uiState.value = current.copy(isPlaying = true)
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
        // Libera o player antigo
        mediaPlayer?.release()

        val music = musics[index]

        mediaPlayer = MediaPlayer.create(context, music.resId).apply {
            // 🔁 IMPORTANTE: música fica loopando sozinha
            isLooping = true
            start()
        }

        _uiState.value = _uiState.value.copy(
            currentIndex = index,
            isPlaying = true
        )
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}