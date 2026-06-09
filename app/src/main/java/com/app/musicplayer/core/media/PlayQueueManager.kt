package com.app.musicplayer.core.media

import com.app.musicplayer.core.model.PlayMode
import com.app.musicplayer.core.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayQueueManager @Inject constructor() {

    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val queue: StateFlow<List<Track>> = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _playMode = MutableStateFlow(PlayMode.SEQUENCE)
    val playMode: StateFlow<PlayMode> = _playMode.asStateFlow()

    private var originalQueue: List<Track> = emptyList()
    private var shuffledIndices: List<Int> = emptyList()

    val currentTrack: Track?
        get() {
            val index = _currentIndex.value
            val q = _queue.value
            return if (index in q.indices) q[index] else null
        }

    fun setQueue(tracks: List<Track>, startIndex: Int = 0) {
        originalQueue = tracks
        _queue.value = tracks
        _currentIndex.value = startIndex.coerceIn(0, tracks.size - 1)
        if (_playMode.value == PlayMode.SHUFFLE) {
            generateShuffledIndices()
        }
    }

    fun next(): Track? {
        val q = _queue.value
        if (q.isEmpty()) return null

        val nextIndex = when (_playMode.value) {
            PlayMode.REPEAT_ONE -> _currentIndex.value
            PlayMode.SHUFFLE -> {
                val currentShufflePos = shuffledIndices.indexOf(_currentIndex.value)
                val nextShufflePos = (currentShufflePos + 1) % shuffledIndices.size
                shuffledIndices[nextShufflePos]
            }
            PlayMode.SEQUENCE -> {
                val next = _currentIndex.value + 1
                if (next >= q.size) return null // end of queue
                next
            }
            PlayMode.REPEAT_ALL -> (_currentIndex.value + 1) % q.size
        }

        _currentIndex.value = nextIndex
        return q[nextIndex]
    }

    fun previous(): Track? {
        val q = _queue.value
        if (q.isEmpty()) return null

        val prevIndex = when (_playMode.value) {
            PlayMode.REPEAT_ONE -> _currentIndex.value
            PlayMode.SHUFFLE -> {
                val currentShufflePos = shuffledIndices.indexOf(_currentIndex.value)
                val prevShufflePos = if (currentShufflePos <= 0) shuffledIndices.size - 1 else currentShufflePos - 1
                shuffledIndices[prevShufflePos]
            }
            PlayMode.SEQUENCE -> {
                val prev = _currentIndex.value - 1
                if (prev < 0) return null
                prev
            }
            PlayMode.REPEAT_ALL -> {
                val prev = _currentIndex.value - 1
                if (prev < 0) q.size - 1 else prev
            }
        }

        _currentIndex.value = prevIndex
        return q[prevIndex]
    }

    fun setPlayMode(mode: PlayMode) {
        _playMode.value = mode
        if (mode == PlayMode.SHUFFLE) {
            generateShuffledIndices()
        }
    }

    fun moveTrack(from: Int, to: Int) {
        val mutable = _queue.value.toMutableList()
        val item = mutable.removeAt(from)
        mutable.add(to, item)
        _queue.value = mutable

        // Adjust current index
        val current = _currentIndex.value
        _currentIndex.value = when {
            current == from -> to
            from < current && to >= current -> current - 1
            from > current && to <= current -> current + 1
            else -> current
        }
    }

    fun removeTrack(index: Int) {
        val mutable = _queue.value.toMutableList()
        if (index !in mutable.indices) return
        mutable.removeAt(index)
        _queue.value = mutable

        val current = _currentIndex.value
        if (index < current) {
            _currentIndex.value = current - 1
        } else if (index == current && current >= mutable.size) {
            _currentIndex.value = (mutable.size - 1).coerceAtLeast(0)
        }
    }

    fun addNext(track: Track) {
        val mutable = _queue.value.toMutableList()
        val insertAt = (_currentIndex.value + 1).coerceAtMost(mutable.size)
        mutable.add(insertAt, track)
        _queue.value = mutable
    }

    fun addToEnd(track: Track) {
        _queue.value = _queue.value + track
    }

    fun getQueueTrackIds(): List<Long> = _queue.value.map { it.id }

    private fun generateShuffledIndices() {
        val size = _queue.value.size
        if (size == 0) return
        val current = _currentIndex.value
        val indices = (0 until size).filter { it != current }.shuffled()
        shuffledIndices = listOf(current) + indices
    }
}
