package com.app.musicplayer.feature.library.scanner

import android.os.FileObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

sealed class MediaChangeEvent {
    data class FileAdded(val path: String) : MediaChangeEvent()
    data class FileDeleted(val path: String) : MediaChangeEvent()
    data class FileMoved(val path: String) : MediaChangeEvent()
}

/**
 * Monitors directories for media file changes.
 * Uses Android FileObserver to detect file creation/deletion.
 * Debounces events to batch process multiple rapid changes.
 */
@Singleton
class MediaFileObserver @Inject constructor() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _events = Channel<MediaChangeEvent>(Channel.BUFFERED)
    val events: Flow<MediaChangeEvent> = _events.receiveAsFlow()

    private val observers = mutableListOf<FileObserver>()

    private val audioExtensions = setOf(
        "mp3", "flac", "ogg", "m4a", "aac", "wav", "opus",
        "wma", "ape", "alac", "aiff", "dsf", "dff", "wv"
    )

    fun startWatching(folders: List<String>) {
        stopWatching()

        folders.forEach { folderPath ->
            val dir = File(folderPath)
            if (!dir.exists() || !dir.isDirectory) return@forEach

            val observer = object : FileObserver(
                dir,
                CREATE or DELETE or MOVED_FROM or MOVED_TO
            ) {
                override fun onEvent(event: Int, path: String?) {
                    if (path == null) return
                    val fullPath = "$folderPath/$path"

                    // Only process audio files
                    val extension = path.substringAfterLast(".", "").lowercase()
                    if (extension !in audioExtensions) return

                    val changeEvent = when (event) {
                        CREATE, MOVED_TO -> MediaChangeEvent.FileAdded(fullPath)
                        DELETE, MOVED_FROM -> MediaChangeEvent.FileDeleted(fullPath)
                        else -> return
                    }

                    scope.launch {
                        // Small debounce for rapid filesystem events
                        delay(500)
                        _events.send(changeEvent)
                    }
                }
            }

            observer.startWatching()
            observers.add(observer)
        }
    }

    fun stopWatching() {
        observers.forEach { it.stopWatching() }
        observers.clear()
    }
}
