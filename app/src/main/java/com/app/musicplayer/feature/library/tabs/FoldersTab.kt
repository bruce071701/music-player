package com.app.musicplayer.feature.library.tabs

import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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
fun FoldersTab(
    folders: List<String> = emptyList(),
    allTracks: List<Track> = emptyList(),
    onFolderClick: (String) -> Unit = {}
) {
    var selectedChip by remember { mutableStateOf("Directory") }

    if (folders.isEmpty()) {
        EmptyLibraryState(message = stringResource(R.string.no_folders))
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
                        stringResource(R.string.folders_count, folders.size),
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

            // Chip buttons: Directory / Hidden Folders
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChipButton(
                        text = stringResource(R.string.directory),
                        isSelected = selectedChip == "Directory",
                        onClick = { selectedChip = "Directory" }
                    )
                    ChipButton(
                        text = stringResource(R.string.hidden_folders),
                        isSelected = selectedChip == "Hidden Folders",
                        onClick = { selectedChip = "Hidden Folders" }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Folder list
            val displayFolders = if (selectedChip == "Hidden Folders") {
                folders.filter { it.contains("/.") }
            } else {
                folders.filter { !it.contains("/.") }
            }

            items(displayFolders) { folder ->
                val folderName = folder.substringAfterLast("/")
                val trackCount = allTracks.count { (it.filePath ?: "").substringBeforeLast("/") == folder }

                FolderListItem(
                    name = folderName,
                    trackCount = trackCount,
                    path = folder,
                    onClick = { onFolderClick(folder) }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun ChipButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (isSelected) Modifier.border(
                    1.5.dp,
                    AccentPrimary,
                    RoundedCornerShape(20.dp)
                ) else Modifier.border(
                    1.dp,
                    TextTertiary.copy(alpha = 0.3f),
                    RoundedCornerShape(20.dp)
                )
            )
            .clickable(onClick = onClick),
        color = if (isSelected) AccentPrimary.copy(alpha = 0.15f) else androidx.compose.ui.graphics.Color.Transparent,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (isSelected) AccentPrimary else TextSecondary
        )
    }
}

@Composable
private fun FolderListItem(
    name: String,
    trackCount: Int,
    path: String,
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
            shape = RoundedCornerShape(10.dp),
            color = AppSurfaceVariant
        ) {
            Icon(
                Icons.Default.Folder,
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
                text = "$trackCount tracks · $path",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
