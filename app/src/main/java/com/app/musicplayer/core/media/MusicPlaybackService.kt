package com.app.musicplayer.core.media

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.app.musicplayer.MainActivity
import com.app.musicplayer.core.datastore.AppPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlaybackService : MediaSessionService() {

    @Inject
    lateinit var preferences: AppPreferences

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Crossfade support
    private var crossfadePlayer: ExoPlayer? = null
    private var crossfadeDurationMs: Int = 0
    private var isCrossfading: Boolean = false

    override fun onCreate() {
        super.onCreate()

        // Create notification channel with custom settings
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                "music_playback",
                "Music Playback",
                android.app.NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for music playback"
                setShowBadge(false)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                /* handleAudioFocus = */ true
            )
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()

        // Apply gapless playback setting
        player.pauseAtEndOfMediaItems = false

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setCallback(object : MediaSession.Callback {
                override fun onConnect(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo
                ): MediaSession.ConnectionResult {
                    val commands = MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon()
                    return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                        .setAvailableSessionCommands(commands.build())
                        .setAvailablePlayerCommands(
                            Player.Commands.Builder()
                                .addAllCommands()
                                .build()
                        )
                        .build()
                }
            })
            .build()

        // Configure media notification appearance
        val notificationProvider = androidx.media3.session.DefaultMediaNotificationProvider.Builder(this)
            .setChannelId("music_playback")
            .setChannelName(com.penji.musicplayer.offline.R.string.app_name)
            .build()
        notificationProvider.setSmallIcon(com.penji.musicplayer.offline.R.drawable.ic_launcher_foreground)
        setMediaNotificationProvider(notificationProvider)

        // Register widget control receiver
        val filter = android.content.IntentFilter().apply {
            addAction(ACTION_PLAY_PAUSE)
            addAction(ACTION_NEXT)
            addAction(ACTION_PREVIOUS)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(widgetControlReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(widgetControlReceiver, filter)
        }

        // Load preferences and apply crossfade
        serviceScope.launch {
            crossfadeDurationMs = preferences.crossfadeDuration.first()
            val gaplessEnabled = preferences.gaplessEnabled.first()

            if (gaplessEnabled) {
                // Gapless is the default ExoPlayer behavior
                // Make sure crossfade is not interfering
                crossfadeDurationMs = 0
            }

            if (crossfadeDurationMs > 0) {
                setupCrossfade()
            }
        }

        // Observe preference changes in real-time
        serviceScope.launch {
            preferences.crossfadeDuration.collect { duration ->
                crossfadeDurationMs = duration
                if (duration > 0) {
                    if (crossfadePlayer == null) setupCrossfade()
                } else {
                    crossfadePlayer?.release()
                    crossfadePlayer = null
                    isCrossfading = false
                    player.volume = 1f
                }
            }
        }
        serviceScope.launch {
            preferences.gaplessEnabled.collect { enabled ->
                if (enabled) {
                    // Disable crossfade when gapless is on
                    crossfadeDurationMs = 0
                    crossfadePlayer?.release()
                    crossfadePlayer = null
                    isCrossfading = false
                    player.volume = 1f
                }
            }
        }

        // Listen for track transitions to handle crossfade
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (crossfadeDurationMs > 0 && playbackState == Player.STATE_READY) {
                    checkCrossfadePosition()
                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                if (crossfadeDurationMs > 0) {
                    // Reset crossfade state on track change
                    isCrossfading = false
                }
            }
        })
    }

    private fun setupCrossfade() {
        // Create a secondary player for crossfade overlap
        crossfadePlayer = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                false // Don't handle audio focus for secondary player
            )
            .build()
    }

    private fun checkCrossfadePosition() {
        if (isCrossfading || crossfadeDurationMs <= 0) return

        val duration = player.duration
        val position = player.currentPosition
        val remaining = duration - position

        // Start crossfade when remaining time equals crossfade duration
        if (duration > 0 && remaining in 1..crossfadeDurationMs.toLong()) {
            startCrossfade()
        }
    }

    private fun startCrossfade() {
        if (isCrossfading) return
        isCrossfading = true

        val nextIndex = player.currentMediaItemIndex + 1
        if (nextIndex >= player.mediaItemCount) return

        val nextItem = player.getMediaItemAt(nextIndex)

        crossfadePlayer?.let { cfPlayer ->
            cfPlayer.setMediaItem(nextItem)
            cfPlayer.prepare()
            cfPlayer.volume = 0f
            cfPlayer.play()

            // Gradually fade volumes
            serviceScope.launch {
                val steps = 20
                val stepDelay = crossfadeDurationMs.toLong() / steps
                for (i in 1..steps) {
                    kotlinx.coroutines.delay(stepDelay)
                    val progress = i.toFloat() / steps
                    player.volume = (1f - progress).coerceIn(0f, 1f)
                    cfPlayer.volume = progress.coerceIn(0f, 1f)
                }

                // Transition complete - skip to next on main player
                player.volume = 1f
                player.seekToNextMediaItem()
                cfPlayer.stop()
                cfPlayer.clearMediaItems()
                isCrossfading = false
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        try { unregisterReceiver(widgetControlReceiver) } catch (_: Exception) {}
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        crossfadePlayer?.release()
        crossfadePlayer = null
        serviceScope.cancel()
        super.onDestroy()
    }

    private val widgetControlReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(ctx: android.content.Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_PLAY_PAUSE -> {
                    if (player.isPlaying) player.pause() else player.play()
                }
                ACTION_NEXT -> {
                    if (player.hasNextMediaItem()) player.seekToNextMediaItem()
                }
                ACTION_PREVIOUS -> {
                    if (player.currentPosition > 3000) {
                        player.seekTo(0)
                    } else if (player.hasPreviousMediaItem()) {
                        player.seekToPreviousMediaItem()
                    }
                }
            }
        }
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "com.penji.musicplayer.offline.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.penji.musicplayer.offline.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.penji.musicplayer.offline.ACTION_PREVIOUS"
    }
}
