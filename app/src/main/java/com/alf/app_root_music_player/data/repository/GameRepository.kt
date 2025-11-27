package com.alf.app_root_music_player.data.repository

import android.content.Context
import com.alf.app_root_music_player.data.model.Game
import kotlinx.serialization.json.Json
import java.io.IOException

class GameRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    private var games: List<Game>? = null

    /**
     * Loads and parses games.json from assets
     */
    private fun loadGames(): List<Game> {
        if (games != null) return games!!

        return try {
            val jsonString = context.assets.open("games.json")
                .bufferedReader()
                .use { it.readText() }
            
            val gamesList = json.decodeFromString<List<Game>>(jsonString)
            games = gamesList
            gamesList
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Gets all games with resolved resource IDs
     */
    fun getAllGames(): List<Game> {
        return loadGames().map { game ->
            val iconResId = ResourceResolver.resolveDrawableResource(context, game.iconResName)
                .takeIf { it != 0 } 
                ?: ResourceResolver.resolveMipmapResource(context, game.iconResName)
            
            val resolvedSoundtracks = game.soundtracks.map { soundtrack ->
                soundtrack.withResolvedIds(
                    audioResId = ResourceResolver.resolveRawResource(context, soundtrack.audioResName),
                    coverResId = ResourceResolver.resolveDrawableResource(context, soundtrack.coverResName)
                )
            }
            
            val resolvedSoundEffects = game.soundEffects.map { soundEffect ->
                soundEffect.withResolvedId(
                    resId = ResourceResolver.resolveRawResource(context, soundEffect.audioResName)
                )
            }
            
            game.withResolvedIds(iconResId, resolvedSoundtracks, resolvedSoundEffects)
        }
    }

    /**
     * Gets a game by ID with resolved resource IDs
     */
    fun getGameById(id: String): Game? {
        return getAllGames().find { it.id == id }
    }
}

