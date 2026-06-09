package com.app.musicplayer.core.media

import com.app.musicplayer.core.datastore.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

data class PlaybackRestorationData(
    val trackId: Long,
    val positionMs: Long,
    val queueTrackIds: List<Long>
)

/**
 * Periodically saves playback state to DataStore.
 * Enables recovery after service kill or device reboot.
 */
@Singleton
class PlaybackStateSaver @Inject constructor(
    private val preferences: AppPreferences,
    private val playerController: PlayerController,
    private val queueManager: PlayQueueManager
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Start periodic state saving (every 5 seconds while playing).
     */
    fun startPeriodicSave() {
        scope.launch {
            while (isActive) {
                delay(5000)
                if (playerController.isPlaying.value) {
                    saveCurrentState()
                }
            }
        }
    }

    /**
     * Save state immediately (e.g., on track change or pause).
     */
    suspend fun saveCurrentState() {
        val track = playerController.currentTrack.value ?: return
        val position = playerController.getCurrentPosition()
        val queueIds = queueManager.getQueueTrackIds()

        preferences.setLastTrackId(track.id)
        preferences.setLastPositionMs(position)
        preferences.setLastQueueJson(Json.encodeToString(queueIds))
    }

    /**
     * Restore last saved state (for service recovery or boot resume).
     */
    suspend fun restoreState(): PlaybackRestorationData? {
        val trackId = preferences.lastTrackId.first()
        val positionMs = preferences.lastPositionMs.first()
        val queueJson = preferences.lastQueueJson.first()

        if (trackId <= 0 || queueJson.isBlank()) return null

        val queueIds = try {
            Json.decodeFromString<List<Long>>(queueJson)
        } catch (e: Exception) {
            return null
        }

        return PlaybackRestorationData(
            trackId = trackId,
            positionMs = positionMs,
            queueTrackIds = queueIds
        )
    }
}
