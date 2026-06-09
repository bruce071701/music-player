package com.app.musicplayer.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.musicplayer.core.database.entity.PlayHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayHistoryDao {

    @Insert
    suspend fun insertHistory(history: PlayHistoryEntity): Long

    @Query("SELECT * FROM play_history ORDER BY played_at DESC LIMIT :limit")
    fun getRecentHistory(limit: Int = 100): Flow<List<PlayHistoryEntity>>

    @Query("SELECT * FROM play_history WHERE scrobbled = 0 ORDER BY played_at ASC")
    suspend fun getUnscrobbledHistory(): List<PlayHistoryEntity>

    @Query("UPDATE play_history SET scrobbled = 1 WHERE id = :historyId")
    suspend fun markAsScrobbled(historyId: Long)

    @Query("UPDATE play_history SET scrobbled = 1 WHERE id IN (:ids)")
    suspend fun markAsScrobbled(ids: List<Long>)

    @Query("DELETE FROM play_history WHERE played_at < :beforeTimestamp")
    suspend fun deleteOlderThan(beforeTimestamp: Long)
}
