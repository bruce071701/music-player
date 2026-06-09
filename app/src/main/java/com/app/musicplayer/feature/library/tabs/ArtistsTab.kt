package com.app.musicplayer.feature.library.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.penji.musicplayer.offline.R
import com.app.musicplayer.core.model.Track
import com.app.musicplayer.core.ui.theme.AccentPrimary
import com.app.musicplayer.core.ui.theme.AppSurfaceVariant
import com.app.musicplayer.core.ui.theme.TextPrimary
import com.app.musicplayer.core.ui.theme.TextSecondary
import com.app.musicplayer.core.ui.theme.TextTertiary

@Composable
fun ArtistsTab(
    artists: List<String> = emptyList(),
    allTracks: List<Track> = emptyList(),
    onArtistClick: (String) -> Unit = {}
) {
    if (artists.isEmpty()) {
        EmptyLibraryState(message = stringResource(R.string.no_artists))
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.artists_count, artists.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Artist list
            items(artists) { artist ->
                val artistTracks = allTracks.filter { it.displayArtist == artist }
                val albumCount = artistTracks.mapNotNull { it.album }.distinct().size

                ArtistListItem(
                    name = artist,
                    albumCount = albumCount,
                    trackCount = artistTracks.size,
                    onClick = { onArtistClick(artist) }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun ArtistListItem(
    name: String,
    albumCount: Int,
    trackCount: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(22.dp),
            color = AppSurfaceVariant
        ) {
            Icon(
                Icons.Default.Mic,
                contentDescription = null,
                modifier = Modifier.padding(10.dp),
                tint = AccentPrimary
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$albumCount albums · $trackCount tracks",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
    }
}
