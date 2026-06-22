package com.app.musicplayer.feature.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.musicplayer.core.model.PlayMode
import com.app.musicplayer.core.ui.theme.AccentPrimary
import com.app.musicplayer.core.ui.theme.TextSecondary
import com.penji.musicplayer.offline.R
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onDismiss: () -> Unit
) {
    val track by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPositionMs by viewModel.currentPositionMs.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()
    val playMode by viewModel.playMode.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val sleepTimerActive by viewModel.sleepTimer.isActive.collectAsState()

    val duration = playbackState.durationMs
    val progress = if (duration > 0) currentPositionMs.toFloat() / duration.toFloat() else 0f

    var showLyrics by remember { mutableStateOf(false) }
    var showTrackOptions by remember { mutableStateOf(false) }
    var showAddToPlaylistDialog by remember { mutableStateOf(false) }
    var showTrackInfoDialog by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }

    // Dialogs
    if (showLyrics) {
        ModalBottomSheet(
            onDismissRequest = { showLyrics = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            val lyricsData by viewModel.lyrics.collectAsState()
            val isLoadingLyrics by viewModel.isLoadingLyrics.collectAsState()
            androidx.compose.runtime.LaunchedEffect(Unit) { viewModel.requestLyrics() }
            if (isLoadingLyrics) {
                Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        androidx.compose.material3.CircularProgressIndicator(color = AccentPrimary)
                        Spacer(Modifier.height(12.dp))
                        Text(stringResource(R.string.loading_lyrics), color = Color.White.copy(alpha = 0.6f))
                    }
                }
            } else {
                LyricsScreen(lyricsData, currentPositionMs, track?.title ?: "", { showLyrics = false })
            }
        }
    }

    if (showSleepTimerDialog) {
        SleepTimerDialog(sleepTimerActive,
            { viewModel.startSleepTimer(it); showSleepTimerDialog = false },
            { viewModel.cancelSleepTimer(); showSleepTimerDialog = false },
            { showSleepTimerDialog = false })
    }

    if (showTrackOptions && track != null) {
        TrackOptionsSheet(track!!, { showTrackOptions = false },
            onViewLyrics = { showTrackOptions = false; showLyrics = true },
            onBookmark = { viewModel.toggleFavorite(); showTrackOptions = false },
            onAddToPlaylist = { showTrackOptions = false; showAddToPlaylistDialog = true },
            onViewInfo = { showTrackOptions = false; showTrackInfoDialog = true })
    }
    if (showAddToPlaylistDialog && track != null) {
        AddToPlaylistDialog(track!!, viewModel, { showAddToPlaylistDialog = false })
    }
    if (showTrackInfoDialog && track != null) {
        TrackInfoDialog(track!!, { showTrackInfoDialog = false })
    }



    // === Main Player UI ===
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        // Red ambient glow behind the disc area
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height * 0.38f
            // Main red glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE53935).copy(alpha = 0.15f),
                        Color(0xFFE53935).copy(alpha = 0.08f),
                        Color(0xFFE53935).copy(alpha = 0.03f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = size.width * 0.55f
                ),
                radius = size.width * 0.55f,
                center = Offset(cx, cy)
            )
            // Bottom-left red ambient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE53935).copy(alpha = 0.08f),
                        Color(0xFFE53935).copy(alpha = 0.03f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.15f, size.height * 0.55f),
                    radius = size.width * 0.35f
                ),
                radius = size.width * 0.35f,
                center = Offset(size.width * 0.15f, size.height * 0.55f)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top row: shuffle + title + repeat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val newMode = if (playMode == PlayMode.SHUFFLE) PlayMode.SEQUENCE else PlayMode.SHUFFLE
                    viewModel.setPlayMode(newMode)
                }) {
                    Icon(Icons.Default.Shuffle, "Shuffle",
                        tint = if (playMode == PlayMode.SHUFFLE) AccentPrimary else Color.White.copy(0.5f))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(track?.title ?: stringResource(R.string.not_playing),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(track?.displayArtist ?: "",
                        style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                IconButton(onClick = { viewModel.cyclePlayMode() }) {
                    Icon(
                        when (playMode) { PlayMode.REPEAT_ONE -> Icons.Default.RepeatOne; else -> Icons.Default.Repeat },
                        "Repeat",
                        tint = if (playMode == PlayMode.REPEAT_ALL || playMode == PlayMode.REPEAT_ONE) AccentPrimary
                        else Color.White.copy(0.5f))
                }
            }

            // Time display
            Spacer(Modifier.height(4.dp))
            Text(
                "${formatTime(currentPositionMs)} / ${formatTime(duration)}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = AccentPrimary
            )

            Spacer(Modifier.height(8.dp))

            // === Vinyl disc with glowing progress ring ===
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow behind the progress ring
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val ringPadding = 8.dp.toPx()
                    val ringRadius = (size.minDimension - ringPadding * 2) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Red glow for the progress portion
                    if (progress > 0f) {
                        val glowStroke = 16.dp.toPx()
                        drawArc(
                            color = Color(0xFFE53935).copy(alpha = 0.3f),
                            startAngle = -90f,
                            sweepAngle = progress * 360f,
                            useCenter = false,
                            style = Stroke(width = glowStroke, cap = StrokeCap.Round),
                            topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
                            size = androidx.compose.ui.geometry.Size(ringRadius * 2, ringRadius * 2)
                        )
                    }
                }

                // Progress ring
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val ringPadding = 8.dp.toPx()
                    val strokeWidth = 4.dp.toPx()
                    val ringRadius = (size.minDimension - ringPadding * 2) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Background ring (dim)
                    drawCircle(
                        color = Color.White.copy(alpha = 0.06f),
                        radius = ringRadius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )

                    // Progress arc (bright red)
                    if (progress > 0f) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFFFF5252),
                                    Color(0xFFE53935),
                                    Color(0xFFD32F2F),
                                    Color(0xFFE53935),
                                    Color(0xFFFF5252)
                                ),
                                center = center
                            ),
                            startAngle = -90f,
                            sweepAngle = progress * 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
                            size = androidx.compose.ui.geometry.Size(ringRadius * 2, ringRadius * 2)
                        )
                    }

                    // Progress dot indicator (small circle at current position)
                    val angle = Math.toRadians((-90.0 + progress * 360.0))
                    val dotX = center.x + ringRadius * cos(angle).toFloat()
                    val dotY = center.y + ringRadius * sin(angle).toFloat()

                    // Outer ring of the dot
                    drawCircle(
                        color = Color(0xFFE53935),
                        radius = 7.dp.toPx(),
                        center = Offset(dotX, dotY),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    // Inner dot (hollow look)
                    drawCircle(
                        color = Color(0xFF0A0A0A),
                        radius = 4.dp.toPx(),
                        center = Offset(dotX, dotY)
                    )
                }

                // Vinyl disc (takes most of the space inside the ring)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                when {
                                    dragAmount < -100 -> viewModel.next()
                                    dragAmount > 100 -> viewModel.previous()
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(onDoubleTap = { viewModel.toggleFavorite() })
                        },
                    contentAlignment = Alignment.Center
                ) {
                    VinylDiscView(isPlaying = isPlaying, modifier = Modifier.fillMaxSize())

                    // Center cover art overlay (circular, inside the vinyl label area)
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        com.app.musicplayer.core.ui.components.GenerativeCoverArt(
                            seed = (track?.title ?: "Music") + (track?.artist ?: ""),
                            modifier = Modifier.fillMaxSize()
                        )
                        if (track?.coverUri != null) {
                            coil.compose.AsyncImage(
                                model = track?.coverUri,
                                contentDescription = "Album cover",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.toggleFavorite() }) {
                    Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        "Favorite", tint = if (isFavorite) AccentPrimary else Color.White.copy(0.6f))
                }
                IconButton(onClick = { showAddToPlaylistDialog = true }) {
                    Icon(Icons.Default.PlaylistAdd, "Add to playlist", tint = Color.White.copy(0.6f))
                }
                IconButton(onClick = { showSleepTimerDialog = true }) {
                    Icon(Icons.Default.Timer, "Timer",
                        tint = if (sleepTimerActive) AccentPrimary else Color.White.copy(0.6f))
                }
                IconButton(onClick = { viewModel.cyclePlayMode() }) {
                    Icon(when (playMode) { PlayMode.REPEAT_ONE -> Icons.Default.RepeatOne; else -> Icons.Default.Repeat },
                        "Repeat", tint = if (playMode != PlayMode.SEQUENCE) AccentPrimary else Color.White.copy(0.6f))
                }
                IconButton(onClick = { showTrackOptions = true }) {
                    Icon(Icons.Default.MoreHoriz, "More", tint = Color.White.copy(0.6f))
                }
            }

            Spacer(Modifier.height(12.dp))

            // Transport controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rewind 10s
                IconButton(onClick = { viewModel.seekTo((currentPositionMs - 10000).coerceAtLeast(0)) }) {
                    Icon(Icons.Default.FastRewind, "Rewind 10s", tint = Color.White, modifier = Modifier.size(26.dp))
                }

                // Previous
                IconButton(onClick = { viewModel.previous() }, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.SkipPrevious, "Previous", tint = Color.White, modifier = Modifier.size(32.dp))
                }

                // Play/Pause - RED circle with glow
                Box(contentAlignment = Alignment.Center) {
                    // Glow behind button
                    Canvas(modifier = Modifier.size(72.dp)) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFE53935).copy(alpha = 0.3f),
                                    Color(0xFFE53935).copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                center = Offset(size.width / 2, size.height / 2),
                                radius = size.width / 2
                            ),
                            radius = size.width / 2,
                            center = Offset(size.width / 2, size.height / 2)
                        )
                    }
                    Surface(
                        onClick = { viewModel.playPause() },
                        shape = CircleShape,
                        color = AccentPrimary,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                if (isPlaying) "Pause" else "Play",
                                tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                    }
                }

                // Next
                IconButton(onClick = { viewModel.next() }, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.SkipNext, "Next", tint = Color.White, modifier = Modifier.size(32.dp))
                }

                // Forward 10s
                IconButton(onClick = { viewModel.seekTo((currentPositionMs + 10000).coerceAtMost(duration)) }) {
                    Icon(Icons.Default.FastForward, "Forward 10s", tint = Color.White, modifier = Modifier.size(26.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Bottom row: EQ / Lyrics / Queue
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { /* EQ */ }) {
                        Icon(Icons.Default.Tune, "Equalizer", tint = Color.White.copy(0.5f))
                    }
                    Text(stringResource(R.string.settings_equalizer), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.5f))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { showLyrics = true }) {
                        Icon(Icons.Default.Lyrics, "Lyrics", tint = Color.White.copy(0.5f))
                    }
                    Text(stringResource(R.string.lyrics), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.5f))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { /* Queue */ }) {
                        Icon(Icons.Default.QueueMusic, "Queue", tint = Color.White.copy(0.5f))
                    }
                    Text(stringResource(R.string.tab_playlists), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.5f))
                }
            }
        }
    }
}

@Composable
private fun SleepTimerDialog(isActive: Boolean, onTimerSet: (Int) -> Unit, onCancel: () -> Unit, onDismiss: () -> Unit) {
    val options = listOf(15, 30, 45, 60, 90, 120)
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.sleep_timer)) },
        text = {
            Column {
                if (isActive) {
                    androidx.compose.material3.TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.cancel_timer), color = AccentPrimary)
                    }
                }
                options.forEach { min ->
                    androidx.compose.material3.TextButton(onClick = { onTimerSet(min) }, modifier = Modifier.fillMaxWidth()) {
                        Text("$min min")
                    }
                }
            }
        },
        confirmButton = { androidx.compose.material3.TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}

private fun formatTime(ms: Long): String {
    if (ms <= 0) return "00:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
