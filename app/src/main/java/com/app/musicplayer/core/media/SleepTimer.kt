package com.app.musicplayer.core.media

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepTimer @Inject constructor() {

    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _remainingMs = MutableStateFlow(0L)
    val remainingMs: StateFlow<Long> = _remainingMs.asStateFlow()

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    var onTimerFinished: (() -> Unit)? = null

    fun start(durationMs: Long) {
        cancel()
        _isActive.value = true
        _remainingMs.value = durationMs

        timerJob = scope.launch {
            var remaining = durationMs
            while (remaining > 0) {
                delay(1000)
                remaining -= 1000
                _remainingMs.value = remaining.coerceAtLeast(0)
            }
            _isActive.value = false
            _remainingMs.value = 0
            onTimerFinished?.invoke()
        }
    }

    fun cancel() {
        timerJob?.cancel()
        timerJob = null
        _isActive.value = false
        _remainingMs.value = 0
    }

    fun addTime(extraMs: Long) {
        if (_isActive.value) {
            _remainingMs.value += extraMs
        }
    }

    val remainingFormatted: String
        get() {
            val totalSec = _remainingMs.value / 1000
            val min = totalSec / 60
            val sec = totalSec % 60
            return "%d:%02d".format(min, sec)
        }
}
