package com.app.musicplayer.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    // === Theme ===
    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        ThemeMode.fromString(prefs[Keys.THEME_MODE] ?: "system")
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.value }
    }

    // === Playback ===
    val gaplessEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.GAPLESS_ENABLED] ?: true
    }

    suspend fun setGaplessEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.GAPLESS_ENABLED] = enabled }
    }

    val crossfadeDuration: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.CROSSFADE_DURATION] ?: 0
    }

    suspend fun setCrossfadeDuration(ms: Int) {
        dataStore.edit { it[Keys.CROSSFADE_DURATION] = ms }
    }

    val replayGainMode: Flow<ReplayGainMode> = dataStore.data.map { prefs ->
        ReplayGainMode.fromString(prefs[Keys.REPLAY_GAIN_MODE] ?: "off")
    }

    suspend fun setReplayGainMode(mode: ReplayGainMode) {
        dataStore.edit { it[Keys.REPLAY_GAIN_MODE] = mode.value }
    }

    val defaultPlaybackSpeed: Flow<Float> = dataStore.data.map { prefs ->
        prefs[Keys.DEFAULT_PLAYBACK_SPEED] ?: 1.0f
    }

    suspend fun setDefaultPlaybackSpeed(speed: Float) {
        dataStore.edit { it[Keys.DEFAULT_PLAYBACK_SPEED] = speed }
    }

    val pauseOnHeadphoneDisconnect: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.PAUSE_ON_HEADPHONE_DISCONNECT] ?: true
    }

    suspend fun setPauseOnHeadphoneDisconnect(enabled: Boolean) {
        dataStore.edit { it[Keys.PAUSE_ON_HEADPHONE_DISCONNECT] = enabled }
    }

    val autoResumeOnBoot: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.AUTO_RESUME_ON_BOOT] ?: false
    }

    suspend fun setAutoResumeOnBoot(enabled: Boolean) {
        dataStore.edit { it[Keys.AUTO_RESUME_ON_BOOT] = enabled }
    }

    // === Library scanning ===
    val scanMode: Flow<ScanMode> = dataStore.data.map { prefs ->
        ScanMode.fromString(prefs[Keys.SCAN_MODE] ?: "all")
    }

    suspend fun setScanMode(mode: ScanMode) {
        dataStore.edit { it[Keys.SCAN_MODE] = mode.value }
    }

    val blacklistFolders: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[Keys.BLACKLIST_FOLDERS] ?: emptySet()
    }

    suspend fun setBlacklistFolders(folders: Set<String>) {
        dataStore.edit { it[Keys.BLACKLIST_FOLDERS] = folders }
    }

    val whitelistFolders: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[Keys.WHITELIST_FOLDERS] ?: emptySet()
    }

    suspend fun setWhitelistFolders(folders: Set<String>) {
        dataStore.edit { it[Keys.WHITELIST_FOLDERS] = folders }
    }

    val minTrackDurationSec: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.MIN_TRACK_DURATION_SEC] ?: 10
    }

    suspend fun setMinTrackDurationSec(seconds: Int) {
        dataStore.edit { it[Keys.MIN_TRACK_DURATION_SEC] = seconds }
    }

    // === Playback state persistence (for service recovery) ===
    val lastTrackId: Flow<Long> = dataStore.data.map { prefs ->
        prefs[Keys.LAST_TRACK_ID] ?: -1L
    }

    suspend fun setLastTrackId(trackId: Long) {
        dataStore.edit { it[Keys.LAST_TRACK_ID] = trackId }
    }

    val lastPositionMs: Flow<Long> = dataStore.data.map { prefs ->
        prefs[Keys.LAST_POSITION_MS] ?: 0L
    }

    suspend fun setLastPositionMs(positionMs: Long) {
        dataStore.edit { it[Keys.LAST_POSITION_MS] = positionMs }
    }

    val lastQueueJson: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.LAST_QUEUE_JSON] ?: ""
    }

    suspend fun setLastQueueJson(json: String) {
        dataStore.edit { it[Keys.LAST_QUEUE_JSON] = json }
    }

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val GAPLESS_ENABLED = booleanPreferencesKey("gapless_enabled")
        val CROSSFADE_DURATION = intPreferencesKey("crossfade_duration")
        val REPLAY_GAIN_MODE = stringPreferencesKey("replay_gain_mode")
        val DEFAULT_PLAYBACK_SPEED = floatPreferencesKey("default_playback_speed")
        val PAUSE_ON_HEADPHONE_DISCONNECT = booleanPreferencesKey("pause_on_headphone_disconnect")
        val AUTO_RESUME_ON_BOOT = booleanPreferencesKey("auto_resume_on_boot")
        val SCAN_MODE = stringPreferencesKey("scan_mode")
        val BLACKLIST_FOLDERS = stringSetPreferencesKey("blacklist_folders")
        val WHITELIST_FOLDERS = stringSetPreferencesKey("whitelist_folders")
        val MIN_TRACK_DURATION_SEC = intPreferencesKey("min_track_duration_sec")
        val LAST_TRACK_ID = longPreferencesKey("last_track_id")
        val LAST_POSITION_MS = longPreferencesKey("last_position_ms")
        val LAST_QUEUE_JSON = stringPreferencesKey("last_queue_json")
    }
}

enum class ThemeMode(val value: String) {
    SYSTEM("system"), LIGHT("light"), DARK("dark"), AMOLED("amoled"), PINK_ORANGE("pink_orange");

    companion object {
        fun fromString(value: String) = entries.find { it.value == value } ?: SYSTEM
    }
}

enum class ReplayGainMode(val value: String) {
    OFF("off"), TRACK("track"), ALBUM("album");

    companion object {
        fun fromString(value: String) = entries.find { it.value == value } ?: OFF
    }
}

enum class ScanMode(val value: String) {
    ALL("all"), BLACKLIST("blacklist"), WHITELIST("whitelist");

    companion object {
        fun fromString(value: String) = entries.find { it.value == value } ?: ALL
    }
}
