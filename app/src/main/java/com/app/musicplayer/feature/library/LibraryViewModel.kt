package com.app.musicplayer.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.musicplayer.core.database.dao.TrackDao
import com.app.musicplayer.core.database.mapper.toDomainModels
import com.app.musicplayer.core.model.Track
import com.app.musicplayer.feature.library.scanner.MediaScanner
import com.app.musicplayer.feature.library.scanner.ScanProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val trackDao: TrackDao,
    private val playlistDao: com.app.musicplayer.core.database.dao.PlaylistDao,
    private val mediaScanner: MediaScanner,
    private val preferences: com.app.musicplayer.core.datastore.AppPreferences
) : ViewModel() {

    val allTracks: StateFlow<List<Track>> = trackDao.getAllLocalTracks()
        .map { it.toDomainModels() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val artists: StateFlow<List<String>> = trackDao.getAllArtists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val albums: StateFlow<List<String>> = trackDao.getAllAlbums()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val genres: StateFlow<List<String>> = trackDao.getAllGenres()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scanProgress: StateFlow<ScanProgress> = mediaScanner.scanProgress

    val userPlaylists: StateFlow<List<com.app.musicplayer.core.database.entity.PlaylistEntity>> = playlistDao.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _folders = MutableStateFlow<List<String>>(emptyList())
    val folders: StateFlow<List<String>> = _folders.asStateFlow()

    init {
        loadFolders()
    }

    fun scanLibrary() {
        viewModelScope.launch {
            mediaScanner.scanLocalMusic()
            loadFolders()
        }
    }

    fun scanWithOptions(minDurationSec: Int, minSizeKb: Int) {
        viewModelScope.launch {
            // Update preferences before scanning
            preferences.setMinTrackDurationSec(minDurationSec)
            mediaScanner.scanLocalMusic()
            loadFolders()
        }
    }

    private fun loadFolders() {
        viewModelScope.launch {
            trackDao.getAllLocalTracks().collect { tracks ->
                val folderSet = tracks.mapNotNull { it.filePath }
                    .map { it.substringBeforeLast("/") }
                    .distinct()
                    .sorted()
                _folders.value = folderSet
            }
        }
    }

    fun getTracksByArtist(artist: String) = trackDao.getTracksByArtist(artist)
        .map { it.toDomainModels() }

    fun getTracksByAlbum(album: String) = trackDao.getTracksByAlbum(album)
        .map { it.toDomainModels() }

    fun getTracksByGenre(genre: String) = trackDao.getTracksByGenre(genre)
        .map { it.toDomainModels() }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistDao.insertPlaylist(
                com.app.musicplayer.core.database.entity.PlaylistEntity(name = name)
            )
        }
    }

    /**
     * Restore playlists from a user-selected folder containing .m3u/.m3u8/.pls files.
     */
    suspend fun restorePlaylistsFromFolder(context: android.content.Context, treeUri: android.net.Uri): Int {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val docDir = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, treeUri)
                    ?: return@withContext 0

                val playlistExtensions = setOf("m3u", "m3u8", "pls", "wpl")
                val playlistFiles = docDir.listFiles().filter { file ->
                    file.isFile && file.name?.substringAfterLast(".")?.lowercase() in playlistExtensions
                }

                if (playlistFiles.isEmpty()) return@withContext 0

                var count = 0
                playlistFiles.forEach { file ->
                    try {
                        val inputStream = context.contentResolver.openInputStream(file.uri) ?: return@forEach
                        val lines = inputStream.bufferedReader().readLines()
                        inputStream.close()

                        val trackPaths = lines.filter { it.isNotBlank() && !it.startsWith("#") }.map { it.trim() }
                        if (trackPaths.isEmpty()) return@forEach

                        val playlistName = (file.name ?: "Playlist").substringBeforeLast(".").replace("_", " ").replace("-", " ")

                        val existing = playlistDao.getAllPlaylists().first()
                        if (existing.any { it.name.equals(playlistName, ignoreCase = true) }) return@forEach

                        val playlistId = playlistDao.insertPlaylist(
                            com.app.musicplayer.core.database.entity.PlaylistEntity(name = playlistName)
                        )

                        trackPaths.forEach { path ->
                            val track = trackDao.getTrackByFilePath(path)
                            if (track != null) {
                                playlistDao.addTrackToPlaylist(playlistId, track.id)
                            } else {
                                val fileName = path.substringAfterLast("/")
                                val allPaths = trackDao.getAllLocalFilePaths()
                                allPaths.filterNotNull().find { it.endsWith("/$fileName") }?.let { matchedPath ->
                                    trackDao.getTrackByFilePath(matchedPath)?.let {
                                        playlistDao.addTrackToPlaylist(playlistId, it.id)
                                    }
                                }
                            }
                        }
                        count++
                    } catch (_: Exception) { }
                }
                count
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }
    }

    /**
     * Restore playlists by scanning common playlist folders on device.
     * Looks for .m3u/.m3u8 files in known locations.
     */
    suspend fun restorePlaylistsFromDevice(): Int {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            var count = 0
            val playlistDirs = listOf(
                "/storage/emulated/0/Playlists",
                "/storage/emulated/0/Music/Playlists",
                "/storage/emulated/0/Download/Playlists",
                "/sdcard/Playlists",
                "/sdcard/Music/Playlists"
            )

            val playlistExtensions = setOf("m3u", "m3u8", "pls", "wpl")

            playlistDirs.forEach { dirPath ->
                val dir = java.io.File(dirPath)
                if (dir.exists() && dir.isDirectory) {
                    dir.listFiles()?.filter { file ->
                        file.isFile && file.extension.lowercase() in playlistExtensions
                    }?.forEach { file ->
                        val imported = importM3uFile(file)
                        if (imported) count++
                    }
                }
            }

            // Also scan the app's external files directory
            try {
                val context = android.app.Application()
                // We'll use Environment to get external storage
                val externalDir = android.os.Environment.getExternalStorageDirectory()
                val musicDir = java.io.File(externalDir, "Music")
                musicDir.listFiles()?.filter { file ->
                    file.isFile && file.extension.lowercase() in playlistExtensions
                }?.forEach { file ->
                    val imported = importM3uFile(file)
                    if (imported) count++
                }
            } catch (_: Exception) { }

            count
        }
    }

    private suspend fun importM3uFile(file: java.io.File): Boolean {
        return try {
            val lines = file.readLines()
            val trackPaths = lines.filter { it.isNotBlank() && !it.startsWith("#") }.map { it.trim() }
            if (trackPaths.isEmpty()) return false

            val playlistName = file.nameWithoutExtension.replace("_", " ").replace("-", " ")

            // Check if playlist with same name already exists
            val existing = playlistDao.getAllPlaylists().first()
            if (existing.any { it.name.equals(playlistName, ignoreCase = true) }) return false

            val playlistId = playlistDao.insertPlaylist(
                com.app.musicplayer.core.database.entity.PlaylistEntity(name = playlistName)
            )

            var matchCount = 0
            trackPaths.forEach { path ->
                // Try exact path match
                val track = trackDao.getTrackByFilePath(path)
                if (track != null) {
                    playlistDao.addTrackToPlaylist(playlistId, track.id)
                    matchCount++
                } else {
                    // Try matching by filename
                    val fileName = path.substringAfterLast("/")
                    val allPaths = trackDao.getAllLocalFilePaths()
                    val matchedPath = allPaths.filterNotNull().find { it.endsWith("/$fileName") }
                    if (matchedPath != null) {
                        trackDao.getTrackByFilePath(matchedPath)?.let {
                            playlistDao.addTrackToPlaylist(playlistId, it.id)
                            matchCount++
                        }
                    }
                }
            }
            matchCount > 0
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Import selected tracks into a new playlist.
     */
    fun createPlaylistWithTracks(name: String, trackIds: List<Long>) {
        viewModelScope.launch {
            val playlistId = playlistDao.insertPlaylist(
                com.app.musicplayer.core.database.entity.PlaylistEntity(name = name)
            )
            trackIds.forEach { trackId ->
                playlistDao.addTrackToPlaylist(playlistId, trackId)
            }
        }
    }

    /**
     * Import a playlist from a user-selected folder.
     * Folder name becomes playlist name, all audio files in it become tracks.
     */
    suspend fun importPlaylistFromFolder(context: android.content.Context, treeUri: android.net.Uri): String {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val docUri = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, treeUri)
                    ?: return@withContext "Failed to open folder"

                val folderName = docUri.name ?: "Imported Playlist"

                // Find audio files in the folder
                val audioExtensions = setOf("mp3", "flac", "ogg", "m4a", "aac", "wav", "opus", "wma", "ape", "alac")
                val audioFiles = docUri.listFiles().filter { file ->
                    file.isFile && file.name?.substringAfterLast(".")?.lowercase() in audioExtensions
                }

                if (audioFiles.isEmpty()) {
                    return@withContext "No audio files found in this folder"
                }

                // Create playlist with folder name
                val playlistId = playlistDao.insertPlaylist(
                    com.app.musicplayer.core.database.entity.PlaylistEntity(name = folderName)
                )

                // Match files to existing tracks in database by filename
                var matchCount = 0
                audioFiles.forEach { file ->
                    val fileName = file.name ?: return@forEach
                    // Try to find this file in our scanned library
                    val allPaths = trackDao.getAllLocalFilePaths()
                    val matchedPath = allPaths.filterNotNull().find { it.endsWith("/$fileName") }
                    if (matchedPath != null) {
                        trackDao.getTrackByFilePath(matchedPath)?.let { track ->
                            playlistDao.addTrackToPlaylist(playlistId, track.id)
                            matchCount++
                        }
                    }
                }

                "Imported: $folderName ($matchCount/${audioFiles.size} tracks matched)"
            } catch (e: Exception) {
                e.printStackTrace()
                "Import failed: ${e.message}"
            }
        }
    }

    fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            playlistDao.addTrackToPlaylist(playlistId, trackId)
        }
    }

    fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            playlistDao.removeTrackFromPlaylist(playlistId, trackId)
        }
    }

    fun getPlaylistTracks(playlistId: Long): kotlinx.coroutines.flow.Flow<List<Track>> {
        return playlistDao.getPlaylistTracks(playlistId).map { it.toDomainModels() }
    }
}
