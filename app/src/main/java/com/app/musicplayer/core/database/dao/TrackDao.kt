package com.app.musicplayer.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.musicplayer.core.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT * FROM tracks WHERE source = 'local' ORDER BY title ASC")
    fun getAllLocalTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrackById(id: Long): TrackEntity?

    @Query("SELECT DISTINCT artist FROM tracks WHERE artist IS NOT NULL ORDER BY artist ASC")
    fun getAllArtists(): Flow<List<String>>

    @Query("SELECT * FROM tracks WHERE artist = :artist ORDER BY album, track_number")
    fun getTracksByArtist(artist: String): Flow<List<TrackEntity>>

    @Query("SELECT DISTINCT album FROM tracks WHERE album IS NOT NULL ORDER BY album ASC")
    fun getAllAlbums(): Flow<List<String>>

    @Query("SELECT * FROM tracks WHERE album = :album ORDER BY disc_number, track_number")
    fun getTracksByAlbum(album: String): Flow<List<TrackEntity>>

    @Query("SELECT DISTINCT genre FROM tracks WHERE genre IS NOT NULL ORDER BY genre ASC")
    fun getAllGenres(): Flow<List<String>>

    @Query("SELECT * FROM tracks WHERE genre = :genre ORDER BY title ASC")
    fun getTracksByGenre(genre: String): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE is_favorite = 1 ORDER BY added_at DESC")
    fun getFavorites(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks ORDER BY last_played_at DESC LIMIT :limit")
    fun getRecentlyPlayed(limit: Int = 50): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks ORDER BY play_count DESC LIMIT :limit")
    fun getMostPlayed(limit: Int = 50): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks ORDER BY added_at DESC LIMIT :limit")
    fun getRecentlyAdded(limit: Int = 50): Flow<List<TrackEntity>>

    @Query("""
        SELECT * FROM tracks WHERE id IN 
        (SELECT docid FROM tracks_fts WHERE tracks_fts MATCH :query)
    """)
    fun searchTracks(query: String): Flow<List<TrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Update
    suspend fun updateTrack(track: TrackEntity)

    @Query("UPDATE tracks SET is_favorite = NOT is_favorite WHERE id = :trackId")
    suspend fun toggleFavorite(trackId: Long)

    @Query("UPDATE tracks SET play_count = play_count + 1, last_played_at = :timestamp WHERE id = :trackId")
    suspend fun incrementPlayCount(trackId: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE tracks SET rating = :rating WHERE id = :trackId")
    suspend fun setRating(trackId: Long, rating: Int)

    @Query("DELETE FROM tracks WHERE id = :trackId")
    suspend fun deleteTrack(trackId: Long)

    @Query("DELETE FROM tracks WHERE file_path = :filePath")
    suspend fun deleteByFilePath(filePath: String)

    @Query("SELECT COUNT(*) FROM tracks WHERE source = 'local'")
    fun getLocalTrackCount(): Flow<Int>

    @Query("SELECT file_path FROM tracks WHERE source = 'local'")
    suspend fun getAllLocalFilePaths(): List<String?>

    @Query("SELECT * FROM tracks WHERE file_path = :filePath LIMIT 1")
    suspend fun getTrackByFilePath(filePath: String): TrackEntity?
}
