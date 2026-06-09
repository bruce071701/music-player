package com.app.musicplayer.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.musicplayer.core.database.dao.TrackDao
import com.app.musicplayer.core.database.mapper.toDomainModels
import com.app.musicplayer.core.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val trackDao: TrackDao
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    /**
     * Quick suggestions from user's actual library - artists and albums
     */
    val quickSuggestions: StateFlow<List<String>> = combine(
        trackDao.getAllArtists(),
        trackDao.getAllAlbums()
    ) { artists, albums ->
        val suggestions = mutableListOf<String>()
        // Take top artists (up to 6)
        suggestions.addAll(artists.take(6))
        // Take top albums (up to 6)
        suggestions.addAll(albums.take(6))
        // Shuffle and limit
        suggestions.shuffled().take(12)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchResults: StateFlow<List<Track>> = _query
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(emptyList())
            } else {
                // FTS4 query with prefix matching
                val ftsQuery = query.trim().split(" ")
                    .joinToString(" ") { "$it*" }
                trackDao.searchTracks(ftsQuery)
                    .map { it.toDomainModels() }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }
}
