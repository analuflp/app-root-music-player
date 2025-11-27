package com.alf.app_root_music_player.player

import com.alf.app_root_music_player.model.Music

data class PlayerUiState(
    val musics: List<Music> = emptyList(),
    val currentIndex: Int = -1,
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val duration: Int = 0
    )

