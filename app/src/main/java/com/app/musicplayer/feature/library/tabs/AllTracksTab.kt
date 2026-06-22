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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.penji.musicplayer.offline.R
import com.app.musicplayer.core.model.Track
import com.app.musicplayer.core.ui.theme.AccentPrimary
import com.app.musicplayer.core.ui.theme.TextSecondary

@Composable
fun AllTracksTab(
    tracks: List<Track> = emptyList(),
    onTrackClick: (Track) -> Unit = {},
    onShuffleAll: (() -> Unit)? = null,
    onPlayAll: (() -> Unit)? = null
) {
    var showSortDialog by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf(SortOption.TITLE) }
    var sortAsc by remember { mutableStateOf(true) }

    val sortedTracks = remember(tracks, sortBy, sortAsc) {
        val sorted = when (sortBy) {
            SortOption.TITLE -> tracks.sortedBy { it.title.lowercase() }
            SortOption.ARTIST -> tracks.sortedBy { (it.artist ?: "").lowercase() }
            SortOption.ALBUM -> tracks.sortedBy { (it.album ?: "").lowercase() }
            SortOption.FOLDER -> tracks.sortedBy { it.filePath?.substringBeforeLast("/") ?: "" }
            SortOption.DATE_ADDED -> tracks.sortedByDescending { it.addedAt }
            SortOption.PLAY_COUNT -> tracks.sortedByDescending { it.playCount }
            SortOption.YEAR -> tracks.sortedByDescending { it.year ?: 0 }
            SortOption.DURATION -> tracks.sortedByDescending { it.durationMs }
        }
        if (!sortAsc && sortBy != SortOption.DATE_ADDED && sortBy != SortOption.PLAY_COUNT) sorted.reversed() else sorted
    }

    if (showSortDialog) {
        SortDialog(
            currentSort = sortBy,
            isAsc = sortAsc,
            onSelect = { sort, asc ->
                sortBy = sort
                sortAsc = asc
                showSortDialog = false
            },
            onDismiss = { showSortDialog = false }
        )
    }

    if (tracks.isEmpty()) {
        EmptyLibraryState(message = stringResource(R.string.no_tracks))
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Header with count + sort icons
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.tracks_count, tracks.size),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = { showSortDialog = true }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Sort, "Sort", tint = TextSecondary, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Shuffle button
                        Surface(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onShuffleAll?.invoke() ?: run {
                                        val shuffled = tracks.shuffled()
                                        if (shuffled.isNotEmpty()) onTrackClick(shuffled.first())
                                    }
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

                        // Play button
                        Surface(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onPlayAll?.invoke() ?: run {
                                        if (sortedTracks.isNotEmpty()) onTrackClick(sortedTracks.first())
                                    }
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
                }
            }

            // Sorted track list
            items(sortedTracks, key = { it.id }) { track ->
                TrackListItem(track = track, onClick = { onTrackClick(track) })
            }
        }
    }
}

private enum class SortOption {
    TITLE, ARTIST, ALBUM, FOLDER, DATE_ADDED, PLAY_COUNT, YEAR, DURATION
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun SortDialog(
    currentSort: SortOption,
    isAsc: Boolean,
    onSelect: (SortOption, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSort by remember { mutableStateOf(currentSort) }
    var selectedAsc by remember { mutableStateOf(isAsc) }

    val sortOptions = listOf(
        SortOption.TITLE to "Song title",
        SortOption.ARTIST to "Artist",
        SortOption.ALBUM to "Album",
        SortOption.FOLDER to "Folder",
        SortOption.DATE_ADDED to "Date added",
        SortOption.PLAY_COUNT to "Play count",
        SortOption.YEAR to "Year",
        SortOption.DURATION to "Duration"
    )

    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 48.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Sort",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Sort options
            sortOptions.forEach { (option, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedSort = option }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedSort == option) AccentPrimary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (selectedSort == option) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    RadioButton(
                        selected = selectedSort == option,
                        onClick = { selectedSort = option },
                        colors = RadioButtonDefaults.colors(selectedColor = AccentPrimary),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sort direction
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedAsc = true }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "A → Z",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selectedAsc) AccentPrimary else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (selectedAsc) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
                RadioButton(
                    selected = selectedAsc,
                    onClick = { selectedAsc = true },
                    colors = RadioButtonDefaults.colors(selectedColor = AccentPrimary),
                    modifier = Modifier.size(20.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedAsc = false }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Z → A",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (!selectedAsc) AccentPrimary else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (!selectedAsc) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
                RadioButton(
                    selected = !selectedAsc,
                    onClick = { selectedAsc = false },
                    colors = RadioButtonDefaults.colors(selectedColor = AccentPrimary),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Text(stringResource(R.string.cancel))
                }
                Button(
                    onClick = { onSelect(selectedSort, selectedAsc) },
                    modifier = Modifier.weight(1f).height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TrackListItem(
    track: Track,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cover art
        Surface(
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
            color = Color.Transparent
        ) {
            // Always draw generative art as background fallback
            com.app.musicplayer.core.ui.components.GenerativeCoverArt(
                seed = track.title + (track.artist ?: ""),
                modifier = Modifier.fillMaxSize()
            )
            // Overlay actual cover if available
            if (track.coverUri != null) {
                AsyncImage(
                    model = track.coverUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // Track info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.displayArtist,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Duration
        Text(
            text = track.durationFormatted,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
fun EmptyLibraryState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = TextSecondary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        }
    }
}
