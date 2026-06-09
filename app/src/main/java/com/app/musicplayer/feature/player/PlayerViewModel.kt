package com.app.musicplayer.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.musicplayer.core.database.dao.PlayHistoryDao
import com.app.musicplayer.core.database.dao.TrackDao
import com.app.musicplayer.core.database.entity.PlayHistoryEntity
import com.app.musicplayer.core.database.mapper.toDomainModels
import com.app.musicplayer.core.media.PlayQueueManager
import com.app.musicplayer.core.media.PlaybackState
import com.app.musicplayer.core.media.PlayerController
import com.app.musicplayer.core.media.ReplayGainProcessor
import com.app.musicplayer.core.media.SleepTimer
import com.app.musicplayer.core.model.PlayMode
import com.app.musicplayer.core.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val queueManager: PlayQueueManager,
    private val trackDao: TrackDao,
    private val playHistoryDao: PlayHistoryDao,
    private val replayGainProcessor: ReplayGainProcessor,
    private val lyricsManager: LyricsManager,
    val sleepTimer: SleepTimer
) : ViewModel() {

    val currentTrack: StateFlow<Track?> = playerController.currentTrack
    val isPlaying: StateFlow<Boolean> = playerController.isPlaying
    val playbackState: StateFlow<PlaybackState> = playerController.playbackState
    val queue: StateFlow<List<Track>> = queueManager.queue
    val playMode: StateFlow<PlayMode> = queueManager.playMode

    private val _isPlayerExpanded = MutableStateFlow(false)
    val isPlayerExpanded: StateFlow<Boolean> = _isPlayerExpanded.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _lyrics = MutableStateFlow<List<LyricLine>>(emptyList())
    val lyrics: StateFlow<List<LyricLine>> = _lyrics.asStateFlow()

    private val _isLoadingLyrics = MutableStateFlow(false)
    val isLoadingLyrics: StateFlow<Boolean> = _isLoadingLyrics.asStateFlow()

    init {
        playerController.initialize()
        startPositionUpdater()
        setupSleepTimer()
    }

    fun play(track: Track, queue: List<Track>) {
        playerController.play(track, queue)
        viewModelScope.launch {
            trackDao.incrementPlayCount(track.id)
            playHistoryDao.insertHistory(
                PlayHistoryEntity(trackId = track.id)
            )
            _isFavorite.value = trackDao.getTrackById(track.id)?.isFavorite ?: false
            // Load lyrics
            loadLyricsForTrack(track)
        }
    }

    fun playPause() {
        if (currentTrack.value == null) {
            // No track loaded - play first available track from library
            playFirstAvailableTrack()
        } else {
            playerController.playPause()
        }
    }

    private fun playFirstAvailableTrack() {
        viewModelScope.launch {
            val tracks = trackDao.getAllLocalTracks().first()
            if (tracks.isNotEmpty()) {
                val domainTracks = tracks.toDomainModels()
                play(domainTracks.first(), domainTracks)
            }
        }
    }

    fun next() {
        playerController.next()
        updateFavoriteState()
        viewModelScope.launch { currentTrack.value?.let { loadLyricsForTrack(it) } }
    }

    fun previous() {
        playerController.previous()
        updateFavoriteState()
        viewModelScope.launch { currentTrack.value?.let { loadLyricsForTrack(it) } }
    }

    fun seekTo(positionMs: Long) {
        playerController.seekTo(positionMs)
        _currentPositionMs.value = positionMs
    }

    fun setPlayMode(mode: PlayMode) {
        playerController.setPlayMode(mode)
    }

    fun cyclePlayMode() {
        val current = queueManager.playMode.value
        val next = when (current) {
            PlayMode.SEQUENCE -> PlayMode.REPEAT_ALL
            PlayMode.REPEAT_ALL -> PlayMode.REPEAT_ONE
            PlayMode.REPEAT_ONE -> PlayMode.SHUFFLE
            PlayMode.SHUFFLE -> PlayMode.SEQUENCE
        }
        setPlayMode(next)
    }

    fun setSpeed(speed: Float) {
        playerController.setSpeed(speed)
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            currentTrack.value?.let { track ->
                trackDao.toggleFavorite(track.id)
                _isFavorite.value = !_isFavorite.value
            }
        }
    }

    fun expandPlayer() {
        _isPlayerExpanded.value = true
    }

    fun collapsePlayer() {
        _isPlayerExpanded.value = false
    }

    fun startSleepTimer(minutes: Int) {
        sleepTimer.start(minutes * 60 * 1000L)
    }

    fun cancelSleepTimer() {
        sleepTimer.cancel()
    }

    fun removeFromQueue(index: Int) {
        queueManager.removeTrack(index)
    }

    private fun startPositionUpdater() {
        viewModelScope.launch {
            while (isActive) {
                if (isPlaying.value) {
                    _currentPositionMs.value = playerController.getCurrentPosition()
                }
                delay(200) // Update every 200ms for smooth progress
            }
        }
    }

    private fun setupSleepTimer() {
        sleepTimer.onTimerFinished = {
            playerController.playPause() // Pause when timer ends
        }
    }

    private fun updateFavoriteState() {
        viewModelScope.launch {
            currentTrack.value?.let { track ->
                _isFavorite.value = trackDao.getTrackById(track.id)?.isFavorite ?: false
            }
        }
    }

    private suspend fun loadLyricsForTrack(track: Track) {
        _isLoadingLyrics.value = true
        _lyrics.value = emptyList()
        val loaded = lyricsManager.loadLyrics(track)
        _lyrics.value = loaded
        _isLoadingLyrics.value = false
    }

    fun requestLyrics() {
        viewModelScope.launch {
            if (_lyrics.value.isEmpty() && !_isLoadingLyrics.value) {
                currentTrack.value?.let { loadLyricsForTrack(it) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerController.release()
    }
}
