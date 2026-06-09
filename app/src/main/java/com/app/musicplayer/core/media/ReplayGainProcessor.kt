package com.app.musicplayer.core.media

import com.app.musicplayer.core.datastore.AppPreferences
import com.app.musicplayer.core.datastore.ReplayGainMode
import com.app.musicplayer.core.model.Track
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class ReplayGainProcessor @Inject constructor(
    private val preferences: AppPreferences
) {
    suspend fun calculateGain(track: Track): Float {
        return when (preferences.replayGainMode.first()) {
            ReplayGainMode.TRACK -> track.replayGainTrack ?: 0f
            ReplayGainMode.ALBUM -> track.replayGainAlbum ?: track.replayGainTrack ?: 0f
            ReplayGainMode.OFF -> 0f
        }
    }

    fun gainDbToVolume(gainDb: Float): Float {
        // Convert dB gain to linear volume multiplier
        return 10f.pow(gainDb / 20f).coerceIn(0f, 1f)
    }
}
