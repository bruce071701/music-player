package com.app.musicplayer.feature.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.musicplayer.core.model.Track

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TrackOptionsSheet(
    track: Track,
    onDismiss: () -> Unit,
    onDelete: () -> Unit = {},
    onAddToPlaylist: () -> Unit = {},
    onBookmark: () -> Unit = {},
    onViewAlbumArt: () -> Unit = {},
    onViewInfo: () -> Unit = {},
    onViewLyrics: () -> Unit = {},
    onGoToArtist: () -> Unit = {},
    onGoToAlbum: () -> Unit = {},
    onGoToFolder: () -> Unit = {},
    onGoToGenre: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Track header info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cover thumbnail
                Surface(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    if (track.coverUri != null) {
                        AsyncImage(
                            model = track.coverUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.MusicNote, null,
                            modifier = Modifier.padding(16.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${track.displayArtist} - ${track.displayAlbum}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Duration + format
                    Text(
                        text = buildString {
                            append(track.durationFormatted)
                            track.filePath?.substringAfterLast(".")?.let { ext ->
                                append(" | $ext")
                            }
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Star rating placeholder
                IconButton(onClick = {}) {
                    Icon(
                        if (track.rating > 0) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Rate",
                        tint = if (track.rating > 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action grid (2 columns)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                maxItemsInEachRow = 2
            ) {
                ActionGridItem(Icons.Default.Delete, "Delete", onClick = onDelete,
                    modifier = Modifier.weight(1f))
                ActionGridItem(Icons.Default.PlaylistAdd, "Playlist", onClick = onAddToPlaylist,
                    modifier = Modifier.weight(1f))
                ActionGridItem(Icons.Default.Bookmark, "Bookmark", onClick = onBookmark,
                    modifier = Modifier.weight(1f))
                ActionGridItem(Icons.Default.Image, "Album Art", onClick = onViewAlbumArt,
                    modifier = Modifier.weight(1f))
                ActionGridItem(Icons.Default.Info, "Info/Tags", onClick = onViewInfo,
                    modifier = Modifier.weight(1f))
                ActionGridItem(Icons.Default.Lyrics, "Lyrics", onClick = onViewLyrics,
                    modifier = Modifier.weight(1f))
                ActionGridItem(Icons.Default.Person, "Artist", onClick = onGoToArtist,
                    modifier = Modifier.weight(1f))
                ActionGridItem(Icons.Default.Album, "Album", onClick = onGoToAlbum,
                    modifier = Modifier.weight(1f))
                ActionGridItem(Icons.Default.Folder, "Folder", onClick = onGoToFolder,
                    modifier = Modifier.weight(1f))
                ActionGridItem(Icons.Default.Style, "Genre", onClick = onGoToGenre,
                    modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ActionGridItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                icon, contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
