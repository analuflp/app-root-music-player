package com.alf.app_root_music_player.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Game(
    val id: String,
    val name: String,
    val iconResName: String, // Resource name (e.g., "ic_launcher")
    val soundtracks: List<Soundtrack>,
    val soundEffects: List<SoundEffect>,
    @Transient val iconResId: Int = 0 // Resolved at runtime
) {
    fun withResolvedIds(
        iconResId: Int,
        resolvedSoundtracks: List<Soundtrack>,
        resolvedSoundEffects: List<SoundEffect>
    ) = copy(
        iconResId = iconResId,
        soundtracks = resolvedSoundtracks,
        soundEffects = resolvedSoundEffects
    )
}

