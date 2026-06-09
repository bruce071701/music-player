package com.app.musicplayer.feature.library.tabs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.penji.musicplayer.offline.R
import com.app.musicplayer.core.ui.theme.AccentPrimary
import com.app.musicplayer.core.ui.theme.CardBlue
import com.app.musicplayer.core.ui.theme.CardGreen
import com.app.musicplayer.core.ui.theme.CardOrange
import com.app.musicplayer.core.ui.theme.CardPink
import com.app.musicplayer.core.ui.theme.TextSecondary
import com.app.musicplayer.feature.library.LibraryViewModel
import com.app.musicplayer.feature.player.PlayerViewModel
import kotlinx.coroutines.launch

@Composable
fun PlaylistsTab(
    libraryViewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val allTracks by libraryViewModel.allTracks.collectAsState()
    val userPlaylists by libraryViewModel.userPlaylists.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var openedPlaylistId by remember { mutableStateOf<Long?>(null) }

    // Import playlist: user picks a folder, all audio files in it become a playlist
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val result = libraryViewModel.importPlaylistFromFolder(context, uri)
                statusMessage = result
            }
        }
    }

    // Restore playlists: user picks a folder containing .m3u/.m3u8 files
    val restoreFolderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                statusMessage = "Scanning playlist files..."
                val count = libraryViewModel.restorePlaylistsFromFolder(context, uri)
                statusMessage = if (count > 0) "Restored $count playlists" else "No playlist files found"
            }
        }
    }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                libraryViewModel.createPlaylist(name)
                showCreateDialog = false
            }
        )
    }

    // If a playlist is opened, show its tracks
    val openedPlaylist = userPlaylists.find { it.id == openedPlaylistId }
    if (openedPlaylist != null) {
        PlaylistDetailView(
            playlistName = openedPlaylist.name,
            playlistId = openedPlaylist.id,
            libraryViewModel = libraryViewModel,
            playerViewModel = playerViewModel,
            onBack = { openedPlaylistId = null }
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "4 playlists",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        Icons.Default.Add, "Add",
                        modifier = Modifier.size(22.dp).clickable { showCreateDialog = true },
                        tint = TextSecondary
                    )
                }
            }
        }

        // Smart playlist cards - 2x2 grid
        item {
            val favCount = allTracks.count { it.isFavorite }
            val recentAddedCount = allTracks.size.coerceAtMost(50)
            val recentPlayedCount = allTracks.count { (it.lastPlayedAt ?: 0) > 0 }.coerceAtMost(50)
            val mostPlayedCount = allTracks.count { it.playCount > 0 }.coerceAtMost(50)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlaylistCard(
                    title = stringResource(R.string.favorites),
                    subtitle = "$favCount tracks",
                    icon = Icons.Default.Favorite,
                    gradient = listOf(CardPink, CardPink.copy(alpha = 0.7f)),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val favorites = allTracks.filter { it.isFavorite }
                        if (favorites.isNotEmpty()) playerViewModel.play(favorites.first(), favorites)
                    }
                )
                PlaylistCard(
                    title = stringResource(R.string.recently_added),
                    subtitle = "$recentAddedCount tracks",
                    icon = Icons.Default.NewReleases,
                    gradient = listOf(CardGreen, CardGreen.copy(alpha = 0.7f)),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val recent = allTracks.sortedByDescending { it.addedAt }.take(50)
                        if (recent.isNotEmpty()) playerViewModel.play(recent.first(), recent)
                    }
                )
            }
        }

        item {
            val recentPlayedCount = allTracks.count { (it.lastPlayedAt ?: 0) > 0 }.coerceAtMost(50)
            val mostPlayedCount = allTracks.count { it.playCount > 0 }.coerceAtMost(50)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlaylistCard(
                    title = stringResource(R.string.recently_played),
                    subtitle = "$recentPlayedCount tracks",
                    icon = Icons.Default.History,
                    gradient = listOf(CardBlue, CardBlue.copy(alpha = 0.7f)),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val recent = allTracks.sortedByDescending { it.lastPlayedAt ?: 0 }
                            .filter { (it.lastPlayedAt ?: 0) > 0 }.take(50)
                        if (recent.isNotEmpty()) playerViewModel.play(recent.first(), recent)
                    }
                )
                PlaylistCard(
                    title = stringResource(R.string.most_played),
                    subtitle = "$mostPlayedCount tracks",
                    icon = Icons.Default.TrendingUp,
                    gradient = listOf(CardOrange, CardOrange.copy(alpha = 0.7f)),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val top = allTracks.sortedByDescending { it.playCount }
                            .filter { it.playCount > 0 }.take(50)
                        if (top.isNotEmpty()) playerViewModel.play(top.first(), top)
                    }
                )
            }
        }

        // My playlists section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(R.string.my_playlists) + " (${userPlaylists.size})",
                style = MaterialTheme.typography.titleSmall,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Display user-created playlists
        items(userPlaylists, key = { it.id }) { playlist ->
            PlaylistActionRow(
                icon = Icons.Default.QueueMusic,
                text = playlist.name,
                onClick = { openedPlaylistId = playlist.id }
            )
        }

        // Create new playlist row
        item {
            PlaylistActionRow(
                icon = Icons.Default.Add,
                text = stringResource(R.string.create_playlist),
                onClick = { showCreateDialog = true }
            )
        }

        item {
            PlaylistActionRow(
                icon = Icons.Default.Restore,
                text = stringResource(R.string.restore_playlist),
                onClick = { restoreFolderLauncher.launch(null) }
            )
        }

        item {
            PlaylistActionRow(
                icon = Icons.Default.LibraryAdd,
                text = stringResource(R.string.import_playlist),
                onClick = { folderPickerLauncher.launch(null) }
            )
        }

        // Status message
        if (statusMessage != null) {
            item {
                Text(
                    text = statusMessage!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentPrimary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun PlaylistCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(gradient),
                    RoundedCornerShape(12.dp)
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            Icon(
                icon, null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp),
                tint = Color.White.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun PlaylistActionRow(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Color(0xFF2A2A2A)
        ) {
            Icon(
                icon, null,
                modifier = Modifier.padding(10.dp),
                tint = TextSecondary
            )
        }
        Text(text, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
    }
}

@Composable
private fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.create_playlist)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.playlist_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onCreate(name) }, enabled = name.isNotBlank()) {
                Text(stringResource(R.string.create))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}


@Composable
private fun PlaylistDetailView(
    playlistName: String,
    playlistId: Long,
    libraryViewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel,
    onBack: () -> Unit
) {
    val playlistTracks by libraryViewModel.getPlaylistTracks(playlistId).collectAsState(initial = emptyList())
    val allTracks by libraryViewModel.allTracks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddTracksToPlaylistDialog(
            allTracks = allTracks,
            existingTrackIds = playlistTracks.map { it.id }.toSet(),
            onAdd = { selectedIds ->
                selectedIds.forEach { trackId ->
                    libraryViewModel.addTrackToPlaylist(playlistId, trackId)
                }
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() },
                tint = AccentPrimary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    playlistName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    "${playlistTracks.size} tracks",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            // Add tracks button
            Icon(
                Icons.Default.Add,
                contentDescription = "Add tracks",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showAddDialog = true },
                tint = AccentPrimary
            )
        }

        if (playlistTracks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No tracks in this playlist", color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showAddDialog = true },
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            "Add tracks",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = AccentPrimary
                        )
                    }
                }
            }
        } else {
            // Play all / Shuffle buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val shuffled = playlistTracks.shuffled()
                            if (shuffled.isNotEmpty()) playerViewModel.play(shuffled.first(), shuffled)
                        },
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Shuffle, null, tint = AccentPrimary, modifier = Modifier.size(18.dp))
                        Text(stringResource(R.string.shuffle), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            if (playlistTracks.isNotEmpty()) playerViewModel.play(playlistTracks.first(), playlistTracks)
                        },
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, null, tint = AccentPrimary, modifier = Modifier.size(18.dp))
                        Text(stringResource(R.string.play), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Track list with remove option
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(playlistTracks, key = { it.id }) { track ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { playerViewModel.play(track, playlistTracks) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Track info (reuse layout from TrackListItem)
                        Surface(
                            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(6.dp)),
                            color = MaterialTheme.colorScheme.surfaceVariant
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
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                track.title,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                track.displayArtist,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                maxLines = 1
                            )
                        }
                        // Remove from playlist
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Remove",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    libraryViewModel.removeTrackFromPlaylist(playlistId, track.id)
                                },
                            tint = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun AddTracksToPlaylistDialog(
    allTracks: List<com.app.musicplayer.core.model.Track>,
    existingTrackIds: Set<Long>,
    onAdd: (List<Long>) -> Unit,
    onDismiss: () -> Unit
) {
    val availableTracks = allTracks.filter { it.id !in existingTrackIds }
    var selectedIds by remember { mutableStateOf(setOf<Long>()) }

    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 48.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Add tracks (${selectedIds.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                if (selectedIds.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onAdd(selectedIds.toList()) },
                        color = AccentPrimary
                    ) {
                        Text(
                            "Add",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (availableTracks.isEmpty()) {
                Text("All tracks are already in this playlist", color = TextSecondary)
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                    items(availableTracks, key = { it.id }) { track ->
                        val isSelected = track.id in selectedIds
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedIds = if (isSelected) selectedIds - track.id else selectedIds + track.id
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    selectedIds = if (isSelected) selectedIds - track.id else selectedIds + track.id
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(track.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(track.displayArtist, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}
