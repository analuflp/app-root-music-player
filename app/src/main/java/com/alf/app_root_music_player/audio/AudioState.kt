package com.alf.app_root_music_player.audio

import com.alf.app_root_music_player.data.model.Soundtrack

data class AudioState(
    val currentSoundtrack: Soundtrack? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val duration: Int = 0
)

