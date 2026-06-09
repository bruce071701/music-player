package com.app.musicplayer.core.common

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.penji.musicplayer.offline.R

/**
 * Utility class for formatting strings with localized plurals and numbers
 */
object StringFormatter {
    
    /**
     * Format track count with proper pluralization
     */
    fun formatTrackCount(context: Context, count: Int): String {
        return context.getString(R.string.tracks_count, count)
    }
    
    /**
     * Format artist count with proper pluralization
     */
    fun formatArtistCount(context: Context, count: Int): String {
        return context.getString(R.string.artists_count, count)
    }
    
    /**
     * Format album count with proper pluralization
     */
    fun formatAlbumCount(context: Context, count: Int): String {
        return context.getString(R.string.albums_count, count)
    }
    
    /**
     * Format folder count with proper pluralization
     */
    fun formatFolderCount(context: Context, count: Int): String {
        return context.getString(R.string.folders_count, count)
    }
    
    /**
     * Format video count with proper pluralization
     */
    fun formatVideoCount(context: Context, count: Int): String {
        return context.getString(R.string.videos_count, count)
    }
    
    /**
     * Format duration in minutes and seconds (e.g., "3:45")
     */
    fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
    
    /**
     * Format file size (e.g., "5.2 MB", "1.1 GB")
     */
    fun formatFileSize(context: Context, sizeBytes: Long): String {
        if (sizeBytes == 0L) return "0 B"
        
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(sizeBytes.toDouble()) / Math.log10(1024.0)).toInt()
        
        return String.format(
            "%.1f %s",
            sizeBytes / Math.pow(1024.0, digitGroups.toDouble()),
            units[digitGroups]
        )
    }
    
    /**
     * Get localized empty state message based on content type
     */
    fun getEmptyStateMessage(context: Context, contentType: ContentType): String {
        return when (contentType) {
            ContentType.TRACKS -> context.getString(R.string.no_tracks)
            ContentType.ARTISTS -> context.getString(R.string.no_artists)
            ContentType.ALBUMS -> context.getString(R.string.no_albums)
            ContentType.FOLDERS -> context.getString(R.string.no_folders)
            ContentType.GENRES -> context.getString(R.string.no_genres)
            ContentType.VIDEOS -> context.getString(R.string.no_videos)
        }
    }
    
    enum class ContentType {
        TRACKS, ARTISTS, ALBUMS, FOLDERS, GENRES, VIDEOS
    }
}