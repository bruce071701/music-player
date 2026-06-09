package com.app.musicplayer.di

import android.content.Context
import androidx.room.Room
import com.app.musicplayer.core.database.MusicDatabase
import com.app.musicplayer.core.database.dao.EqPresetDao
import com.app.musicplayer.core.database.dao.PlayHistoryDao
import com.app.musicplayer.core.database.dao.PlaylistDao
import com.app.musicplayer.core.database.dao.TrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MusicDatabase {
        return Room.databaseBuilder(
            context,
            MusicDatabase::class.java,
            "music_player.db"
        ).build()
    }

    @Provides
    fun provideTrackDao(database: MusicDatabase): TrackDao = database.trackDao()

    @Provides
    fun providePlaylistDao(database: MusicDatabase): PlaylistDao = database.playlistDao()

    @Provides
    fun providePlayHistoryDao(database: MusicDatabase): PlayHistoryDao = database.playHistoryDao()

    @Provides
    fun provideEqPresetDao(database: MusicDatabase): EqPresetDao = database.eqPresetDao()
}
