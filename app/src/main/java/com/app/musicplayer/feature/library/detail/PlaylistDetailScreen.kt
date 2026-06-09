package com.app.musicplayer.feature.library.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.musicplayer.core.model.Track
import com.app.musicplayer.feature.library.tabs.TrackListItem
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlistName: String,
    tracksFlow: Flow<List<Track>>,
    onTrackClick: (Track, List<Track>) -> Unit,
    onDeletePlaylist: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val tracks by tracksFlow.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(playlistName) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onDeletePlaylist) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete playlist")
                }
            }
        )

        Text(
            text = "${tracks.size} 首曲目",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(onClick = {
                if (tracks.isNotEmpty()) onTrackClick(tracks.first(), tracks)
            }) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Text("播放全部", modifier = Modifier.padding(start = 4.dp))
            }
            FilledTonalButton(onClick = {
                if (tracks.isNotEmpty()) onTrackClick(tracks.random(), tracks.shuffled())
            }) {
                Icon(Icons.Default.Shuffle, contentDescription = null)
                Text("随机播放", modifier = Modifier.padding(start = 4.dp))
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(tracks, key = { it.id }) { track ->
                TrackListItem(
                    track = track,
                    onClick = { onTrackClick(track, tracks) }
                )
            }
        }
    }
}
