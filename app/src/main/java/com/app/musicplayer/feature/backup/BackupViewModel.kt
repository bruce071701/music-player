package com.app.musicplayer.feature.backup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.app.musicplayer.core.database.dao.EqPresetDao
import com.app.musicplayer.core.database.dao.PlaylistDao
import com.app.musicplayer.core.database.dao.TrackDao
import com.app.musicplayer.core.database.entity.EqPresetEntity
import com.app.musicplayer.core.database.entity.PlaylistEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Serializable
data class BackupData(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val playlists: List<PlaylistBackup> = emptyList(),
    val favorites: List<String> = emptyList(), // file paths of favorite tracks
    val eqPresets: List<EqPresetBackup> = emptyList(),
    val settings: Map<String, String> = emptyMap(),
    val musicFiles: List<String> = emptyList() // file names included in zip
)

@Serializable
data class PlaylistBackup(
    val name: String,
    val trackPaths: List<String> = emptyList()
)

@Serializable
data class EqPresetBackup(
    val name: String,
    val bandsJson: String,
    val preamp: Float
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val trackDao: TrackDao,
    private val playlistDao: PlaylistDao,
    private val eqPresetDao: EqPresetDao
) : ViewModel() {

    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    /**
     * Export backup (playlists + settings + music files) as a zip to the given URI.
     * The URI typically points to Google Drive via SAF.
     */
    suspend fun exportBackup(context: Context, uri: Uri, includeMusicFiles: Boolean = true): Boolean = withContext(Dispatchers.IO) {
        try {
            // Collect playlists
            val playlists = playlistDao.getAllPlaylists().first().map { playlist ->
                val trackIds = playlistDao.getTrackIdsForPlaylist(playlist.id)
                val trackPaths = trackIds.mapNotNull { id ->
                    trackDao.getTrackById(id)?.filePath
                }
                PlaylistBackup(
                    name = playlist.name,
                    trackPaths = trackPaths
                )
            }

            // Collect favorites
            val allTracksRaw = trackDao.getAllLocalTracks().first()
            val favorites = allTracksRaw.filter { it.isFavorite }.mapNotNull { it.filePath }

            // Collect EQ presets
            val eqPresets = eqPresetDao.getAllPresets().first()
                .filter { !it.isBuiltin }
                .map { EqPresetBackup(it.name, it.bandsJson, it.preamp) }

            // Collect all music file paths
            val musicFilePaths = if (includeMusicFiles) {
                allTracksRaw.mapNotNull { it.filePath }.filter { java.io.File(it).exists() }
            } else emptyList()

            android.util.Log.d("Backup", "Playlists: ${playlists.size}, Favorites: ${favorites.size}, Tracks: ${musicFilePaths.size}")

            val backupData = BackupData(
                playlists = playlists,
                favorites = favorites,
                eqPresets = eqPresets,
                musicFiles = musicFilePaths.map { it.substringAfterLast("/") }
            )

            val jsonString = json.encodeToString(backupData)

            // Write as zip: metadata.json + music files
            context.contentResolver.openOutputStream(uri)?.use { output ->
                java.util.zip.ZipOutputStream(output).use { zip ->
                    // Add metadata
                    zip.putNextEntry(java.util.zip.ZipEntry("metadata.json"))
                    zip.write(jsonString.toByteArray(Charsets.UTF_8))
                    zip.closeEntry()

                    // Add music files
                    if (includeMusicFiles) {
                        var fileCount = 0
                        musicFilePaths.forEach { path ->
                            val file = java.io.File(path)
                            if (file.exists() && file.length() > 0) {
                                try {
                                    zip.putNextEntry(java.util.zip.ZipEntry("music/${file.name}"))
                                    file.inputStream().use { it.copyTo(zip) }
                                    zip.closeEntry()
                                    fileCount++
                                } catch (e: Exception) {
                                    android.util.Log.e("Backup", "Failed to add: $path", e)
                                }
                            }
                        }
                        android.util.Log.d("Backup", "Added $fileCount music files to zip")
                    }
                }
            } ?: return@withContext false

            true
        } catch (e: Exception) {
            android.util.Log.e("Backup", "Export failed", e)
            false
        }
    }

    /**
     * Export metadata only (no music files) as JSON.
     */
    suspend fun exportMetadataOnly(context: Context, uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val playlists = playlistDao.getAllPlaylists().first().map { playlist ->
                val trackIds = playlistDao.getTrackIdsForPlaylist(playlist.id)
                val trackPaths = trackIds.mapNotNull { id ->
                    trackDao.getTrackById(id)?.filePath
                }
                PlaylistBackup(name = playlist.name, trackPaths = trackPaths)
            }
            val favorites = trackDao.getFavorites().first().mapNotNull { it.filePath }
            val eqPresets = eqPresetDao.getAllPresets().first()
                .filter { !it.isBuiltin }
                .map { EqPresetBackup(it.name, it.bandsJson, it.preamp) }

            val backupData = BackupData(
                playlists = playlists,
                favorites = favorites,
                eqPresets = eqPresets
            )

            context.contentResolver.openOutputStream(uri)?.use { output ->
                output.write(json.encodeToString(backupData).toByteArray(Charsets.UTF_8))
            } ?: return@withContext false
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Import backup from a zip or JSON file.
     * Returns number of playlists restored.
     */
    suspend fun importBackup(context: Context, uri: Uri): Int = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext 0
            
            // Read first 2 bytes to detect zip
            val allBytes = inputStream.readBytes()
            inputStream.close()
            
            if (allBytes.size < 2) return@withContext 0
            
            val isZip = allBytes[0] == 0x50.toByte() && allBytes[1] == 0x4B.toByte()

            if (isZip) {
                restoreFromZip(context, java.io.ByteArrayInputStream(allBytes))
            } else {
                val jsonString = String(allBytes, Charsets.UTF_8)
                restoreFromJson(jsonString)
            }
        } catch (e: Exception) {
            android.util.Log.e("Backup", "Import failed", e)
            0
        }
    }

    private suspend fun restoreFromZip(context: Context, inputStream: java.io.InputStream): Int {
        var restoredCount = 0
        val musicDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_MUSIC)
            ?: context.filesDir

        try {
            java.util.zip.ZipInputStream(inputStream).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    when {
                        entry.name == "metadata.json" -> {
                            // Read entry content properly without closing the zip stream
                            val buffer = java.io.ByteArrayOutputStream()
                            val buf = ByteArray(4096)
                            var len: Int
                            while (zip.read(buf).also { len = it } > 0) {
                                buffer.write(buf, 0, len)
                            }
                            val jsonString = buffer.toString(Charsets.UTF_8.name())
                            restoredCount = restoreFromJson(jsonString)
                        }
                        entry.name.startsWith("music/") && !entry.isDirectory -> {
                            val fileName = entry.name.substringAfterLast("/")
                            if (fileName.isNotBlank()) {
                                val outFile = java.io.File(musicDir, fileName)
                                if (!outFile.exists()) {
                                    try {
                                        outFile.outputStream().use { out ->
                                            val buf = ByteArray(8192)
                                            var len: Int
                                            while (zip.read(buf).also { len = it } > 0) {
                                                out.write(buf, 0, len)
                                            }
                                        }
                                    } catch (_: Exception) {
                                        outFile.delete()
                                    }
                                }
                            }
                        }
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("Backup", "Zip restore failed", e)
        }

        return restoredCount
    }

    private suspend fun restoreFromJson(jsonString: String): Int {
        val backupData = json.decodeFromString<BackupData>(jsonString)
        var restoredCount = 0

        // Restore playlists
        backupData.playlists.forEach { playlistBackup ->
            val playlistId = playlistDao.insertPlaylist(
                PlaylistEntity(name = playlistBackup.name)
            )
            playlistBackup.trackPaths.forEach { path ->
                trackDao.getTrackByFilePath(path)?.let { track ->
                    playlistDao.addTrackToPlaylist(playlistId, track.id)
                }
            }
            restoredCount++
        }

        // Restore favorites
        backupData.favorites.forEach { path ->
            trackDao.getTrackByFilePath(path)?.let { track ->
                if (!track.isFavorite) {
                    trackDao.toggleFavorite(track.id)
                }
            }
        }

        // Restore EQ presets
        backupData.eqPresets.forEach { preset ->
            eqPresetDao.insertPreset(
                EqPresetEntity(
                    name = preset.name,
                    isBuiltin = false,
                    bandsJson = preset.bandsJson,
                    preamp = preset.preamp
                )
            )
        }

        return restoredCount
    }
}
