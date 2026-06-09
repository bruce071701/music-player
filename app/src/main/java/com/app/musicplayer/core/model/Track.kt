package com.app.musicplayer.core.model

data class Track(
    val id: Long = 0,
    val source: TrackSource = TrackSource.LOCAL,
    val title: String,
    val artist: String? = null,
    val albumArtist: String? = null,
    val album: String? = null,
    val durationMs: Long = 0,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val year: Int? = null,
    val genre: String? = null,
    val filePath: String? = null,
    val youtubeId: String? = null,
    val coverUri: String? = null,
    val bitrate: Int? = null,
    val sampleRate: Int? = null,
    val fileSize: Long? = null,
    val replayGainTrack: Float? = null,
    val replayGainAlbum: Float? = null,
    val rating: Int = 0,
    val playCount: Int = 0,
    val lastPlayedAt: Long? = null,
    val isFavorite: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
) {
    val displayArtist: String
        get() = artist ?: "Unknown Artist"

    val displayAlbum: String
        get() = album ?: "Unknown Album"

    val durationFormatted: String
        get() {
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}

enum class TrackSource {
    LOCAL, YOUTUBE
}
