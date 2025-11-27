package com.alf.app_root_music_player.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SoundEffect(
    val id: String,
    val title: String,
    val audioResName: String, // Resource name (e.g., "winter_theme")
    val emoji: String = "", // Emoji associated with the sound effect
    @Transient val audioResId: Int = 0 // Resolved at runtime
) {
    fun withResolvedId(resId: Int) = copy(audioResId = resId)
}

