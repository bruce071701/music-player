package com.app.musicplayer.feature.library

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.penji.musicplayer.offline.R
import com.app.musicplayer.core.common.PermissionHelper
import com.app.musicplayer.feature.library.tabs.AlbumsTab
import com.app.musicplayer.feature.library.tabs.AllTracksTab
import com.app.musicplayer.feature.library.tabs.ArtistsTab
import com.app.musicplayer.feature.library.tabs.FoldersTab
import com.app.musicplayer.feature.library.tabs.GenresTab
import com.app.musicplayer.feature.library.tabs.PlaylistsTab
import com.app.musicplayer.feature.player.PlayerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(
    libraryViewModel: LibraryViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val allTracks by libraryViewModel.allTracks.collectAsState()
    val artists by libraryViewModel.artists.collectAsState()
    val albums by libraryViewModel.albums.collectAsState()
    val genres by libraryViewModel.genres.collectAsState()
    val folders by libraryViewModel.folders.collectAsState()
    val scanProgress by libraryViewModel.scanProgress.collectAsState()

    var hasPermission by remember { mutableStateOf(PermissionHelper.hasAudioPermission(context)) }
    var permanentlyDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            libraryViewModel.scanLibrary()
        } else {
            permanentlyDenied = true
        }
    }

    // Request permission on first launch
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(PermissionHelper.getAudioPermission())
        } else if (allTracks.isEmpty()) {
            libraryViewModel.scanLibrary()
        }
    }

    if (!hasPermission) {
        PermissionScreen(
            onPermissionGranted = {
                hasPermission = true
                libraryViewModel.scanLibrary()
            },
            isPermanentlyDenied = permanentlyDenied
        )
        return
    }

    val tabs = listOf(
        stringResource(R.string.tab_tracks),
        stringResource(R.string.tab_playlists),
        stringResource(R.string.tab_artists),
        stringResource(R.string.tab_albums),
        stringResource(R.string.tab_folders),
        stringResource(R.string.tab_genres)
    )
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.nav_library),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
            },
            actions = {
                IconButton(onClick = { libraryViewModel.scanLibrary() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh library", tint = Color.White.copy(alpha = 0.7f))
                }
            },
            colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Scan progress indicator
        if (scanProgress.isScanning) {
            LinearProgressIndicator(
                progress = {
                    if (scanProgress.totalCount > 0)
                        scanProgress.scannedCount.toFloat() / scanProgress.totalCount.toFloat()
                    else 0f
                },
                modifier = Modifier.fillMaxSize().then(Modifier.fillMaxSize(0f)) // only width
            )
            LinearProgressIndicator(modifier = Modifier.fillMaxSize().then(Modifier.fillMaxSize(0f)))
        }

        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> AllTracksTab(
                    tracks = allTracks,
                    onTrackClick = { track ->
                        playerViewModel.play(track, allTracks)
                    }
                )
                1 -> PlaylistsTab(
                    libraryViewModel = libraryViewModel,
                    playerViewModel = playerViewModel
                )
                2 -> ArtistsTab(
                    artists = artists,
                    onArtistClick = { }
                )
                3 -> AlbumsTab(
                    albums = albums,
                    onAlbumClick = { }
                )
                4 -> FoldersTab(
                    folders = folders,
                    onFolderClick = { }
                )
                5 -> GenresTab(
                    genres = genres,
                    onGenreClick = { }
                )
            }
        }
    }
}
