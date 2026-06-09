# Android 音乐播放器 — 架构详细设计文档

## 1. 架构概览

### 1.1 整体架构风格

采用 **Clean Architecture + MVI (Model-View-Intent)** 分层架构，结合 Android 推荐的单 Activity 多 Compose Screen 模式。

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                     │
│  (Compose UI + ViewModel + MVI State/Event/Effect)       │
├─────────────────────────────────────────────────────────┤
│                     Domain Layer                          │
│  (Use Cases + Repository Interfaces + Domain Models)     │
├─────────────────────────────────────────────────────────┤
│                      Data Layer                           │
│  (Repository Impl + Room + DataStore + MediaStore +      │
│   Network APIs + MediaSession)                           │
├─────────────────────────────────────────────────────────┤
│                   Framework Layer                         │
│  (ExoPlayer + MediaSessionService + Broadcast Receivers  │
│   + Widget + Android Auto)                               │
└─────────────────────────────────────────────────────────┘
```

### 1.2 技术栈

| 类别 | 技术选型 |
|------|----------|
| 语言 | Kotlin 1.9+ |
| UI | Jetpack Compose + Material 3 |
| 导航 | Navigation Compose |
| DI | Hilt |
| 异步 | Kotlin Coroutines + Flow |
| 数据库 | Room + FTS4 |
| 偏好存储 | DataStore Preferences |
| 播放器 | Media3 ExoPlayer + FFmpeg Extension |
| 会话 | Media3 MediaSession + MediaSessionService |
| 网络 | Retrofit + OkHttp |
| 图片 | Coil Compose |
| 元数据 | JAudioTagger |
| 颜色提取 | Palette API |
| Widget | Glance API |
| YouTube | android-youtube-player SDK |
| 序列化 | Kotlinx Serialization |
| 构建工具 | Gradle KTS + KSP |

### 1.3 模块划分

```
:app                          → Application 入口、Hilt Application、MainActivity
:core:common                  → 工具类、扩展函数、常量定义
:core:model                   → 领域模型 (Track, Playlist, EqPreset 等)
:core:database                → Room 数据库、Entity、DAO、Migration
:core:datastore               → DataStore 偏好设置管理
:core:network                 → Retrofit API 定义 (YouTube/Last.fm/MusicBrainz)
:core:media                   → ExoPlayer 封装、MediaSessionService、音频处理
:core:ui                      → 通用 Compose 组件、Theme、Icons
:feature:library              → 曲库功能 (全部/专辑/艺术家/文件夹/流派)
:feature:player               → 播放器 (MiniPlayer + 全屏 PlayerScreen + 歌词)
:feature:search               → 本地搜索 (FTS)
:feature:youtube              → YouTube 搜索与播放
:feature:settings             → 设置页面
:feature:equalizer            → 均衡器
:feature:widget               → 桌面 Widget (Glance)
```

---

## 2. 分层详细设计

### 2.1 Presentation Layer (表现层)

#### MVI 模式规范

每个 Feature Screen 包含：
- **State**: 不可变数据类，描述 UI 当前完整状态
- **Event**: 用户操作 / 系统事件的密封类
- **Effect**: 一次性副作用（导航、Toast、弹窗）

```kotlin
// 示例：PlayerScreen
data class PlayerState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val duration: Long = 0L,
    val playMode: PlayMode = PlayMode.SEQUENCE,
    val isFavorite: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val queue: List<Track> = emptyList()
)

sealed interface PlayerEvent {
    data object PlayPause : PlayerEvent
    data object Next : PlayerEvent
    data object Previous : PlayerEvent
    data class SeekTo(val position: Long) : PlayerEvent
    data class SetPlayMode(val mode: PlayMode) : PlayerEvent
    data object ToggleFavorite : PlayerEvent
    data class SetSpeed(val speed: Float) : PlayerEvent
}

sealed interface PlayerEffect {
    data class ShowError(val message: String) : PlayerEffect
    data object NavigateToLyrics : PlayerEffect
}
```

#### ViewModel 基类

```kotlin
abstract class MviViewModel<State, Event, Effect>(
    initialState: State
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    fun onEvent(event: Event) { handleEvent(event) }
    protected abstract fun handleEvent(event: Event)
    protected fun updateState(reducer: State.() -> State) {
        _state.update(reducer)
    }
    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
```

### 2.2 Domain Layer (领域层)

#### 领域模型

```kotlin
data class Track(
    val id: Long,
    val source: TrackSource,       // LOCAL, YOUTUBE
    val title: String,
    val artist: String?,
    val albumArtist: String?,
    val album: String?,
    val durationMs: Long,
    val trackNumber: Int?,
    val discNumber: Int?,
    val year: Int?,
    val genre: String?,
    val filePath: String?,
    val youtubeId: String?,
    val coverUri: String?,
    val bitrate: Int?,
    val sampleRate: Int?,
    val fileSize: Long?,
    val replayGainTrack: Float?,
    val replayGainAlbum: Float?,
    val rating: Int,
    val playCount: Int,
    val lastPlayedAt: Long?,
    val isFavorite: Boolean,
    val addedAt: Long
)

data class Playlist(
    val id: Long,
    val name: String,
    val type: PlaylistType,        // USER, SMART, AUTO
    val smartRules: SmartRules?,
    val coverUri: String?,
    val trackCount: Int,
    val createdAt: Long,
    val updatedAt: Long
)

data class EqPreset(
    val id: Long,
    val name: String,
    val isBuiltin: Boolean,
    val bands: List<Float>,        // 10-band gains
    val preamp: Float
)

enum class PlayMode { SEQUENCE, REPEAT_ONE, REPEAT_ALL, SHUFFLE }
enum class TrackSource { LOCAL, YOUTUBE }
enum class PlaylistType { USER, SMART, AUTO }
```

#### Repository 接口

```kotlin
interface TrackRepository {
    fun getAllTracks(): Flow<List<Track>>
    fun getTracksByArtist(artist: String): Flow<List<Track>>
    fun getTracksByAlbum(album: String): Flow<List<Track>>
    fun getTracksByGenre(genre: String): Flow<List<Track>>
    fun getTracksByFolder(folderPath: String): Flow<List<Track>>
    fun getFavorites(): Flow<List<Track>>
    fun getRecentlyPlayed(limit: Int): Flow<List<Track>>
    fun getMostPlayed(limit: Int): Flow<List<Track>>
    fun searchTracks(query: String): Flow<List<Track>>
    suspend fun updateTrack(track: Track)
    suspend fun toggleFavorite(trackId: Long)
    suspend fun incrementPlayCount(trackId: Long)
}

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun createPlaylist(name: String): Long
    suspend fun deletePlaylist(id: Long)
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long)
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
    suspend fun reorderTracks(playlistId: Long, fromIndex: Int, toIndex: Int)
    fun getPlaylistTracks(playlistId: Long): Flow<List<Track>>
}

interface MediaScannerRepository {
    suspend fun scanMediaStore(): List<Track>
    suspend fun scanWithMetadata(tracks: List<Track>): List<Track>
    fun observeMediaChanges(): Flow<MediaChangeEvent>
}

interface PlaybackRepository {
    val playerState: StateFlow<PlayerState>
    val currentTrack: StateFlow<Track?>
    val queue: StateFlow<List<Track>>
    suspend fun play(track: Track, queue: List<Track>)
    suspend fun playPause()
    suspend fun next()
    suspend fun previous()
    suspend fun seekTo(positionMs: Long)
    suspend fun setPlayMode(mode: PlayMode)
    suspend fun setSpeed(speed: Float)
    suspend fun setCrossfadeDuration(ms: Int)
    suspend fun setReplayGainMode(mode: ReplayGainMode)
}
```

#### Use Cases

```kotlin
class ScanLocalMusicUseCase @Inject constructor(
    private val scannerRepo: MediaScannerRepository,
    private val trackRepo: TrackRepository
)

class PlayTrackUseCase @Inject constructor(
    private val playbackRepo: PlaybackRepository,
    private val historyRepo: PlayHistoryRepository
)

class SearchTracksUseCase @Inject constructor(
    private val trackRepo: TrackRepository
)

class ManagePlaylistUseCase @Inject constructor(
    private val playlistRepo: PlaylistRepository
)

class ScrobbleUseCase @Inject constructor(
    private val lastFmRepo: LastFmRepository,
    private val historyRepo: PlayHistoryRepository
)
```

### 2.3 Data Layer (数据层)

#### Room 数据库

```kotlin
@Database(
    entities = [
        TrackEntity::class,
        TrackFtsEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class,
        PlayHistoryEntity::class,
        EqPresetEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playHistoryDao(): PlayHistoryDao
    abstract fun eqPresetDao(): EqPresetDao
}
```

#### DataStore 设置

```kotlin
@Singleton
class AppPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    // 播放设置
    val gaplessEnabled: Flow<Boolean>
    val crossfadeDuration: Flow<Int>           // 0 = off, 500-12000ms
    val replayGainMode: Flow<ReplayGainMode>   // OFF, TRACK, ALBUM
    val defaultPlaybackSpeed: Flow<Float>
    val autoResumeOnBoot: Flow<Boolean>
    val pauseOnHeadphoneDisconnect: Flow<Boolean>

    // 曲库设置
    val scanMode: Flow<ScanMode>               // ALL, BLACKLIST, WHITELIST
    val blacklistFolders: Flow<Set<String>>
    val whitelistFolders: Flow<Set<String>>
    val minTrackDuration: Flow<Int>            // seconds

    // 主题设置
    val themeMode: Flow<ThemeMode>             // SYSTEM, LIGHT, DARK, AMOLED

    // 播放状态持久化（服务被杀后恢复用）
    val lastQueueJson: Flow<String>
    val lastTrackId: Flow<Long>
    val lastPositionMs: Flow<Long>
}
```

### 2.4 Framework Layer (框架层)

#### MediaSessionService

```kotlin
@AndroidEntryPoint
class MusicPlaybackService : MediaSessionService() {

    @Inject lateinit var exoPlayer: ExoPlayer
    @Inject lateinit var preferences: AppPreferences

    private lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setCallback(MediaSessionCallback())
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    inner class MediaSessionCallback : MediaSession.Callback {
        // 处理外部控制（通知栏、蓝牙耳机、Android Auto）
    }
}
```

#### ExoPlayer 配置

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context
    ): ExoPlayer {
        val ffmpegRenderer = FfmpegAudioRenderer(/* ... */)

        return ExoPlayer.Builder(context)
            .setRenderersFactory { eventHandler, _, audioRendererEventListener, _, _ ->
                arrayOf(
                    MediaCodecAudioRenderer(context, MediaCodecSelector.DEFAULT,
                        eventHandler, audioRendererEventListener),
                    ffmpegRenderer
                )
            }
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                /* handleAudioFocus = */ true
            )
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()
    }
}
```

---

## 3. 导航架构

### 3.1 Navigation Graph

```kotlin
@Composable
fun MusicNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = "library") {
        // 底部 Tab 目标
        composable("library") { LibraryScreen(navController) }
        composable("search") { SearchScreen(navController) }
        composable("youtube") { YouTubeScreen(navController) }
        composable("settings") { SettingsScreen(navController) }

        // 详情页
        composable("artist/{artistName}") { ArtistDetailScreen(it, navController) }
        composable("album/{albumId}") { AlbumDetailScreen(it, navController) }
        composable("playlist/{playlistId}") { PlaylistDetailScreen(it, navController) }
        composable("equalizer") { EqualizerScreen(navController) }
        composable("youtube_player/{videoId}") { YouTubePlayerScreen(it) }
    }
}
```

### 3.2 全屏播放器

全屏播放器使用 `ModalBottomSheet` 覆盖在 NavHost 之上，不参与 Navigation Graph，由全局 PlayerViewModel 控制展开/收起。

```kotlin
@Composable
fun MainScreen() {
    val playerVm: PlayerViewModel = hiltViewModel()
    val showPlayer by playerVm.isPlayerExpanded.collectAsState()

    Scaffold(
        bottomBar = {
            Column {
                MiniPlayer(onExpand = { playerVm.expandPlayer() })
                BottomNavigationBar(navController)
            }
        }
    ) { padding ->
        MusicNavHost(navController, Modifier.padding(padding))
    }

    if (showPlayer) {
        ModalBottomSheet(onDismissRequest = { playerVm.collapsePlayer() }) {
            PlayerScreen(playerVm)
        }
    }
}
```

---

## 4. 播放引擎设计

### 4.1 播放队列管理

```kotlin
class PlayQueueManager @Inject constructor() {
    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val queue: StateFlow<List<Track>> = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private var originalQueue: List<Track> = emptyList()
    private var shuffledIndices: List<Int> = emptyList()

    fun setQueue(tracks: List<Track>, startIndex: Int)
    fun next(): Track?
    fun previous(): Track?
    fun shuffle(enabled: Boolean)
    fun moveTrack(from: Int, to: Int)
    fun removeTrack(index: Int)
    fun addNext(track: Track)
    fun addToEnd(track: Track)
}
```

### 4.2 Gapless / Crossfade 实现

```kotlin
class CrossfadeController @Inject constructor(
    private val player: ExoPlayer,
    private val preferences: AppPreferences
) {
    // Gapless: ExoPlayer 原生支持，确保 MediaSource 按序添加
    // Crossfade: 使用 MixingAudioSink 或双 Player 方案

    suspend fun configureCrossfade() {
        preferences.crossfadeDuration.collect { duration ->
            if (duration > 0) {
                // 启用 Crossfade：在当前曲目剩余 duration 时开始淡出
                // 同时开始播放下一首（淡入）
                enableCrossfade(duration)
            } else {
                // Gapless 模式
                disableCrossfade()
            }
        }
    }
}
```

### 4.3 ReplayGain

```kotlin
class ReplayGainProcessor @Inject constructor(
    private val preferences: AppPreferences
) {
    fun calculateGain(track: Track): Float {
        return when (preferences.replayGainMode.first()) {
            ReplayGainMode.TRACK -> track.replayGainTrack ?: 0f
            ReplayGainMode.ALBUM -> track.replayGainAlbum ?: track.replayGainTrack ?: 0f
            ReplayGainMode.OFF -> 0f
        }
    }

    fun applyGain(player: ExoPlayer, gainDb: Float) {
        val volume = 10f.pow(gainDb / 20f).coerceIn(0f, 1f)
        player.volume = volume
    }
}
```

### 4.4 均衡器 (自实现 BiquadFilter)

```kotlin
class AudioEqualizer @Inject constructor() {
    // 10-band center frequencies: 31, 62, 125, 250, 500, 1K, 2K, 4K, 8K, 16K Hz
    private val centerFrequencies = intArrayOf(31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000)

    // 每段使用双二阶滤波器 (Biquad Peaking EQ)
    private val filters = Array(10) { BiquadFilter() }

    fun setBandGain(band: Int, gainDb: Float) {
        filters[band].configurePeaking(
            sampleRate = 44100,
            centerFreq = centerFrequencies[band],
            gainDb = gainDb,
            q = 1.414  // sqrt(2)，标准 Q 值
        )
    }

    fun process(buffer: ShortArray): ShortArray {
        // 逐样本通过 10 段滤波器链
        for (i in buffer.indices) {
            var sample = buffer[i].toFloat()
            for (filter in filters) {
                sample = filter.process(sample)
            }
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toFloat(), Short.MAX_VALUE.toFloat()).toInt().toShort()
        }
        return buffer
    }
}
```

---

## 5. 曲库扫描设计

### 5.1 扫描流程

```
┌──────────────┐     ┌──────────────────┐     ┌─────────────────┐
│  MediaStore  │────▶│  Filter (黑白名单/ │────▶│  JAudioTagger   │
│  Query       │     │  时长/MIME)        │     │  Metadata Parse │
└──────────────┘     └──────────────────┘     └────────┬────────┘
                                                        │
                                                        ▼
                                              ┌─────────────────┐
                                              │  Room Upsert     │
                                              │  (tracks 表)     │
                                              └────────┬────────┘
                                                        │
                                                        ▼
                                              ┌─────────────────┐
                                              │  Emit Flow       │
                                              │  更新 UI         │
                                              └─────────────────┘
```

### 5.2 FileObserver 增量监听

```kotlin
class MediaFileObserver @Inject constructor(
    private val trackRepo: TrackRepository
) {
    private var observers: List<FileObserver> = emptyList()

    fun startWatching(folders: List<String>) {
        observers = folders.map { path ->
            object : FileObserver(File(path), CREATE or DELETE or MOVED_FROM or MOVED_TO) {
                override fun onEvent(event: Int, path: String?) {
                    // 增量更新 Room 数据
                }
            }.also { it.startWatching() }
        }
    }
}
```

---

## 6. 后台服务与系统集成

### 6.1 Notification & Lock Screen

MediaSessionService 自动生成 MediaStyle Notification，显示：
- 封面图
- 曲名 + 艺术家
- 播放/暂停、上一首、下一首按钮
- 进度条（Android 13+）

### 6.2 Audio Focus 策略

| 事件 | 行为 |
|------|------|
| AUDIOFOCUS_GAIN | 恢复播放（如果之前因 transient loss 暂停） |
| AUDIOFOCUS_LOSS | 暂停播放，不自动恢复 |
| AUDIOFOCUS_LOSS_TRANSIENT | 暂停播放，事件结束后自动恢复 |
| AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK | 降低音量到 20%，事件结束后恢复 |

### 6.3 耳机事件处理

```kotlin
// ExoPlayer.Builder 已设置 setHandleAudioBecomingNoisy(true)
// 自动处理耳机拔出暂停
// 蓝牙耳机媒体按键通过 MediaSession.Callback 自动响应
```

### 6.4 状态持久化（防杀恢复）

```kotlin
class PlaybackStateSaver @Inject constructor(
    private val preferences: AppPreferences
) {
    // 每 5 秒 或 曲目切换时 保存当前状态到 DataStore
    suspend fun saveState(trackId: Long, positionMs: Long, queue: List<Long>) {
        preferences.setLastTrackId(trackId)
        preferences.setLastPositionMs(positionMs)
        preferences.setLastQueue(queue.joinToString(","))
    }

    suspend fun restoreState(): PlaybackRestorationData? {
        // 读取 DataStore 恢复
    }
}
```

---

## 7. 网络层设计

### 7.1 YouTube Data API v3

```kotlin
interface YouTubeApiService {
    @GET("youtube/v3/search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 25,
        @Query("key") apiKey: String
    ): YouTubeSearchResponse
}
```

### 7.2 Last.fm API (Web Auth + Scrobble)

```kotlin
interface LastFmApiService {
    @FormUrlEncoded
    @POST("2.0/")
    suspend fun scrobble(
        @Field("method") method: String = "track.scrobble",
        @Field("artist") artist: String,
        @Field("track") track: String,
        @Field("timestamp") timestamp: Long,
        @Field("api_key") apiKey: String,
        @Field("sk") sessionKey: String,
        @Field("api_sig") signature: String
    ): LastFmResponse
}
```

### 7.3 MusicBrainz Cover Art (限速队列)

基于 PRD 附录 C 的限速策略，使用协程 Channel + delay 实现 1 req/s 限制。

---

## 8. Widget 设计 (Glance API)

### 8.1 三种尺寸

| 尺寸 | 内容 | 交互 |
|------|------|------|
| 4×2 | 封面 + 曲名 + 播放/暂停/上下首 + 进度条 | 完整控制 |
| 4×1 | 曲名 + 播放/暂停/上下首 | 紧凑控制 |
| 2×2 | 封面 + 播放/暂停 | 最小控制 |

### 8.2 数据同步

Widget 通过 `GlanceAppWidgetReceiver` 监听 MediaSession 状态变化，使用 `WorkManager` 定时刷新（间隔 ≤ 2s）。

---

## 9. 错误处理架构

### 9.1 统一错误类型

```kotlin
sealed class AppError {
    data class FileNotFound(val path: String) : AppError()
    data class DecodeFailed(val track: Track, val cause: Throwable) : AppError()
    data class PermissionDenied(val permission: String) : AppError()
    data class NetworkError(val cause: Throwable) : AppError()
    data class ApiQuotaExhausted(val service: String) : AppError()
    data class PlaybackError(val cause: Throwable) : AppError()
}
```

### 9.2 错误处理策略

```kotlin
class ErrorHandler @Inject constructor() {
    fun handlePlaybackError(error: PlaybackException, queueManager: PlayQueueManager) {
        when {
            error.isFileNotFound() -> {
                // 标记曲目，跳到下一首
                queueManager.next()
                showToast("文件不存在，已跳过")
            }
            error.isDecodeFailed() -> {
                queueManager.next()
                showToast("无法播放该文件")
            }
            else -> {
                // 停止播放，显示错误
                showToast("播放出现错误")
            }
        }
    }
}
```

---

## 10. 包结构 (完整目录树)

```
com.app.musicplayer/
├── MusicPlayerApplication.kt
├── di/
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   ├── NetworkModule.kt
│   ├── PlayerModule.kt
│   └── RepositoryModule.kt
├── core/
│   ├── model/
│   │   ├── Track.kt
│   │   ├── Playlist.kt
│   │   ├── PlayMode.kt
│   │   ├── EqPreset.kt
│   │   └── SmartRules.kt
│   ├── database/
│   │   ├── MusicDatabase.kt
│   │   ├── entity/
│   │   │   ├── TrackEntity.kt
│   │   │   ├── TrackFtsEntity.kt
│   │   │   ├── PlaylistEntity.kt
│   │   │   ├── PlaylistTrackEntity.kt
│   │   │   ├── PlayHistoryEntity.kt
│   │   │   └── EqPresetEntity.kt
│   │   ├── dao/
│   │   │   ├── TrackDao.kt
│   │   │   ├── PlaylistDao.kt
│   │   │   ├── PlayHistoryDao.kt
│   │   │   └── EqPresetDao.kt
│   │   └── mapper/
│   │       ├── TrackMapper.kt
│   │       └── PlaylistMapper.kt
│   ├── datastore/
│   │   └── AppPreferences.kt
│   ├── network/
│   │   ├── youtube/
│   │   │   ├── YouTubeApiService.kt
│   │   │   └── YouTubeModels.kt
│   │   ├── lastfm/
│   │   │   ├── LastFmApiService.kt
│   │   │   ├── LastFmAuthManager.kt
│   │   │   └── LastFmModels.kt
│   │   └── musicbrainz/
│   │       ├── MusicBrainzApiService.kt
│   │       └── CoverArtQueue.kt
│   ├── media/
│   │   ├── MusicPlaybackService.kt
│   │   ├── PlayQueueManager.kt
│   │   ├── CrossfadeController.kt
│   │   ├── ReplayGainProcessor.kt
│   │   ├── AudioEqualizer.kt
│   │   ├── BiquadFilter.kt
│   │   ├── SleepTimer.kt
│   │   ├── PlaybackStateSaver.kt
│   │   └── AudioFocusHandler.kt
│   └── ui/
│       ├── theme/
│       │   ├── Theme.kt
│       │   ├── Color.kt
│       │   ├── Type.kt
│       │   └── DynamicColorExtractor.kt
│       ├── components/
│       │   ├── TrackListItem.kt
│       │   ├── AlbumGridItem.kt
│       │   ├── ArtistListItem.kt
│       │   ├── SearchBar.kt
│       │   ├── EmptyState.kt
│       │   ├── ErrorState.kt
│       │   └── LoadingIndicator.kt
│       └── icons/
│           └── MusicIcons.kt
├── feature/
│   ├── library/
│   │   ├── LibraryScreen.kt
│   │   ├── LibraryViewModel.kt
│   │   ├── tabs/
│   │   │   ├── AllTracksTab.kt
│   │   │   ├── ArtistsTab.kt
│   │   │   ├── AlbumsTab.kt
│   │   │   ├── FoldersTab.kt
│   │   │   └── GenresTab.kt
│   │   ├── detail/
│   │   │   ├── ArtistDetailScreen.kt
│   │   │   ├── AlbumDetailScreen.kt
│   │   │   └── PlaylistDetailScreen.kt
│   │   └── scanner/
│   │       ├── MediaScannerWorker.kt
│   │       └── MetadataExtractor.kt
│   ├── player/
│   │   ├── PlayerScreen.kt
│   │   ├── PlayerViewModel.kt
│   │   ├── MiniPlayer.kt
│   │   ├── LyricsScreen.kt
│   │   ├── LyricsParser.kt
│   │   └── GestureHandler.kt
│   ├── search/
│   │   ├── SearchScreen.kt
│   │   └── SearchViewModel.kt
│   ├── youtube/
│   │   ├── YouTubeSearchScreen.kt
│   │   ├── YouTubePlayerScreen.kt
│   │   └── YouTubeViewModel.kt
│   ├── settings/
│   │   ├── SettingsScreen.kt
│   │   └── SettingsViewModel.kt
│   ├── equalizer/
│   │   ├── EqualizerScreen.kt
│   │   └── EqualizerViewModel.kt
│   └── widget/
│       ├── MusicWidget4x2.kt
│       ├── MusicWidget4x1.kt
│       ├── MusicWidget2x2.kt
│       └── MusicWidgetReceiver.kt
├── receiver/
│   └── BootReceiver.kt
└── auto/
    └── MusicAutoService.kt
```

---

## 11. 关键设计决策

| 决策 | 选择 | 理由 |
|------|------|------|
| 架构模式 | Clean Architecture + MVI | 单向数据流，状态可预测，适合复杂播放状态管理 |
| 单 Activity | 是 | Compose Navigation 推荐方式，共享 ViewModel 更方便 |
| 全屏播放器 | ModalBottomSheet | 符合 PRD 手势设计，下滑关闭直觉自然 |
| EQ 实现 | 自实现 BiquadFilter | 系统 EQ API 各厂商差异大，自实现保证一致性 |
| 播放服务 | MediaSessionService | Media3 推荐方式，自动处理通知/锁屏/Auto |
| Crossfade | 双 AudioSink 方案 | ExoPlayer 不原生支持 Crossfade，需自行混音 |
| Widget | Glance API | 声明式 UI，与 Compose 风格一致 |
| 存储 | Room + DataStore | 结构化数据用 Room，KV 配置用 DataStore |
| FFmpeg | Media3 FFmpeg Extension | 官方扩展，覆盖 APE/WMA/DSD 等格式 |

---

## 12. 实施计划映射

| Phase | 涉及模块 | 核心产出 |
|-------|----------|----------|
| Phase 1 | :app, :core:database, :core:ui, :core:common | 空框架 + Theme + DB Schema |
| Phase 2 | :feature:library, :core:media (scanner) | 曲库浏览完整 |
| Phase 3 | :core:media (player), :feature:player | 播放功能可用 |
| Phase 4 | :core:media (service) | 后台播放 + 通知栏 |
| Phase 5 | :feature:search, :feature:library (scanner增强) | FTS 搜索 + 扫描优化 |
| Phase 6 | :core:media (crossfade/replaygain/sleep) | 播放增强 |
| Phase 7 | :feature:equalizer, :core:media (EQ) | 均衡器完整 |
| Phase 8 | :feature:player (lyrics) | 歌词功能 |
| Phase 9a | :feature:widget | Widget |
| Phase 9b | :auto, :core:network (lastfm) | Auto + Scrobble |
| Phase 10 | :feature:youtube, :core:network (youtube) | YouTube 功能 |
| Phase 11 | 全局 | 错误处理 |
| Phase 12 | :app | 发布准备 |

---

## 13. API Key 安全方案

```properties
# local.properties (不提交到 Git)
YOUTUBE_API_KEY=your_key_here
LASTFM_API_KEY=your_key_here
LASTFM_API_SECRET=your_secret_here
MUSICBRAINZ_USER_AGENT=MusicPlayer/1.0 (contact@email.com)
```

```kotlin
// build.gradle.kts
android {
    defaultConfig {
        val properties = gradleLocalProperties(rootDir)
        buildConfigField("String", "YOUTUBE_API_KEY",
            "\"${properties.getProperty("YOUTUBE_API_KEY", "")}\"")
        buildConfigField("String", "LASTFM_API_KEY",
            "\"${properties.getProperty("LASTFM_API_KEY", "")}\"")
        buildConfigField("String", "LASTFM_API_SECRET",
            "\"${properties.getProperty("LASTFM_API_SECRET", "")}\"")
    }
}
```

---

本文档将作为后续开发实施的核心参考，每个 Phase 开始前可据此细化该阶段的任务清单。
