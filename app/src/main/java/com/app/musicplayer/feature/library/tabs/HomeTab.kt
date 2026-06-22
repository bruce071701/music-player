package com.app.musicplayer.feature.library.tabs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.musicplayer.core.model.Track
import com.app.musicplayer.core.ui.components.GenerativeCoverArt
import com.app.musicplayer.core.ui.theme.AccentPrimary
import com.app.musicplayer.core.ui.theme.AppSurfaceVariant
import com.app.musicplayer.core.ui.theme.TextPrimary
import com.app.musicplayer.core.ui.theme.TextSecondary
import com.app.musicplayer.core.ui.theme.TextTertiary
import com.app.musicplayer.feature.library.LibraryViewModel
import com.app.musicplayer.feature.player.PlayerViewModel
import com.penji.musicplayer.offline.R

@Composable
fun HomeTab(
    libraryViewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel,
    onNavigateToScan: () -> Unit = {}
) {
    val allTracks by libraryViewModel.allTracks.collectAsState()
    val scanProgress by libraryViewModel.scanProgress.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // === Row 1: Shuffle + Play ===
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionCard(
                    icon = Icons.Default.Shuffle,
                    title = stringResource(R.string.shuffle),
                    subtitle = stringResource(R.string.shuffle_all_songs_desc),
                    iconTint = AccentPrimary,
                    iconBorderColor = AccentPrimary,
                    backgroundColors = listOf(
                        Color(0xFF2D1520),
                        Color(0xFF1F1225),
                        Color(0xFF181020)
                    ),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val shuffled = allTracks.shuffled()
                        if (shuffled.isNotEmpty()) playerViewModel.play(shuffled.first(), shuffled)
                    }
                )
                ActionCard(
                    icon = Icons.Default.PlayArrow,
                    title = stringResource(R.string.play),
                    subtitle = stringResource(R.string.play_favorites_desc),
                    iconTint = AccentPrimary,
                    iconBorderColor = AccentPrimary,
                    backgroundColors = listOf(
                        Color(0xFF2D1520),
                        Color(0xFF1F1225),
                        Color(0xFF181020)
                    ),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (allTracks.isNotEmpty()) playerViewModel.play(allTracks.first(), allTracks)
                    }
                )
            }
        }

        // === Row 2: Scan Music + Play All ===
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionCard(
                    icon = Icons.Default.Refresh,
                    title = if (scanProgress.isScanning) {
                        "${scanProgress.scannedCount}/${scanProgress.totalCount}"
                    } else {
                        stringResource(R.string.scan_music)
                    },
                    subtitle = stringResource(R.string.find_music_desc),
                    iconTint = Color(0xFF6CA0DC),
                    iconBorderColor = Color(0xFF4A7AB5),
                    backgroundColors = listOf(
                        Color(0xFF151828),
                        Color(0xFF121520),
                        Color(0xFF0E1018)
                    ),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToScan() }
                )
                ActionCard(
                    icon = Icons.Default.PlayArrow,
                    title = stringResource(R.string.play_all),
                    subtitle = stringResource(R.string.play_all_songs_desc),
                    iconTint = Color(0xFF6CA0DC),
                    iconBorderColor = Color(0xFF4A7AB5),
                    backgroundColors = listOf(
                        Color(0xFF151828),
                        Color(0xFF121520),
                        Color(0xFF0E1018)
                    ),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (allTracks.isNotEmpty()) playerViewModel.play(allTracks.first(), allTracks)
                    }
                )
            }
        }

        // Scan progress bar
        if (scanProgress.isScanning) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Scanning...",
                            style = MaterialTheme.typography.bodySmall,
                            color = AccentPrimary
                        )
                        Text(
                            text = "${scanProgress.scannedCount} / ${scanProgress.totalCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = {
                            if (scanProgress.totalCount > 0)
                                scanProgress.scannedCount.toFloat() / scanProgress.totalCount.toFloat()
                            else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = AccentPrimary,
                        trackColor = AppSurfaceVariant
                    )
                }
            }
        }

        // === "For You" section header ===
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.for_you),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = TextPrimary
                )
                TextButton(onClick = {
                    val shuffled = allTracks.shuffled()
                    if (shuffled.isNotEmpty()) playerViewModel.play(shuffled.first(), shuffled)
                }) {
                    Icon(
                        Icons.Default.Shuffle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = AccentPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        stringResource(R.string.shuffle),
                        color = AccentPrimary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        // === Horizontal scrollable track cards ===
        item {
            val recommendedTracks = allTracks.take(10)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(recommendedTracks, key = { it.id }) { track ->
                    TrackCard(
                        track = track,
                        onClick = { playerViewModel.play(track, allTracks) },
                        onMoreClick = { /* track options */ }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

/**
 * Action card matching the reference design:
 * - Dark gradient background with subtle dot pattern
 * - Icon in a bordered circle (left side)
 * - Title + subtitle text (right of icon)
 * - Rounded corners (14dp)
 * - Height ~100dp
 */
@Composable
private fun ActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    iconBorderColor: Color,
    backgroundColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = backgroundColors,
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    ),
                    RoundedCornerShape(14.dp)
                )
        )

        // Dot pattern overlay (subtle texture like in the reference)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val dotRadius = 1f
            val spacing = 14.dp.toPx()
            val dotColor = Color.White.copy(alpha = 0.03f)
            var x = spacing
            while (x < size.width) {
                var y = spacing
                while (y < size.height) {
                    drawCircle(color = dotColor, radius = dotRadius, center = Offset(x, y))
                    y += spacing
                }
                x += spacing
            }
        }

        // Content
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon in bordered circle
            Box(
                modifier = Modifier.size(46.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Circle border
                    drawCircle(
                        color = iconBorderColor.copy(alpha = 0.5f),
                        radius = size.minDimension / 2 - 1f,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    // Fill
                    drawCircle(
                        color = iconBorderColor.copy(alpha = 0.1f),
                        radius = size.minDimension / 2 - 2f
                    )
                }
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Text
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Color.White.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Track card for "For You" section matching the reference:
 * - Cover art with rounded corners
 * - Semi-transparent play button overlay (bottom-left)
 * - Title with more-options icon
 * - Artist name
 * - Duration (bottom-right aligned)
 */
@Composable
private fun TrackCard(
    track: Track,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A1E), RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        // Cover art
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        ) {
            // Always draw generative art as background fallback
            GenerativeCoverArt(
                seed = track.title + (track.artist ?: ""),
                modifier = Modifier.fillMaxSize()
            )

            // Overlay with actual cover art if available
            if (track.coverUri != null) {
                AsyncImage(
                    model = track.coverUri,
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Play button overlay (bottom-left)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .size(30.dp)
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        CircleShape
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Text content with padding inside the card
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            // Title + more icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    ),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = TextTertiary,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable(onClick = onMoreClick)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Artist + Duration row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.displayArtist,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = track.durationFormatted,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary.copy(alpha = 0.7f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
