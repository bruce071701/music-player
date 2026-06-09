package com.app.musicplayer.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.musicplayer.core.database.entity.EqPresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EqPresetDao {

    @Query("SELECT * FROM eq_presets ORDER BY is_builtin DESC, name ASC")
    fun getAllPresets(): Flow<List<EqPresetEntity>>

    @Query("SELECT * FROM eq_presets WHERE id = :id")
    suspend fun getPresetById(id: Long): EqPresetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: EqPresetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresets(presets: List<EqPresetEntity>)

    @Update
    suspend fun updatePreset(preset: EqPresetEntity)

    @Query("DELETE FROM eq_presets WHERE id = :id AND is_builtin = 0")
    suspend fun deletePreset(id: Long)

    @Query("SELECT COUNT(*) FROM eq_presets")
    suspend fun getPresetCount(): Int
}
