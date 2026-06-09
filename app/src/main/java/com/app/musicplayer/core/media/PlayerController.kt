package com.app.musicplayer.core.media

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.app.musicplayer.core.model.PlayMode
import com.app.musicplayer.core.model.Track
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0,
    val durationMs: Long = 0,
    val playbackSpeed: Float = 1.0f
)

@Singleton
class PlayerController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val queueManager: PlayQueueManager
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun initialize() {
        // Release any existing connection first (handles Activity recreation after language change)
        if (mediaController != null) {
            mediaController?.removeListener(playerListener)
            controllerFuture?.let { MediaController.releaseFuture(it) }
            mediaController = null
            controllerFuture = null
        }

        val sessionToken = SessionToken(context, ComponentName(context, MusicPlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.let {
                if (it.isDone && !it.isCancelled) it.get() else null
            }
            mediaController?.addListener(playerListener)

            // Sync state from existing session (if music was already playing)
            mediaController?.let { controller ->
                _isPlaying.value = controller.isPlaying
                if (controller.mediaItemCount > 0) {
                    updatePlaybackState()
                }
            }

            executePendingPlay()
        }, MoreExecutors.directExecutor())
    }

    fun release() {
        mediaController?.removeListener(playerListener)
        controllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController = null
    }

    fun play(track: Track, queue: List<Track>) {
        val startIndex = queue.indexOfFirst { it.id == track.id }.coerceAtLeast(0)
        queueManager.setQueue(queue, startIndex)
        _currentTrack.value = track

        mediaController?.let { controller ->
            val mediaItems = queue.map { it.toMediaItem() }
            controller.setMediaItems(mediaItems, startIndex, 0L)
            controller.prepare()
            controller.play()
        } ?: run {
            // Controller not ready yet, retry after connection
            pendingPlay = PendingPlayAction(track, queue, startIndex)
        }
    }

    private data class PendingPlayAction(val track: Track, val queue: List<Track>, val startIndex: Int)
    private var pendingPlay: PendingPlayAction? = null

    private fun executePendingPlay() {
        pendingPlay?.let { pending ->
            mediaController?.let { controller ->
                val mediaItems = pending.queue.map { it.toMediaItem() }
                controller.setMediaItems(mediaItems, pending.startIndex, 0L)
                controller.prepare()
                controller.play()
            }
            pendingPlay = null
        }
    }

    fun playPause() {
        mediaController?.let { controller ->
            if (controller.isPlaying) {
                controller.pause()
            } else {
                controller.play()
            }
        }
    }

    fun next() {
        val nextTrack = queueManager.next()
        if (nextTrack != null) {
            _currentTrack.value = nextTrack
            mediaController?.seekToNextMediaItem()
        }
    }

    fun previous() {
        mediaController?.let { controller ->
            if (controller.currentPosition > 3000) {
                controller.seekTo(0)
            } else {
                val prevTrack = queueManager.previous()
                if (prevTrack != null) {
                    _currentTrack.value = prevTrack
                    controller.seekToPreviousMediaItem()
                }
            }
        }
    }

    fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
    }

    fun setPlayMode(mode: PlayMode) {
        queueManager.setPlayMode(mode)
        mediaController?.let { controller ->
            when (mode) {
                PlayMode.REPEAT_ONE -> {
                    controller.repeatMode = Player.REPEAT_MODE_ONE
                    controller.shuffleModeEnabled = false
                }
                PlayMode.REPEAT_ALL -> {
                    controller.repeatMode = Player.REPEAT_MODE_ALL
                    controller.shuffleModeEnabled = false
                }
                PlayMode.SHUFFLE -> {
                    controller.repeatMode = Player.REPEAT_MODE_ALL
                    controller.shuffleModeEnabled = true
                }
                PlayMode.SEQUENCE -> {
                    controller.repeatMode = Player.REPEAT_MODE_OFF
                    controller.shuffleModeEnabled = false
                }
            }
        }
    }

    fun setSpeed(speed: Float) {
        mediaController?.setPlaybackSpeed(speed)
    }

    fun getCurrentPosition(): Long = mediaController?.currentPosition ?: 0
    fun getDuration(): Long = mediaController?.duration ?: 0

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            updatePlaybackState()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            updatePlaybackState()
            if (playbackState == Player.STATE_ENDED) {
                next()
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            _currentTrack.value = queueManager.currentTrack
            updatePlaybackState()
        }
    }

    private fun updatePlaybackState() {
        mediaController?.let { controller ->
            _playbackState.value = PlaybackState(
                isPlaying = controller.isPlaying,
                currentPositionMs = controller.currentPosition,
                durationMs = controller.duration.coerceAtLeast(0),
                playbackSpeed = controller.playbackParameters.speed
            )
        }
    }

    private fun Track.toMediaItem(): MediaItem {
        val uri = when {
            filePath != null -> android.net.Uri.fromFile(java.io.File(filePath)).toString()
            else -> ""
        }
        return MediaItem.Builder()
            .setMediaId(id.toString())
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setArtworkUri(coverUri?.let { android.net.Uri.parse(it) })
                    .build()
            )
            .build()
    }
}
