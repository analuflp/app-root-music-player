package com.alf.app_root_music_player.player

import com.alf.app_root_music_player.model.musicPlayer

data class PlayerUiState(
        val musics: List<musicPlayer> = emptyList(),
        val currentIndex: Int = -1,
        val isPlaying: Boolean = false
    )

