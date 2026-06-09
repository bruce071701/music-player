package com.app.musicplayer.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.musicplayer.core.datastore.AppPreferences
import com.app.musicplayer.core.datastore.LanguageManager
import com.app.musicplayer.core.datastore.ReplayGainMode
import com.app.musicplayer.core.datastore.ScanMode
import com.app.musicplayer.core.datastore.ThemeMode
import com.app.musicplayer.feature.library.scanner.MediaScanner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: AppPreferences,
    private val mediaScanner: MediaScanner,
    val languageManager: LanguageManager,
    private val sleepTimer: com.app.musicplayer.core.media.SleepTimer
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = preferences.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val gaplessEnabled: StateFlow<Boolean> = preferences.gaplessEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val crossfadeDuration: StateFlow<Int> = preferences.crossfadeDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val replayGainMode: StateFlow<ReplayGainMode> = preferences.replayGainMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReplayGainMode.OFF)

    val scanMode: StateFlow<ScanMode> = preferences.scanMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ScanMode.ALL)

    val pauseOnHeadphone: StateFlow<Boolean> = preferences.pauseOnHeadphoneDisconnect
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val sleepTimerActive: StateFlow<Boolean> = sleepTimer.isActive

    val sleepTimerRemaining: StateFlow<Long> = sleepTimer.remainingMs

    val scanProgress = mediaScanner.scanProgress

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferences.setThemeMode(mode) }
    }

    fun setGapless(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setGaplessEnabled(enabled)
            if (enabled) preferences.setCrossfadeDuration(0) // Mutual exclusion
        }
    }

    fun setCrossfade(durationMs: Int) {
        viewModelScope.launch {
            preferences.setCrossfadeDuration(durationMs)
            if (durationMs > 0) preferences.setGaplessEnabled(false) // Mutual exclusion
        }
    }

    fun setReplayGainMode(mode: ReplayGainMode) {
        viewModelScope.launch { preferences.setReplayGainMode(mode) }
    }

    fun setScanMode(mode: ScanMode) {
        viewModelScope.launch { preferences.setScanMode(mode) }
    }

    fun setPauseOnHeadphone(enabled: Boolean) {
        viewModelScope.launch { preferences.setPauseOnHeadphoneDisconnect(enabled) }
    }

    fun rescanLibrary() {
        viewModelScope.launch { mediaScanner.scanLocalMusic() }
    }

    fun setLanguage(code: String) {
        viewModelScope.launch { languageManager.setLanguage(code) }
    }

    fun startSleepTimer(minutes: Int) {
        sleepTimer.start(minutes * 60 * 1000L)
    }

    fun cancelSleepTimer() {
        sleepTimer.cancel()
    }
}
