package com.app.musicplayer.feature.library.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.penji.musicplayer.offline.R
import com.app.musicplayer.core.ui.theme.AccentPrimary
import com.app.musicplayer.core.ui.theme.AppSurfaceVariant
import com.app.musicplayer.core.ui.theme.TextPrimary
import com.app.musicplayer.core.ui.theme.TextSecondary
import com.app.musicplayer.core.ui.theme.TextTertiary
import com.app.musicplayer.feature.library.LibraryViewModel
import com.app.musicplayer.feature.library.scanner.ScanProgress
import com.app.musicplayer.feature.player.PlayerViewModel

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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 2x2 Action buttons grid
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionButton(
                    icon = Icons.Default.Shuffle,
                    label = stringResource(R.string.shuffle),
                    iconTint = AccentPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val shuffled = allTracks.shuffled()
                        if (shuffled.isNotEmpty()) playerViewModel.play(shuffled.first(), shuffled)
                    }
                )
                ActionButton(
                    icon = Icons.Default.PlayArrow,
                    label = stringResource(R.string.play),
                    iconTint = AccentPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (allTracks.isNotEmpty()) playerViewModel.play(allTracks.first(), allTracks)
                    }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionButton(
                    icon = Icons.Default.Refresh,
                    label = if (scanProgress.isScanning) {
                        "${scanProgress.scannedCount}/${scanProgress.totalCount}"
                    } else {
                        stringResource(R.string.scan_music)
                    },
                    iconTint = if (scanProgress.isScanning) AccentPrimary else TextSecondary,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToScan() }
                )
                ActionButton(
                    icon = Icons.Default.PlayArrow,
                    label = stringResource(R.string.play_all),
                    iconTint = TextSecondary,
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

        // Middle ad banner placeholder (between actions and recommendations)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E1E)),
                contentAlignment = Alignment.Center
            ) {
                Text("", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
            }
        }

        // "For You" section header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.for_you),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                TextButton(onClick = {
                    val shuffled = allTracks.shuffled()
                    if (shuffled.isNotEmpty()) playerViewModel.play(shuffled.first(), shuffled)
                }) {
                    Icon(
                        Icons.Default.Shuffle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = AccentPrimary
                    )
                    Text(
                        " " + stringResource(R.string.shuffle),
                        color = AccentPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Show up to 10 tracks as recommendations
        val recommendedTracks = allTracks.take(10)
        items(recommendedTracks, key = { it.id }) { track ->
            TrackListItem(
                track = track,
                onClick = { playerViewModel.play(track, allTracks) }
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = AppSurfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = TextPrimary
            )
        }
    }
}
