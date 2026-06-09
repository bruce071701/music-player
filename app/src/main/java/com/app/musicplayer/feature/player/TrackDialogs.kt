package com.app.musicplayer.feature.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.musicplayer.core.model.Track

/**
 * Dialog to add current track to an existing or new playlist.
 */
@Composable
fun AddToPlaylistDialog(
    track: Track,
    viewModel: PlayerViewModel,
    onDismiss: () -> Unit
) {
    var showCreateNew by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    if (showCreateNew) {
        AlertDialog(
            onDismissRequest = { showCreateNew = false },
            title = { Text("New Playlist") },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    label = { Text("Playlist name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPlaylistName.isNotBlank()) {
                            // Create playlist (handled by caller)
                            showCreateNew = false
                            onDismiss()
                        }
                    }
                ) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateNew = false }) { Text("Cancel") }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add to Playlist") },
            text = {
                Column {
                    // Create new playlist option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCreateNew = true }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            "Create new playlist",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Existing playlists",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Playlists will appear here when created
                    Text(
                        "No playlists yet. Create one above.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) { Text("Close") }
            }
        )
    }
}

/**
 * Dialog showing detailed track information (tags, codec, file info).
 */
@Composable
fun TrackInfoDialog(
    track: Track,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Track Info") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoRow("Title", track.title)
                InfoRow("Artist", track.displayArtist)
                InfoRow("Album", track.displayAlbum)
                track.genre?.let { InfoRow("Genre", it) }
                InfoRow("Duration", track.durationFormatted)
                track.year?.let { InfoRow("Year", it.toString()) }
                track.trackNumber?.let { InfoRow("Track #", it.toString()) }
                track.discNumber?.let { InfoRow("Disc #", it.toString()) }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // Technical info
                track.filePath?.let { path ->
                    InfoRow("Format", path.substringAfterLast(".").uppercase())
                    InfoRow("Path", path)
                }
                track.bitrate?.let { InfoRow("Bitrate", "${it} kbps") }
                track.sampleRate?.let { InfoRow("Sample Rate", "${it} Hz") }
                track.fileSize?.let {
                    val sizeMb = it / (1024.0 * 1024.0)
                    InfoRow("File Size", "%.1f MB".format(sizeMb))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                InfoRow("Play Count", track.playCount.toString())
                InfoRow("Rating", if (track.rating > 0) "★".repeat(track.rating) else "Not rated")
                InfoRow("Favorite", if (track.isFavorite) "Yes" else "No")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.35f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.65f)
        )
    }
}
