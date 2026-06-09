package com.app.musicplayer.feature.transfer

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.app.musicplayer.core.model.Track
import com.app.musicplayer.core.ui.theme.AccentPrimary
import com.app.musicplayer.core.ui.theme.AppBackground
import com.app.musicplayer.core.ui.theme.AppSurfaceVariant
import com.app.musicplayer.core.ui.theme.TextPrimary
import com.app.musicplayer.core.ui.theme.TextSecondary
import com.app.musicplayer.core.ui.theme.TextTertiary
import com.app.musicplayer.feature.library.LibraryViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicTransferScreen(
    libraryViewModel: LibraryViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val allTracks by libraryViewModel.allTracks.collectAsState()
    var selectedIds by remember { mutableStateOf(setOf<Long>()) }

    val selectedCount = selectedIds.size
    val isAllSelected = allTracks.isNotEmpty() && selectedIds.size == allTracks.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        "Music Transfer",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    if (selectedCount > 0) {
                        Text(
                            "$selectedCount selected",
                            style = MaterialTheme.typography.bodySmall,
                            color = AccentPrimary
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                }
            },
            actions = {
                // Select all / deselect all
                IconButton(onClick = {
                    selectedIds = if (isAllSelected) {
                        emptySet()
                    } else {
                        allTracks.map { it.id }.toSet()
                    }
                }) {
                    Icon(
                        Icons.Default.SelectAll,
                        contentDescription = "Select All",
                        tint = if (isAllSelected) AccentPrimary else TextSecondary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        if (allTracks.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.MusicNote,
                        null,
                        modifier = Modifier.size(64.dp),
                        tint = TextTertiary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No music to transfer", color = TextSecondary)
                    Text("Scan your library first", color = TextTertiary, fontSize = 12.sp)
                }
            }
        } else {
            // Track list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(allTracks, key = { it.id }) { track ->
                    val isSelected = selectedIds.contains(track.id)
                    TransferTrackItem(
                        track = track,
                        isSelected = isSelected,
                        onClick = {
                            selectedIds = if (isSelected) {
                                selectedIds - track.id
                            } else {
                                selectedIds + track.id
                            }
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }

            // Bottom share button
            if (selectedCount > 0) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = AppSurfaceVariant,
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = {
                            shareSelectedTracks(context, allTracks.filter { selectedIds.contains(it.id) })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.Share, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Share $selectedCount song${if (selectedCount > 1) "s" else ""}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransferTrackItem(
    track: Track,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isSelected) AccentPrimary.copy(alpha = 0.08f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Selection indicator
        Icon(
            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isSelected) AccentPrimary else TextTertiary,
            modifier = Modifier.size(24.dp)
        )

        // Cover art
        Surface(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = AppSurfaceVariant
        ) {
            if (track.coverUri != null) {
                AsyncImage(
                    model = track.coverUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(44.dp)
                )
            } else {
                Icon(
                    Icons.Default.MusicNote, null,
                    modifier = Modifier.padding(10.dp),
                    tint = TextSecondary
                )
            }
        }

        // Track info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) TextPrimary else TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${track.displayArtist} · ${track.durationFormatted}",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // File size
        track.fileSize?.let { size ->
            Text(
                text = formatFileSize(size),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}

private fun shareSelectedTracks(context: Context, tracks: List<Track>) {
    if (tracks.isEmpty()) return

    val uris = ArrayList<Uri>()
    tracks.forEach { track ->
        track.filePath?.let { path ->
            val file = File(path)
            if (file.exists()) {
                try {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    uris.add(uri)
                } catch (e: Exception) {
                    // Fallback: use file URI directly (may not work on all devices)
                    uris.add(Uri.fromFile(file))
                }
            }
        }
    }

    if (uris.isEmpty()) return

    val intent = if (uris.size == 1) {
        Intent(Intent.ACTION_SEND).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, uris[0])
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    } else {
        Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "audio/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    try {
        context.startActivity(Intent.createChooser(intent, "Share via"))
    } catch (_: Exception) { }
}

private fun formatFileSize(bytes: Long): String {
    val mb = bytes / (1024.0 * 1024.0)
    return if (mb >= 1024) "%.1f GB".format(mb / 1024.0)
    else "%.1f MB".format(mb)
}
