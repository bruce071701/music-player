package com.app.musicplayer.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.musicplayer.core.ui.theme.AccentPrimary
import com.app.musicplayer.core.ui.theme.TextSecondary
import com.app.musicplayer.core.ui.theme.TextTertiary
import com.app.musicplayer.feature.library.tabs.EmptyLibraryState
import com.app.musicplayer.feature.library.tabs.TrackListItem
import com.app.musicplayer.feature.player.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    var localQuery by rememberSaveable { mutableStateOf("") }
    val searchResults by searchViewModel.searchResults.collectAsState()
    val recentSearches by searchViewModel.recentSearches.collectAsState()
    val quickSuggestions by searchViewModel.quickSuggestions.collectAsState()
    var isActive by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        SearchBar(
            query = localQuery,
            onQueryChange = {
                localQuery = it
                searchViewModel.onQueryChange(it)
            },
            onSearch = { isActive = false },
            active = isActive,
            onActiveChange = { isActive = it },
            placeholder = { Text("Search tracks, artists, albums\u2026") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if (localQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        localQuery = ""
                        searchViewModel.onQueryChange("")
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            // Inline search results while typing
            if (searchResults.isNotEmpty()) {
                LazyColumn {
                    items(searchResults, key = { it.id }) { track ->
                        TrackListItem(
                            track = track,
                            onClick = {
                                playerViewModel.play(track, searchResults)
                                isActive = false
                            }
                        )
                    }
                }
            }
        }

        // Content below search bar
        if (!isActive && localQuery.isEmpty()) {
            // Show tips when no search active
            Spacer(modifier = Modifier.height(24.dp))

            // Quick suggestions from local library
            if (quickSuggestions.isNotEmpty()) {
                Text(
                    text = "From your library",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickSuggestions.forEach { suggestion ->
                        SuggestionChip(
                            onClick = {
                                localQuery = suggestion
                                searchViewModel.onQueryChange(suggestion)
                            },
                            label = { Text(suggestion) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Color(0xFF1E1E2A),
                                labelColor = TextSecondary
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = Color(0xFF3A3A4C)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tips header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.TipsAndUpdates,
                    contentDescription = null,
                    tint = AccentPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Search Tips",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tips
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SearchTip(emoji = "🎵", text = "Search by song title")
                SearchTip(emoji = "🎤", text = "Search by artist name")
                SearchTip(emoji = "💿", text = "Search by album name")
                SearchTip(emoji = "🔍", text = "Searches your local music library")
                SearchTip(emoji = "▶️", text = "Use YouTube tab for online music")
            }

        } else if (!isActive && localQuery.isNotEmpty()) {
            // Show results when search bar is not active
            if (searchResults.isEmpty()) {
                EmptyLibraryState(message = "No results for \"$localQuery\"")
            } else {
                Text(
                    text = "${searchResults.size} results",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(searchResults, key = { it.id }) { track ->
                        TrackListItem(
                            track = track,
                            onClick = { playerViewModel.play(track, searchResults) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchTip(emoji: String, text: String) {
    Text(
        text = "$emoji  $text",
        style = MaterialTheme.typography.bodyMedium,
        color = TextTertiary
    )
}
