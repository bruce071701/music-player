package com.app.musicplayer.feature.player

import com.app.musicplayer.core.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages lyrics loading from multiple sources:
 * 1. External .lrc file (same directory, same filename)
 * 2. Embedded lyrics in audio file tags (ID3 LYRICS/USLT)
 * 3. Online search via LRCLIB API (lrclib.net)
 */
@Singleton
class LyricsManager @Inject constructor() {

    init {
        Logger.getLogger("org.jaudiotagger").level = Level.OFF
    }

    /**
     * Load lyrics for a track, trying all sources in order.
     */
    suspend fun loadLyrics(track: Track): List<LyricLine> = withContext(Dispatchers.IO) {
        // 1. Try external .lrc file
        val lrcFromFile = loadFromLrcFile(track)
        if (lrcFromFile.isNotEmpty()) return@withContext lrcFromFile

        // 2. Try embedded lyrics from audio tags
        val embedded = loadFromEmbeddedTags(track)
        if (embedded.isNotEmpty()) return@withContext embedded

        // 3. Try online search (LRCLIB)
        val online = searchOnline(track)
        if (online.isNotEmpty()) return@withContext online

        emptyList()
    }

    /**
     * Source 1: Find and parse .lrc file next to the audio file.
     */
    private fun loadFromLrcFile(track: Track): List<LyricLine> {
        val audioPath = track.filePath ?: return emptyList()
        val lrcPath = LyricsParser.findLrcFile(audioPath) ?: return emptyList()
        return LyricsParser.parseLrcFile(lrcPath)
    }

    /**
     * Source 2: Extract lyrics embedded in audio file metadata.
     * Supports ID3v2 USLT (unsynced lyrics) and SYLT (synced lyrics),
     * Vorbis Comment LYRICS field, etc.
     */
    private fun loadFromEmbeddedTags(track: Track): List<LyricLine> {
        val filePath = track.filePath ?: return emptyList()
        val file = File(filePath)
        if (!file.exists()) return emptyList()

        return try {
            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tag ?: return emptyList()

            // Try LYRICS field (synced LRC format sometimes stored here)
            val lyrics = tag.getFirst(FieldKey.LYRICS)
            if (lyrics.isNullOrBlank()) return emptyList()

            // Check if it's LRC format (has time tags)
            if (lyrics.contains("[") && lyrics.contains("]")) {
                LyricsParser.parseLrc(lyrics)
            } else {
                // Plain text lyrics - show as unsynced (all at time 0 with line breaks)
                lyrics.lines()
                    .filter { it.isNotBlank() }
                    .mapIndexed { index, line ->
                        LyricLine(timeMs = index * 3000L, text = line.trim())
                    }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Source 3: Search lyrics online via LRCLIB (free, no auth required).
     * API: https://lrclib.net/api/search?track_name=xxx&artist_name=xxx
     */
    private fun searchOnline(track: Track): List<LyricLine> {
        val title = track.title

        return try {
            val encodedTitle = URLEncoder.encode(title, "UTF-8")
            // Try with artist first, then without
            val urlString = if (!track.artist.isNullOrBlank()) {
                val encodedArtist = URLEncoder.encode(track.artist, "UTF-8")
                "https://lrclib.net/api/search?track_name=$encodedTitle&artist_name=$encodedArtist"
            } else {
                "https://lrclib.net/api/search?track_name=$encodedTitle"
            }

            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "MusicPlayer/1.0 (https://github.com/user/music-player)")
            connection.connectTimeout = 8000
            connection.readTimeout = 8000

            if (connection.responseCode != 200) {
                // Retry without artist
                if (!track.artist.isNullOrBlank()) {
                    return searchOnlineByTitleOnly(title)
                }
                return emptyList()
            }

            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()

            // Parse JSON response - find first result with syncedLyrics
            val syncedLyrics = extractSyncedLyrics(response)
            if (syncedLyrics != null) {
                return LyricsParser.parseLrc(syncedLyrics)
            }

            // Try plain lyrics
            val plainLyrics = extractPlainLyrics(response)
            if (plainLyrics != null) {
                return plainLyrics.lines()
                    .filter { it.isNotBlank() }
                    .mapIndexed { index, line ->
                        LyricLine(timeMs = index * 4000L, text = line.trim())
                    }
            }

            // If nothing found with artist, try without
            if (!track.artist.isNullOrBlank()) {
                return searchOnlineByTitleOnly(title)
            }

            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun searchOnlineByTitleOnly(title: String): List<LyricLine> {
        return try {
            val encodedTitle = URLEncoder.encode(title, "UTF-8")
            val url = URL("https://lrclib.net/api/search?track_name=$encodedTitle")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "MusicPlayer/1.0 (https://github.com/user/music-player)")
            connection.connectTimeout = 8000
            connection.readTimeout = 8000

            if (connection.responseCode != 200) return emptyList()

            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()

            val syncedLyrics = extractSyncedLyrics(response)
            if (syncedLyrics != null) {
                return LyricsParser.parseLrc(syncedLyrics)
            }

            val plainLyrics = extractPlainLyrics(response)
            plainLyrics?.lines()
                ?.filter { it.isNotBlank() }
                ?.mapIndexed { index, line ->
                    LyricLine(timeMs = index * 4000L, text = line.trim())
                } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Simple JSON parsing for syncedLyrics field (avoid adding a JSON dependency just for this).
     */
    private fun extractSyncedLyrics(json: String): String? {
        val key = "\"syncedLyrics\""
        val index = json.indexOf(key)
        if (index == -1) return null
        return extractJsonStringValue(json, index + key.length)
    }

    private fun extractPlainLyrics(json: String): String? {
        val key = "\"plainLyrics\""
        val index = json.indexOf(key)
        if (index == -1) return null
        return extractJsonStringValue(json, index + key.length)
    }

    private fun extractJsonStringValue(json: String, startAfterKey: Int): String? {
        // Find the colon, then the opening quote
        val colonIndex = json.indexOf(':', startAfterKey)
        if (colonIndex == -1) return null

        val afterColon = json.substring(colonIndex + 1).trimStart()
        if (afterColon.startsWith("null")) return null
        if (!afterColon.startsWith("\"")) return null

        // Find the closing quote (handling escape sequences)
        val sb = StringBuilder()
        var i = 1 // skip opening quote
        while (i < afterColon.length) {
            val c = afterColon[i]
            if (c == '\\' && i + 1 < afterColon.length) {
                val next = afterColon[i + 1]
                when (next) {
                    'n' -> sb.append('\n')
                    't' -> sb.append('\t')
                    '"' -> sb.append('"')
                    '\\' -> sb.append('\\')
                    else -> { sb.append(c); sb.append(next) }
                }
                i += 2
            } else if (c == '"') {
                break
            } else {
                sb.append(c)
                i++
            }
        }
        val result = sb.toString()
        return result.ifBlank { null }
    }
}
