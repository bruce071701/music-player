package com.app.musicplayer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.musicplayer.core.database.dao.EqPresetDao
import com.app.musicplayer.core.database.dao.PlayHistoryDao
import com.app.musicplayer.core.database.dao.PlaylistDao
import com.app.musicplayer.core.database.dao.TrackDao
import com.app.musicplayer.core.database.entity.EqPresetEntity
import com.app.musicplayer.core.database.entity.PlayHistoryEntity
import com.app.musicplayer.core.database.entity.PlaylistEntity
import com.app.musicplayer.core.database.entity.PlaylistTrackEntity
import com.app.musicplayer.core.database.entity.TrackEntity
import com.app.musicplayer.core.database.entity.TrackFtsEntity

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
abstract class MusicDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playHistoryDao(): PlayHistoryDao
    abstract fun eqPresetDao(): EqPresetDao
}
