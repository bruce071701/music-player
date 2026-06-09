package com.app.musicplayer.feature.library.scanner

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.app.musicplayer.core.database.dao.TrackDao
import com.app.musicplayer.core.database.entity.TrackEntity
import com.app.musicplayer.core.datastore.AppPreferences
import com.app.musicplayer.core.datastore.ScanMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class ScanProgress(
    val isScanning: Boolean = false,
    val scannedCount: Int = 0,
    val totalCount: Int = 0,
    val lastScanResult: Int = -1 // -1 = no scan yet, 0+ = tracks found last scan
)

@Singleton
class MediaScanner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackDao: TrackDao,
    private val preferences: AppPreferences,
    private val metadataExtractor: MetadataExtractor
) {
    private val _scanProgress = MutableStateFlow(ScanProgress())
    val scanProgress: StateFlow<ScanProgress> = _scanProgress.asStateFlow()

    suspend fun scanLocalMusic() = withContext(Dispatchers.IO) {
        _scanProgress.value = ScanProgress(isScanning = true)
        android.util.Log.d("MediaScanner", "Starting scan...")
        val startTime = System.currentTimeMillis()

        try {
            val tracks = queryMediaStore()
            android.util.Log.d("MediaScanner", "MediaStore returned ${tracks.size} tracks")
            
            val filteredTracks = applyFilters(tracks)
            android.util.Log.d("MediaScanner", "After filtering: ${filteredTracks.size} tracks")

            _scanProgress.value = _scanProgress.value.copy(totalCount = filteredTracks.size)

            if (filteredTracks.isEmpty()) {
                android.util.Log.d("MediaScanner", "No tracks to insert")
                // Show progress for at least 1 second so user can see it
                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed < 1000) kotlinx.coroutines.delay(1000 - elapsed)
                _scanProgress.value = ScanProgress(isScanning = false, scannedCount = 0, totalCount = 0, lastScanResult = 0)
                return@withContext
            }

            // Phase 1: Quick insert basic track data so UI shows results immediately
            filteredTracks.chunked(100).forEachIndexed { batchIndex, batch ->
                trackDao.insertTracks(batch)
                val scanned = ((batchIndex + 1) * 100).coerceAtMost(filteredTracks.size)
                _scanProgress.value = _scanProgress.value.copy(scannedCount = scanned)
            }
            android.util.Log.d("MediaScanner", "Phase 1 complete: inserted ${filteredTracks.size} tracks")

            // Phase 2: Enrich metadata in background (genre, bitrate, ReplayGain)
            try {
                filteredTracks.chunked(20).forEach { batch ->
                    try {
                        val enrichedBatch = metadataExtractor.extractBatchMetadata(batch)
                        trackDao.insertTracks(enrichedBatch)
                    } catch (_: Exception) { }
                }
                android.util.Log.d("MediaScanner", "Phase 2 complete: metadata enriched")
            } catch (_: Exception) {
                android.util.Log.d("MediaScanner", "Phase 2 skipped due to error")
            }

            // Remove tracks whose files no longer exist
            cleanupDeletedFiles()
        } catch (e: Exception) {
            android.util.Log.e("MediaScanner", "Scan failed", e)
        } finally {
            val total = _scanProgress.value.totalCount
            // Ensure progress bar is visible for at least 1.5 seconds
            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed < 1500) kotlinx.coroutines.delay(1500 - elapsed)
            _scanProgress.value = ScanProgress(isScanning = false, scannedCount = 0, totalCount = 0, lastScanResult = total)
            android.util.Log.d("MediaScanner", "Scan finished. Total: $total")
        }
    }

    private fun queryMediaStore(): List<TrackEntity> {
        val tracks = mutableListOf<TrackEntity>()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DATE_ADDED
        )

        // Query all audio files, not just those tagged as IS_MUSIC
        val selection = "${MediaStore.Audio.Media.DURATION} > 0"

        try {
            context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                "${MediaStore.Audio.Media.TITLE} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumArtistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ARTIST)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
                val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

                while (cursor.moveToNext()) {
                    try {
                        val id = cursor.getLong(idColumn)
                        val title = cursor.getString(titleColumn) ?: "Unknown"
                        val artist = cursor.getString(artistColumn)?.takeIf { it != "<unknown>" }
                        val album = cursor.getString(albumColumn)?.takeIf { it != "<unknown>" }
                        val albumArtist = if (albumArtistColumn >= 0) cursor.getString(albumArtistColumn) else null
                        val duration = cursor.getLong(durationColumn)
                        val trackNumber = cursor.getInt(trackColumn)
                        val year = cursor.getInt(yearColumn).takeIf { it > 0 }
                        val filePath = cursor.getString(dataColumn)
                        val fileSize = cursor.getLong(sizeColumn)
                        val dateAdded = cursor.getLong(dateAddedColumn) * 1000 // seconds to ms

                        // Skip if no valid file path
                        if (filePath.isNullOrBlank()) continue

                        // Get album art URI using content URI (works with scoped storage)
                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        ).toString()

                        tracks.add(
                            TrackEntity(
                                title = title,
                                artist = artist,
                                albumArtist = albumArtist,
                                album = album,
                                durationMs = duration,
                                trackNumber = trackNumber,
                                year = year,
                                filePath = filePath,
                                coverUri = contentUri,
                                fileSize = fileSize,
                                addedAt = dateAdded
                            )
                        )
                    } catch (e: Exception) {
                        // Skip this track on error, continue with next
                        continue
                    }
                }
            }
        } catch (e: SecurityException) {
            // Permission denied - return empty list
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tracks
    }

    private suspend fun applyFilters(tracks: List<TrackEntity>): List<TrackEntity> {
        val minDuration = preferences.minTrackDurationSec.first() * 1000L
        val scanMode = preferences.scanMode.first()
        val blacklist = preferences.blacklistFolders.first()
        val whitelist = preferences.whitelistFolders.first()

        return tracks.filter { track ->
            // Duration filter
            if (track.durationMs < minDuration) return@filter false

            // Folder filter
            val path = track.filePath ?: return@filter false
            when (scanMode) {
                ScanMode.BLACKLIST -> blacklist.none { path.startsWith(it) }
                ScanMode.WHITELIST -> whitelist.any { path.startsWith(it) }
                ScanMode.ALL -> true
            }
        }
    }

    private suspend fun cleanupDeletedFiles() {
        val existingPaths = trackDao.getAllLocalFilePaths()
        existingPaths.filterNotNull().forEach { path ->
            val file = java.io.File(path)
            if (!file.exists()) {
                trackDao.deleteByFilePath(path)
            }
        }
    }
}
