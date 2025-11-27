package com.alf.app_root_music_player.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Soundtrack(
    val id: String,
    val title: String,
    val audioResName: String, // Resource name (e.g., "winter_theme")
    val coverResName: String, // Resource name (e.g., "cover_winter")
    @Transient val audioResId: Int = 0, // Resolved at runtime
    @Transient val coverResId: Int = 0 // Resolved at runtime
) {
    fun withResolvedIds(audioResId: Int, coverResId: Int) = copy(
        audioResId = audioResId,
        coverResId = coverResId
    )
}

