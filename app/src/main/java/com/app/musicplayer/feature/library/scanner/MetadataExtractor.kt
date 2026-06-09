package com.app.musicplayer.feature.library.scanner

import com.app.musicplayer.core.database.entity.TrackEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetadataExtractor @Inject constructor() {

    init {
        Logger.getLogger("org.jaudiotagger").level = Level.OFF
    }

    suspend fun extractMetadata(track: TrackEntity): TrackEntity = withContext(Dispatchers.IO) {
        val filePath = track.filePath ?: return@withContext track
        val file = File(filePath)
        if (!file.exists()) return@withContext track

        try {
            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tag ?: return@withContext track
            val header = audioFile.audioHeader

            // Try to get ReplayGain from custom tag fields
            val rgTrack = getTagField(tag, "REPLAYGAIN_TRACK_GAIN")
            val rgAlbum = getTagField(tag, "REPLAYGAIN_ALBUM_GAIN")

            track.copy(
                genre = tag.getFirst(FieldKey.GENRE).takeIf { it.isNotBlank() },
                discNumber = tag.getFirst(FieldKey.DISC_NO).toIntOrNull(),
                trackNumber = tag.getFirst(FieldKey.TRACK).toIntOrNull() ?: track.trackNumber,
                artist = tag.getFirst(FieldKey.ARTIST).takeIf { it.isNotBlank() } ?: track.artist,
                albumArtist = tag.getFirst(FieldKey.ALBUM_ARTIST).takeIf { it.isNotBlank() } ?: track.albumArtist,
                album = tag.getFirst(FieldKey.ALBUM).takeIf { it.isNotBlank() } ?: track.album,
                year = tag.getFirst(FieldKey.YEAR).toIntOrNull() ?: track.year,
                bitrate = header.bitRateAsNumber.toInt(),
                sampleRate = header.sampleRateAsNumber,
                replayGainTrack = extractReplayGain(rgTrack),
                replayGainAlbum = extractReplayGain(rgAlbum)
            )
        } catch (e: Exception) {
            track
        }
    }

    suspend fun extractBatchMetadata(tracks: List<TrackEntity>): List<TrackEntity> {
        return tracks.map { extractMetadata(it) }
    }

    private fun getTagField(tag: org.jaudiotagger.tag.Tag, fieldName: String): String? {
        return try {
            // Try custom tag field for ReplayGain
            tag.getFirst(fieldName).takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }

    private fun extractReplayGain(value: String?): Float? {
        if (value.isNullOrBlank()) return null
        return value.replace("dB", "")
            .replace("db", "")
            .trim()
            .toFloatOrNull()
    }
}
