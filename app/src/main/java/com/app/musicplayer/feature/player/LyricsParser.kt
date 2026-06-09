package com.app.musicplayer.feature.player

import java.io.File

/**
 * LRC lyrics file parser.
 * Supports standard LRC format: [mm:ss.xx]text
 */
object LyricsParser {

    private val TIME_TAG_REGEX = Regex("""\[(\d{1,2}):(\d{2})\.?(\d{0,3})]""")
    private val METADATA_TAG_REGEX = Regex("""\[(ti|ar|al|by|offset):(.+)]""")

    /**
     * Parse LRC content string into list of LyricLine.
     */
    fun parseLrc(content: String): List<LyricLine> {
        val lines = mutableListOf<LyricLine>()

        content.lines().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty()) return@forEach

            // Skip metadata tags
            if (METADATA_TAG_REGEX.matches(trimmed)) return@forEach

            // Find all time tags in this line
            val timeTags = TIME_TAG_REGEX.findAll(trimmed)
            val text = trimmed.replace(TIME_TAG_REGEX, "").trim()

            if (text.isEmpty()) return@forEach

            timeTags.forEach { match ->
                val minutes = match.groupValues[1].toLongOrNull() ?: 0
                val seconds = match.groupValues[2].toLongOrNull() ?: 0
                val millisStr = match.groupValues[3]
                val millis = when (millisStr.length) {
                    0 -> 0L
                    1 -> millisStr.toLong() * 100
                    2 -> millisStr.toLong() * 10
                    3 -> millisStr.toLong()
                    else -> 0L
                }

                val timeMs = minutes * 60 * 1000 + seconds * 1000 + millis
                lines.add(LyricLine(timeMs, text))
            }
        }

        return lines.sortedBy { it.timeMs }
    }

    /**
     * Parse LRC file from disk.
     */
    fun parseLrcFile(filePath: String): List<LyricLine> {
        val file = File(filePath)
        if (!file.exists()) return emptyList()
        return try {
            parseLrc(file.readText())
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Find .lrc file that matches the audio file.
     * Looks for files with the same name but .lrc extension.
     */
    fun findLrcFile(audioFilePath: String): String? {
        val audioFile = File(audioFilePath)
        val lrcFile = File(audioFile.parent, audioFile.nameWithoutExtension + ".lrc")
        return if (lrcFile.exists()) lrcFile.absolutePath else null
    }
}
