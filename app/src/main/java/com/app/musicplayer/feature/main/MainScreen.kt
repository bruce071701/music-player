package com.app.musicplayer.feature.main

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.penji.musicplayer.offline.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.musicplayer.core.common.PermissionHelper
import com.app.musicplayer.core.ui.theme.AccentPrimary
import com.app.musicplayer.core.ui.theme.AppSurfaceVariant
import com.app.musicplayer.core.ui.theme.TextSecondary
import com.app.musicplayer.core.ui.theme.TextTertiary
import com.app.musicplayer.feature.library.LibraryViewModel
import com.app.musicplayer.feature.library.PermissionScreen
import com.app.musicplayer.feature.library.tabs.AlbumsTab
import com.app.musicplayer.feature.library.tabs.AllTracksTab
import com.app.musicplayer.feature.library.tabs.ArtistsTab
import com.app.musicplayer.feature.library.tabs.FoldersTab
import com.app.musicplayer.feature.library.tabs.GenresTab
import com.app.musicplayer.feature.library.tabs.HomeTab
import com.app.musicplayer.feature.library.tabs.PlaylistsTab
import com.app.musicplayer.feature.player.MiniPlayer
import com.app.musicplayer.feature.player.PlayerScreen
import com.app.musicplayer.feature.player.PlayerViewModel
import com.app.musicplayer.feature.search.SearchScreen
import com.app.musicplayer.feature.settings.SettingsScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    playerViewModel: PlayerViewModel = hiltViewModel(),
    libraryViewModel: LibraryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isPlayerExpanded by playerViewModel.isPlayerExpanded.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val allTracks by libraryViewModel.allTracks.collectAsState()
    val artists by libraryViewModel.artists.collectAsState()
    val albums by libraryViewModel.albums.collectAsState()
    val genres by libraryViewModel.genres.collectAsState()
    val folders by libraryViewModel.folders.collectAsState()

    var hasPermission by remember { mutableStateOf(PermissionHelper.hasAudioPermission(context)) }
    var permanentlyDenied by remember { mutableStateOf(false) }

    // Navigation state: "main", "search", "video", "settings"
    var currentScreen by rememberSaveable { mutableStateOf("main") }
    var previousScreen by rememberSaveable { mutableStateOf("main") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            permanentlyDenied = true
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(PermissionHelper.getAudioPermission())
        }
    }

    if (!hasPermission) {
        PermissionScreen(
            onPermissionGranted = {
                hasPermission = true
            },
            isPermanentlyDenied = permanentlyDenied
        )
        return
    }

    // Shared tab index for bottom nav <-> pager sync
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        when (currentScreen) {
            "search" -> {
                Column(modifier = Modifier.weight(1f)) {
                    SearchScreen()
                }
            }
            "video" -> {
                Column(modifier = Modifier.weight(1f)) {
                    com.app.musicplayer.feature.video.VideoPlayerScreen(
                        onBack = { currentScreen = "main" }
                    )
                }
            }
            "settings" -> {
                Column(modifier = Modifier.weight(1f)) {
                    SettingsScreen(
                        onBack = { currentScreen = "main" },
                        onNavigateToEqualizer = { currentScreen = "equalizer" },
                        onNavigateToTransfer = { currentScreen = "transfer" },
                        onNavigateToBackup = { currentScreen = "backup" },
                        onNavigateToScan = { previousScreen = "settings"; currentScreen = "scan" }
                    )
                }
            }
            "equalizer" -> {
                Column(modifier = Modifier.weight(1f)) {
                    com.app.musicplayer.feature.equalizer.EqualizerScreen(
                        onNavigateBack = { currentScreen = "settings" }
                    )
                }
            }
            "transfer" -> {
                Column(modifier = Modifier.weight(1f)) {
                    com.app.musicplayer.feature.transfer.MusicTransferScreen(
                        onBack = { currentScreen = "settings" }
                    )
                }
            }
            "backup" -> {
                Column(modifier = Modifier.weight(1f)) {
                    com.app.musicplayer.feature.backup.BackupRestoreScreen(
                        onBack = { currentScreen = "settings" }
                    )
                }
            }
            "scan" -> {
                Column(modifier = Modifier.weight(1f)) {
                    com.app.musicplayer.feature.scan.ScanMusicScreen(
                        onBack = { currentScreen = previousScreen }
                    )
                }
            }
            else -> {
                // Main content with tabs (takes remaining space)
                MainContent(
                    playerViewModel = playerViewModel,
                    libraryViewModel = libraryViewModel,
                    allTracks = allTracks,
                    artists = artists,
                    albums = albums,
                    genres = genres,
                    folders = folders,
                    onSearchClick = { currentScreen = "search" },
                    onVideoClick = { currentScreen = "video" },
                    onSettingsClick = { currentScreen = "settings" },
                    onScanClick = { previousScreen = "main"; currentScreen = "scan" },
                    selectedTabIndex = selectedTabIndex,
                    onTabChanged = { selectedTabIndex = it },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // MiniPlayer - between content and bottom nav
        MiniPlayer(onExpandPlayer = { playerViewModel.expandPlayer() })

        // Bottom Navigation Bar - at the very bottom
        if (currentScreen == "main") {
            BottomNavigationBar(
                selectedIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )
        } else {
            // When bottom nav is hidden, add nav bar padding so MiniPlayer isn't clipped
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }

    if (isPlayerExpanded) {
        ModalBottomSheet(
            onDismissRequest = { playerViewModel.collapsePlayer() },
            sheetState = sheetState,
            containerColor = Color.Black,
            scrimColor = Color.Black.copy(alpha = 0.9f),
            dragHandle = null,
            modifier = Modifier.fillMaxSize()
        ) {
            PlayerScreen(viewModel = playerViewModel, onDismiss = { playerViewModel.collapsePlayer() })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    playerViewModel: PlayerViewModel,
    libraryViewModel: LibraryViewModel,
    allTracks: List<com.app.musicplayer.core.model.Track>,
    artists: List<String>,
    albums: List<String>,
    genres: List<String>,
    folders: List<String>,
    onSearchClick: () -> Unit,
    onVideoClick: () -> Unit = {},
    onSettingsClick: () -> Unit,
    onScanClick: () -> Unit = {},
    selectedTabIndex: Int = 0,
    onTabChanged: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        stringResource(R.string.tab_home),
        stringResource(R.string.tab_tracks),
        stringResource(R.string.tab_playlists),
        stringResource(R.string.tab_folders),
        stringResource(R.string.tab_albums),
        stringResource(R.string.tab_artists),
        stringResource(R.string.tab_genres)
    )
    val pagerState = rememberPagerState(initialPage = selectedTabIndex, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    // Sync pager with external selectedTabIndex
    LaunchedEffect(selectedTabIndex) {
        if (pagerState.currentPage != selectedTabIndex) {
            pagerState.animateScrollToPage(selectedTabIndex)
        }
    }

    // Notify parent when pager changes
    LaunchedEffect(pagerState.currentPage) {
        onTabChanged(pagerState.currentPage)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Top bar: App name + action icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MUSIC",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
            }
            IconButton(onClick = onVideoClick) {
                Icon(Icons.Default.VideoLibrary, "Video", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, "Settings", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
            }
        }

        // Horizontal scrollable chip tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = pagerState.currentPage == index
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .then(
                            if (!isSelected) Modifier.border(
                                1.dp,
                                TextTertiary.copy(alpha = 0.4f),
                                RoundedCornerShape(20.dp)
                            ) else Modifier
                        )
                        .clickable {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                    color = if (isSelected) AccentPrimary else Color.Transparent,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Pager content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> HomeTab(
                    libraryViewModel = libraryViewModel,
                    playerViewModel = playerViewModel,
                    onNavigateToScan = onScanClick
                )
                1 -> AllTracksTab(
                    tracks = allTracks,
                    onTrackClick = { track -> playerViewModel.play(track, allTracks) }
                )
                2 -> PlaylistsTab(
                    libraryViewModel = libraryViewModel,
                    playerViewModel = playerViewModel
                )
                3 -> FoldersTab(
                    folders = folders,
                    allTracks = allTracks,
                    onFolderClick = { }
                )
                4 -> AlbumsTab(
                    albums = albums,
                    onAlbumClick = { }
                )
                5 -> ArtistsTab(
                    artists = artists,
                    allTracks = allTracks,
                    onArtistClick = { }
                )
                6 -> GenresTab(
                    genres = genres,
                    onScanClick = { libraryViewModel.scanLibrary() }
                )
            }
        }
    }
}

/**
 * Bottom navigation bar matching the reference design.
 * Shows Home, Tracks, Playlists, Folders with icons and labels.
 * Synced with pager state.
 */
@Composable
private fun BottomNavigationBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    data class NavItem(
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
        val labelRes: Int
    )

    val items = listOf(
        NavItem(Icons.Default.Home, R.string.tab_home),
        NavItem(Icons.Default.MusicNote, R.string.tab_tracks),
        NavItem(Icons.Default.QueueMusic, R.string.tab_playlists),
        NavItem(Icons.Default.Folder, R.string.tab_folders)
    )

    // Map pager indices to bottom nav: 0->0, 1->1, 2->2, 3->3
    val activeNavIndex = if (selectedIndex in 0..3) selectedIndex else -1

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0F0F0F)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == activeNavIndex
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.labelRes),
                        tint = if (isSelected) AccentPrimary else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(item.labelRes),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (isSelected) AccentPrimary else Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
