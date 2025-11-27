package com.alf.app_root_music_player.data.repository

import android.content.Context

object ResourceResolver {
    /**
     * Resolves a resource name to its resource ID for raw audio files
     */
    fun resolveRawResource(context: Context, resName: String): Int {
        return context.resources.getIdentifier(resName, "raw", context.packageName)
    }

    /**
     * Resolves a resource name to its resource ID for drawable images
     */
    fun resolveDrawableResource(context: Context, resName: String): Int {
        return context.resources.getIdentifier(resName, "drawable", context.packageName)
    }

    /**
     * Resolves a resource name to its resource ID for mipmap icons
     */
    fun resolveMipmapResource(context: Context, resName: String): Int {
        return context.resources.getIdentifier(resName, "mipmap", context.packageName)
    }
}

