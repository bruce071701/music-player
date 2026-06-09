package com.app.musicplayer.feature.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.musicplayer.core.model.PlayMode
import com.app.musicplayer.core.ui.theme.AccentPrimary
import com.app.musicplayer.core.ui.theme.TextSecondary
import com.penji.musicplayer.offline.R
import androidx.compose.ui.res.stringResource
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

    // Extract dominant color from cover art for background
    val context = androidx.compose.ui.platform.LocalContext.current
    var dominantColor by remember { mutableStateOf(Color(0xFF2D1B1B)) }

    androidx.compose.runtime.LaunchedEffect(track?.coverUri) {
        track?.coverUri?.let { uri ->
            try {
                val loader = coil.ImageLoader(context)
                val request = coil.request.ImageRequest.Builder(context)
                    .data(uri)
                    .allowHardware(false)
                    .build()
                val result = loader.execute(request)
                val bitmap = (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                if (bitmap != null) {
                    val palette = androidx.palette.graphics.Palette.from(bitmap).generate()
                    val extractedColor = palette.getDarkMutedColor(
                        palette.getMutedColor(0xFF2D1B1B.toInt())
                    )
                    dominantColor = Color(extractedColor)
                }
            } catch (_: Exception) { }
        }
    }

    // === Main Player UI ===
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        dominantColor.copy(alpha = 0.8f),
                        dominantColor.copy(alpha = 0.4f),
                        Color(0xFF0A0505)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top row: shuffle + title + link
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

            // Time
            Spacer(Modifier.height(8.dp))
            Text(
                "${formatTime(currentPositionMs)} / ${formatTime(duration)}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = AccentPrimary
            )

            Spacer(Modifier.height(12.dp))

            // === Circular cover with arc progress ===
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                // Arc progress ring
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 6.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Background ring
                    drawCircle(
                        color = Color.White.copy(alpha = 0.1f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )

                    // Progress arc (red)
                    drawArc(
                        color = AccentPrimary,
                        startAngle = -90f,
                        sweepAngle = progress * 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                    )

                    // Progress dot indicator
                    val angle = Math.toRadians((-90.0 + progress * 360.0))
                    val dotX = center.x + radius * cos(angle).toFloat()
                    val dotY = center.y + radius * sin(angle).toFloat()
                    drawCircle(color = Color.White, radius = 8.dp.toPx(), center = Offset(dotX, dotY))
                    drawCircle(color = AccentPrimary, radius = 5.dp.toPx(), center = Offset(dotX, dotY))
                }

                // Circular album cover
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .clip(CircleShape)
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
                    color = Color(0xFF2A2020),
                    shape = CircleShape
                ) {
                    if (track?.coverUri != null) {
                        AsyncImage(
                            model = track?.coverUri,
                            contentDescription = "Album cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    // Always show vinyl underneath
                    VinylDiscView(isPlaying = isPlaying, modifier = Modifier.fillMaxSize())
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

                // Play/Pause - RED circle
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
                IconButton(onClick = { /* EQ */ }) {
                    Icon(Icons.Default.Tune, "Equalizer", tint = Color.White.copy(0.5f))
                }
                Row(
                    modifier = Modifier.clickable { showLyrics = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Lyrics, "Lyrics", tint = Color.White.copy(0.5f), modifier = Modifier.size(20.dp))
                    Text(stringResource(R.string.lyrics), style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.5f))
                }
                IconButton(onClick = { /* Queue */ }) {
                    Icon(Icons.Default.QueueMusic, "Queue", tint = Color.White.copy(0.5f))
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
