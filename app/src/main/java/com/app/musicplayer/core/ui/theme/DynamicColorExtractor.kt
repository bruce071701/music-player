package com.app.musicplayer.core.ui.theme

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette

/**
 * Extracts dominant colors from album art for dynamic theming.
 * Used in the full-screen player background gradient.
 */
object DynamicColorExtractor {

    data class AlbumColors(
        val dominantColor: Color = Color(0xFF1A1C1E),
        val vibrantColor: Color = Color(0xFF1A56DB),
        val mutedColor: Color = Color(0xFF44474E)
    )

    fun extractColors(bitmap: Bitmap?): AlbumColors {
        if (bitmap == null) return AlbumColors()

        val palette = Palette.from(bitmap)
            .maximumColorCount(16)
            .generate()

        return AlbumColors(
            dominantColor = palette.getDominantColor(0xFF1A1C1E.toInt()).toComposeColor(),
            vibrantColor = palette.getVibrantColor(
                palette.getLightVibrantColor(0xFF1A56DB.toInt())
            ).toComposeColor(),
            mutedColor = palette.getMutedColor(
                palette.getDarkMutedColor(0xFF44474E.toInt())
            ).toComposeColor()
        )
    }

    private fun Int.toComposeColor(): Color = Color(this)
}
