package com.app.musicplayer.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.app.musicplayer.core.database.entity.TrackEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TrackDao_Impl implements TrackDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TrackEntity> __insertionAdapterOfTrackEntity;

  private final EntityDeletionOrUpdateAdapter<TrackEntity> __updateAdapterOfTrackEntity;

  private final SharedSQLiteStatement __preparedStmtOfToggleFavorite;

  private final SharedSQLiteStatement __preparedStmtOfIncrementPlayCount;

  private final SharedSQLiteStatement __preparedStmtOfSetRating;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTrack;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByFilePath;

  public TrackDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTrackEntity = new EntityInsertionAdapter<TrackEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `tracks` (`id`,`source`,`title`,`artist`,`album_artist`,`album`,`duration_ms`,`track_number`,`disc_number`,`year`,`genre`,`file_path`,`youtube_id`,`cover_uri`,`bitrate`,`sample_rate`,`file_size`,`replay_gain_track`,`replay_gain_album`,`rating`,`play_count`,`last_played_at`,`is_favorite`,`added_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TrackEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getSource());
        statement.bindString(3, entity.getTitle());
        if (entity.getArtist() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getArtist());
        }
        if (entity.getAlbumArtist() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getAlbumArtist());
        }
        if (entity.getAlbum() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getAlbum());
        }
        statement.bindLong(7, entity.getDurationMs());
        if (entity.getTrackNumber() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getTrackNumber());
        }
        if (entity.getDiscNumber() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getDiscNumber());
        }
        if (entity.getYear() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getYear());
        }
        if (entity.getGenre() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getGenre());
        }
        if (entity.getFilePath() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getFilePath());
        }
        if (entity.getYoutubeId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getYoutubeId());
        }
        if (entity.getCoverUri() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getCoverUri());
        }
        if (entity.getBitrate() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getBitrate());
        }
        if (entity.getSampleRate() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getSampleRate());
        }
        if (entity.getFileSize() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getFileSize());
        }
        if (entity.getReplayGainTrack() == null) {
          statement.bindNull(18);
        } else {
          statement.bindDouble(18, entity.getReplayGainTrack());
        }
        if (entity.getReplayGainAlbum() == null) {
          statement.bindNull(19);
        } else {
          statement.bindDouble(19, entity.getReplayGainAlbum());
        }
        statement.bindLong(20, entity.getRating());
        statement.bindLong(21, entity.getPlayCount());
        if (entity.getLastPlayedAt() == null) {
          statement.bindNull(22);
        } else {
          statement.bindLong(22, entity.getLastPlayedAt());
        }
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(23, _tmp);
        statement.bindLong(24, entity.getAddedAt());
      }
    };
    this.__updateAdapterOfTrackEntity = new EntityDeletionOrUpdateAdapter<TrackEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `tracks` SET `id` = ?,`source` = ?,`title` = ?,`artist` = ?,`album_artist` = ?,`album` = ?,`duration_ms` = ?,`track_number` = ?,`disc_number` = ?,`year` = ?,`genre` = ?,`file_path` = ?,`youtube_id` = ?,`cover_uri` = ?,`bitrate` = ?,`sample_rate` = ?,`file_size` = ?,`replay_gain_track` = ?,`replay_gain_album` = ?,`rating` = ?,`play_count` = ?,`last_played_at` = ?,`is_favorite` = ?,`added_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TrackEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getSource());
        statement.bindString(3, entity.getTitle());
        if (entity.getArtist() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getArtist());
        }
        if (entity.getAlbumArtist() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getAlbumArtist());
        }
        if (entity.getAlbum() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getAlbum());
        }
        statement.bindLong(7, entity.getDurationMs());
        if (entity.getTrackNumber() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getTrackNumber());
        }
        if (entity.getDiscNumber() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getDiscNumber());
        }
        if (entity.getYear() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getYear());
        }
        if (entity.getGenre() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getGenre());
        }
        if (entity.getFilePath() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getFilePath());
        }
        if (entity.getYoutubeId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getYoutubeId());
        }
        if (entity.getCoverUri() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getCoverUri());
        }
        if (entity.getBitrate() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getBitrate());
        }
        if (entity.getSampleRate() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getSampleRate());
        }
        if (entity.getFileSize() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getFileSize());
        }
        if (entity.getReplayGainTrack() == null) {
          statement.bindNull(18);
        } else {
          statement.bindDouble(18, entity.getReplayGainTrack());
        }
        if (entity.getReplayGainAlbum() == null) {
          statement.bindNull(19);
        } else {
          statement.bindDouble(19, entity.getReplayGainAlbum());
        }
        statement.bindLong(20, entity.getRating());
        statement.bindLong(21, entity.getPlayCount());
        if (entity.getLastPlayedAt() == null) {
          statement.bindNull(22);
        } else {
          statement.bindLong(22, entity.getLastPlayedAt());
        }
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(23, _tmp);
        statement.bindLong(24, entity.getAddedAt());
        statement.bindLong(25, entity.getId());
      }
    };
    this.__preparedStmtOfToggleFavorite = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE tracks SET is_favorite = NOT is_favorite WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementPlayCount = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE tracks SET play_count = play_count + 1, last_played_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetRating = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE tracks SET rating = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteTrack = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM tracks WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByFilePath = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM tracks WHERE file_path = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertTrack(final TrackEntity track, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTrackEntity.insertAndReturnId(track);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertTracks(final List<TrackEntity> tracks,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTrackEntity.insert(tracks);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTrack(final TrackEntity track, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTrackEntity.handle(track);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object toggleFavorite(final long trackId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfToggleFavorite.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, trackId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfToggleFavorite.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementPlayCount(final long trackId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementPlayCount.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, trackId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementPlayCount.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setRating(final long trackId, final int rating,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetRating.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, rating);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, trackId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetRating.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTrack(final long trackId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTrack.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, trackId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteTrack.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByFilePath(final String filePath,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByFilePath.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, filePath);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteByFilePath.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TrackEntity>> getAllLocalTracks() {
    final String _sql = "SELECT * FROM tracks WHERE source = 'local' ORDER BY title ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks"}, new Callable<List<TrackEntity>>() {
      @Override
      @NonNull
      public List<TrackEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "artist");
          final int _cursorIndexOfAlbumArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "album_artist");
          final int _cursorIndexOfAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "album");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_ms");
          final int _cursorIndexOfTrackNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "track_number");
          final int _cursorIndexOfDiscNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "disc_number");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfGenre = CursorUtil.getColumnIndexOrThrow(_cursor, "genre");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfYoutubeId = CursorUtil.getColumnIndexOrThrow(_cursor, "youtube_id");
          final int _cursorIndexOfCoverUri = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_uri");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfSampleRate = CursorUtil.getColumnIndexOrThrow(_cursor, "sample_rate");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfReplayGainTrack = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_track");
          final int _cursorIndexOfReplayGainAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_album");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfPlayCount = CursorUtil.getColumnIndexOrThrow(_cursor, "play_count");
          final int _cursorIndexOfLastPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_played_at");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "added_at");
          final List<TrackEntity> _result = new ArrayList<TrackEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrackEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpArtist;
            if (_cursor.isNull(_cursorIndexOfArtist)) {
              _tmpArtist = null;
            } else {
              _tmpArtist = _cursor.getString(_cursorIndexOfArtist);
            }
            final String _tmpAlbumArtist;
            if (_cursor.isNull(_cursorIndexOfAlbumArtist)) {
              _tmpAlbumArtist = null;
            } else {
              _tmpAlbumArtist = _cursor.getString(_cursorIndexOfAlbumArtist);
            }
            final String _tmpAlbum;
            if (_cursor.isNull(_cursorIndexOfAlbum)) {
              _tmpAlbum = null;
            } else {
              _tmpAlbum = _cursor.getString(_cursorIndexOfAlbum);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final Integer _tmpTrackNumber;
            if (_cursor.isNull(_cursorIndexOfTrackNumber)) {
              _tmpTrackNumber = null;
            } else {
              _tmpTrackNumber = _cursor.getInt(_cursorIndexOfTrackNumber);
            }
            final Integer _tmpDiscNumber;
            if (_cursor.isNull(_cursorIndexOfDiscNumber)) {
              _tmpDiscNumber = null;
            } else {
              _tmpDiscNumber = _cursor.getInt(_cursorIndexOfDiscNumber);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpGenre;
            if (_cursor.isNull(_cursorIndexOfGenre)) {
              _tmpGenre = null;
            } else {
              _tmpGenre = _cursor.getString(_cursorIndexOfGenre);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpYoutubeId;
            if (_cursor.isNull(_cursorIndexOfYoutubeId)) {
              _tmpYoutubeId = null;
            } else {
              _tmpYoutubeId = _cursor.getString(_cursorIndexOfYoutubeId);
            }
            final String _tmpCoverUri;
            if (_cursor.isNull(_cursorIndexOfCoverUri)) {
              _tmpCoverUri = null;
            } else {
              _tmpCoverUri = _cursor.getString(_cursorIndexOfCoverUri);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final Integer _tmpSampleRate;
            if (_cursor.isNull(_cursorIndexOfSampleRate)) {
              _tmpSampleRate = null;
            } else {
              _tmpSampleRate = _cursor.getInt(_cursorIndexOfSampleRate);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Float _tmpReplayGainTrack;
            if (_cursor.isNull(_cursorIndexOfReplayGainTrack)) {
              _tmpReplayGainTrack = null;
            } else {
              _tmpReplayGainTrack = _cursor.getFloat(_cursorIndexOfReplayGainTrack);
            }
            final Float _tmpReplayGainAlbum;
            if (_cursor.isNull(_cursorIndexOfReplayGainAlbum)) {
              _tmpReplayGainAlbum = null;
            } else {
              _tmpReplayGainAlbum = _cursor.getFloat(_cursorIndexOfReplayGainAlbum);
            }
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpPlayCount;
            _tmpPlayCount = _cursor.getInt(_cursorIndexOfPlayCount);
            final Long _tmpLastPlayedAt;
            if (_cursor.isNull(_cursorIndexOfLastPlayedAt)) {
              _tmpLastPlayedAt = null;
            } else {
              _tmpLastPlayedAt = _cursor.getLong(_cursorIndexOfLastPlayedAt);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            _item = new TrackEntity(_tmpId,_tmpSource,_tmpTitle,_tmpArtist,_tmpAlbumArtist,_tmpAlbum,_tmpDurationMs,_tmpTrackNumber,_tmpDiscNumber,_tmpYear,_tmpGenre,_tmpFilePath,_tmpYoutubeId,_tmpCoverUri,_tmpBitrate,_tmpSampleRate,_tmpFileSize,_tmpReplayGainTrack,_tmpReplayGainAlbum,_tmpRating,_tmpPlayCount,_tmpLastPlayedAt,_tmpIsFavorite,_tmpAddedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTrackById(final long id, final Continuation<? super TrackEntity> $completion) {
    final String _sql = "SELECT * FROM tracks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TrackEntity>() {
      @Override
      @Nullable
      public TrackEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "artist");
          final int _cursorIndexOfAlbumArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "album_artist");
          final int _cursorIndexOfAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "album");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_ms");
          final int _cursorIndexOfTrackNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "track_number");
          final int _cursorIndexOfDiscNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "disc_number");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfGenre = CursorUtil.getColumnIndexOrThrow(_cursor, "genre");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfYoutubeId = CursorUtil.getColumnIndexOrThrow(_cursor, "youtube_id");
          final int _cursorIndexOfCoverUri = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_uri");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfSampleRate = CursorUtil.getColumnIndexOrThrow(_cursor, "sample_rate");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfReplayGainTrack = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_track");
          final int _cursorIndexOfReplayGainAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_album");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfPlayCount = CursorUtil.getColumnIndexOrThrow(_cursor, "play_count");
          final int _cursorIndexOfLastPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_played_at");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "added_at");
          final TrackEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpArtist;
            if (_cursor.isNull(_cursorIndexOfArtist)) {
              _tmpArtist = null;
            } else {
              _tmpArtist = _cursor.getString(_cursorIndexOfArtist);
            }
            final String _tmpAlbumArtist;
            if (_cursor.isNull(_cursorIndexOfAlbumArtist)) {
              _tmpAlbumArtist = null;
            } else {
              _tmpAlbumArtist = _cursor.getString(_cursorIndexOfAlbumArtist);
            }
            final String _tmpAlbum;
            if (_cursor.isNull(_cursorIndexOfAlbum)) {
              _tmpAlbum = null;
            } else {
              _tmpAlbum = _cursor.getString(_cursorIndexOfAlbum);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final Integer _tmpTrackNumber;
            if (_cursor.isNull(_cursorIndexOfTrackNumber)) {
              _tmpTrackNumber = null;
            } else {
              _tmpTrackNumber = _cursor.getInt(_cursorIndexOfTrackNumber);
            }
            final Integer _tmpDiscNumber;
            if (_cursor.isNull(_cursorIndexOfDiscNumber)) {
              _tmpDiscNumber = null;
            } else {
              _tmpDiscNumber = _cursor.getInt(_cursorIndexOfDiscNumber);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpGenre;
            if (_cursor.isNull(_cursorIndexOfGenre)) {
              _tmpGenre = null;
            } else {
              _tmpGenre = _cursor.getString(_cursorIndexOfGenre);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpYoutubeId;
            if (_cursor.isNull(_cursorIndexOfYoutubeId)) {
              _tmpYoutubeId = null;
            } else {
              _tmpYoutubeId = _cursor.getString(_cursorIndexOfYoutubeId);
            }
            final String _tmpCoverUri;
            if (_cursor.isNull(_cursorIndexOfCoverUri)) {
              _tmpCoverUri = null;
            } else {
              _tmpCoverUri = _cursor.getString(_cursorIndexOfCoverUri);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final Integer _tmpSampleRate;
            if (_cursor.isNull(_cursorIndexOfSampleRate)) {
              _tmpSampleRate = null;
            } else {
              _tmpSampleRate = _cursor.getInt(_cursorIndexOfSampleRate);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Float _tmpReplayGainTrack;
            if (_cursor.isNull(_cursorIndexOfReplayGainTrack)) {
              _tmpReplayGainTrack = null;
            } else {
              _tmpReplayGainTrack = _cursor.getFloat(_cursorIndexOfReplayGainTrack);
            }
            final Float _tmpReplayGainAlbum;
            if (_cursor.isNull(_cursorIndexOfReplayGainAlbum)) {
              _tmpReplayGainAlbum = null;
            } else {
              _tmpReplayGainAlbum = _cursor.getFloat(_cursorIndexOfReplayGainAlbum);
            }
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpPlayCount;
            _tmpPlayCount = _cursor.getInt(_cursorIndexOfPlayCount);
            final Long _tmpLastPlayedAt;
            if (_cursor.isNull(_cursorIndexOfLastPlayedAt)) {
              _tmpLastPlayedAt = null;
            } else {
              _tmpLastPlayedAt = _cursor.getLong(_cursorIndexOfLastPlayedAt);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            _result = new TrackEntity(_tmpId,_tmpSource,_tmpTitle,_tmpArtist,_tmpAlbumArtist,_tmpAlbum,_tmpDurationMs,_tmpTrackNumber,_tmpDiscNumber,_tmpYear,_tmpGenre,_tmpFilePath,_tmpYoutubeId,_tmpCoverUri,_tmpBitrate,_tmpSampleRate,_tmpFileSize,_tmpReplayGainTrack,_tmpReplayGainAlbum,_tmpRating,_tmpPlayCount,_tmpLastPlayedAt,_tmpIsFavorite,_tmpAddedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<String>> getAllArtists() {
    final String _sql = "SELECT DISTINCT artist FROM tracks WHERE artist IS NOT NULL ORDER BY artist ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TrackEntity>> getTracksByArtist(final String artist) {
    final String _sql = "SELECT * FROM tracks WHERE artist = ? ORDER BY album, track_number";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, artist);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks"}, new Callable<List<TrackEntity>>() {
      @Override
      @NonNull
      public List<TrackEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "artist");
          final int _cursorIndexOfAlbumArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "album_artist");
          final int _cursorIndexOfAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "album");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_ms");
          final int _cursorIndexOfTrackNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "track_number");
          final int _cursorIndexOfDiscNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "disc_number");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfGenre = CursorUtil.getColumnIndexOrThrow(_cursor, "genre");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfYoutubeId = CursorUtil.getColumnIndexOrThrow(_cursor, "youtube_id");
          final int _cursorIndexOfCoverUri = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_uri");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfSampleRate = CursorUtil.getColumnIndexOrThrow(_cursor, "sample_rate");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfReplayGainTrack = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_track");
          final int _cursorIndexOfReplayGainAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_album");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfPlayCount = CursorUtil.getColumnIndexOrThrow(_cursor, "play_count");
          final int _cursorIndexOfLastPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_played_at");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "added_at");
          final List<TrackEntity> _result = new ArrayList<TrackEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrackEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpArtist;
            if (_cursor.isNull(_cursorIndexOfArtist)) {
              _tmpArtist = null;
            } else {
              _tmpArtist = _cursor.getString(_cursorIndexOfArtist);
            }
            final String _tmpAlbumArtist;
            if (_cursor.isNull(_cursorIndexOfAlbumArtist)) {
              _tmpAlbumArtist = null;
            } else {
              _tmpAlbumArtist = _cursor.getString(_cursorIndexOfAlbumArtist);
            }
            final String _tmpAlbum;
            if (_cursor.isNull(_cursorIndexOfAlbum)) {
              _tmpAlbum = null;
            } else {
              _tmpAlbum = _cursor.getString(_cursorIndexOfAlbum);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final Integer _tmpTrackNumber;
            if (_cursor.isNull(_cursorIndexOfTrackNumber)) {
              _tmpTrackNumber = null;
            } else {
              _tmpTrackNumber = _cursor.getInt(_cursorIndexOfTrackNumber);
            }
            final Integer _tmpDiscNumber;
            if (_cursor.isNull(_cursorIndexOfDiscNumber)) {
              _tmpDiscNumber = null;
            } else {
              _tmpDiscNumber = _cursor.getInt(_cursorIndexOfDiscNumber);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpGenre;
            if (_cursor.isNull(_cursorIndexOfGenre)) {
              _tmpGenre = null;
            } else {
              _tmpGenre = _cursor.getString(_cursorIndexOfGenre);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpYoutubeId;
            if (_cursor.isNull(_cursorIndexOfYoutubeId)) {
              _tmpYoutubeId = null;
            } else {
              _tmpYoutubeId = _cursor.getString(_cursorIndexOfYoutubeId);
            }
            final String _tmpCoverUri;
            if (_cursor.isNull(_cursorIndexOfCoverUri)) {
              _tmpCoverUri = null;
            } else {
              _tmpCoverUri = _cursor.getString(_cursorIndexOfCoverUri);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final Integer _tmpSampleRate;
            if (_cursor.isNull(_cursorIndexOfSampleRate)) {
              _tmpSampleRate = null;
            } else {
              _tmpSampleRate = _cursor.getInt(_cursorIndexOfSampleRate);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Float _tmpReplayGainTrack;
            if (_cursor.isNull(_cursorIndexOfReplayGainTrack)) {
              _tmpReplayGainTrack = null;
            } else {
              _tmpReplayGainTrack = _cursor.getFloat(_cursorIndexOfReplayGainTrack);
            }
            final Float _tmpReplayGainAlbum;
            if (_cursor.isNull(_cursorIndexOfReplayGainAlbum)) {
              _tmpReplayGainAlbum = null;
            } else {
              _tmpReplayGainAlbum = _cursor.getFloat(_cursorIndexOfReplayGainAlbum);
            }
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpPlayCount;
            _tmpPlayCount = _cursor.getInt(_cursorIndexOfPlayCount);
            final Long _tmpLastPlayedAt;
            if (_cursor.isNull(_cursorIndexOfLastPlayedAt)) {
              _tmpLastPlayedAt = null;
            } else {
              _tmpLastPlayedAt = _cursor.getLong(_cursorIndexOfLastPlayedAt);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            _item = new TrackEntity(_tmpId,_tmpSource,_tmpTitle,_tmpArtist,_tmpAlbumArtist,_tmpAlbum,_tmpDurationMs,_tmpTrackNumber,_tmpDiscNumber,_tmpYear,_tmpGenre,_tmpFilePath,_tmpYoutubeId,_tmpCoverUri,_tmpBitrate,_tmpSampleRate,_tmpFileSize,_tmpReplayGainTrack,_tmpReplayGainAlbum,_tmpRating,_tmpPlayCount,_tmpLastPlayedAt,_tmpIsFavorite,_tmpAddedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<String>> getAllAlbums() {
    final String _sql = "SELECT DISTINCT album FROM tracks WHERE album IS NOT NULL ORDER BY album ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TrackEntity>> getTracksByAlbum(final String album) {
    final String _sql = "SELECT * FROM tracks WHERE album = ? ORDER BY disc_number, track_number";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, album);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks"}, new Callable<List<TrackEntity>>() {
      @Override
      @NonNull
      public List<TrackEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "artist");
          final int _cursorIndexOfAlbumArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "album_artist");
          final int _cursorIndexOfAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "album");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_ms");
          final int _cursorIndexOfTrackNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "track_number");
          final int _cursorIndexOfDiscNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "disc_number");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfGenre = CursorUtil.getColumnIndexOrThrow(_cursor, "genre");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfYoutubeId = CursorUtil.getColumnIndexOrThrow(_cursor, "youtube_id");
          final int _cursorIndexOfCoverUri = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_uri");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfSampleRate = CursorUtil.getColumnIndexOrThrow(_cursor, "sample_rate");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfReplayGainTrack = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_track");
          final int _cursorIndexOfReplayGainAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_album");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfPlayCount = CursorUtil.getColumnIndexOrThrow(_cursor, "play_count");
          final int _cursorIndexOfLastPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_played_at");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "added_at");
          final List<TrackEntity> _result = new ArrayList<TrackEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrackEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpArtist;
            if (_cursor.isNull(_cursorIndexOfArtist)) {
              _tmpArtist = null;
            } else {
              _tmpArtist = _cursor.getString(_cursorIndexOfArtist);
            }
            final String _tmpAlbumArtist;
            if (_cursor.isNull(_cursorIndexOfAlbumArtist)) {
              _tmpAlbumArtist = null;
            } else {
              _tmpAlbumArtist = _cursor.getString(_cursorIndexOfAlbumArtist);
            }
            final String _tmpAlbum;
            if (_cursor.isNull(_cursorIndexOfAlbum)) {
              _tmpAlbum = null;
            } else {
              _tmpAlbum = _cursor.getString(_cursorIndexOfAlbum);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final Integer _tmpTrackNumber;
            if (_cursor.isNull(_cursorIndexOfTrackNumber)) {
              _tmpTrackNumber = null;
            } else {
              _tmpTrackNumber = _cursor.getInt(_cursorIndexOfTrackNumber);
            }
            final Integer _tmpDiscNumber;
            if (_cursor.isNull(_cursorIndexOfDiscNumber)) {
              _tmpDiscNumber = null;
            } else {
              _tmpDiscNumber = _cursor.getInt(_cursorIndexOfDiscNumber);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpGenre;
            if (_cursor.isNull(_cursorIndexOfGenre)) {
              _tmpGenre = null;
            } else {
              _tmpGenre = _cursor.getString(_cursorIndexOfGenre);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpYoutubeId;
            if (_cursor.isNull(_cursorIndexOfYoutubeId)) {
              _tmpYoutubeId = null;
            } else {
              _tmpYoutubeId = _cursor.getString(_cursorIndexOfYoutubeId);
            }
            final String _tmpCoverUri;
            if (_cursor.isNull(_cursorIndexOfCoverUri)) {
              _tmpCoverUri = null;
            } else {
              _tmpCoverUri = _cursor.getString(_cursorIndexOfCoverUri);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final Integer _tmpSampleRate;
            if (_cursor.isNull(_cursorIndexOfSampleRate)) {
              _tmpSampleRate = null;
            } else {
              _tmpSampleRate = _cursor.getInt(_cursorIndexOfSampleRate);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Float _tmpReplayGainTrack;
            if (_cursor.isNull(_cursorIndexOfReplayGainTrack)) {
              _tmpReplayGainTrack = null;
            } else {
              _tmpReplayGainTrack = _cursor.getFloat(_cursorIndexOfReplayGainTrack);
            }
            final Float _tmpReplayGainAlbum;
            if (_cursor.isNull(_cursorIndexOfReplayGainAlbum)) {
              _tmpReplayGainAlbum = null;
            } else {
              _tmpReplayGainAlbum = _cursor.getFloat(_cursorIndexOfReplayGainAlbum);
            }
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpPlayCount;
            _tmpPlayCount = _cursor.getInt(_cursorIndexOfPlayCount);
            final Long _tmpLastPlayedAt;
            if (_cursor.isNull(_cursorIndexOfLastPlayedAt)) {
              _tmpLastPlayedAt = null;
            } else {
              _tmpLastPlayedAt = _cursor.getLong(_cursorIndexOfLastPlayedAt);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            _item = new TrackEntity(_tmpId,_tmpSource,_tmpTitle,_tmpArtist,_tmpAlbumArtist,_tmpAlbum,_tmpDurationMs,_tmpTrackNumber,_tmpDiscNumber,_tmpYear,_tmpGenre,_tmpFilePath,_tmpYoutubeId,_tmpCoverUri,_tmpBitrate,_tmpSampleRate,_tmpFileSize,_tmpReplayGainTrack,_tmpReplayGainAlbum,_tmpRating,_tmpPlayCount,_tmpLastPlayedAt,_tmpIsFavorite,_tmpAddedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<String>> getAllGenres() {
    final String _sql = "SELECT DISTINCT genre FROM tracks WHERE genre IS NOT NULL ORDER BY genre ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TrackEntity>> getTracksByGenre(final String genre) {
    final String _sql = "SELECT * FROM tracks WHERE genre = ? ORDER BY title ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, genre);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks"}, new Callable<List<TrackEntity>>() {
      @Override
      @NonNull
      public List<TrackEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "artist");
          final int _cursorIndexOfAlbumArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "album_artist");
          final int _cursorIndexOfAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "album");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_ms");
          final int _cursorIndexOfTrackNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "track_number");
          final int _cursorIndexOfDiscNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "disc_number");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfGenre = CursorUtil.getColumnIndexOrThrow(_cursor, "genre");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfYoutubeId = CursorUtil.getColumnIndexOrThrow(_cursor, "youtube_id");
          final int _cursorIndexOfCoverUri = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_uri");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfSampleRate = CursorUtil.getColumnIndexOrThrow(_cursor, "sample_rate");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfReplayGainTrack = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_track");
          final int _cursorIndexOfReplayGainAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_album");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfPlayCount = CursorUtil.getColumnIndexOrThrow(_cursor, "play_count");
          final int _cursorIndexOfLastPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_played_at");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "added_at");
          final List<TrackEntity> _result = new ArrayList<TrackEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrackEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpArtist;
            if (_cursor.isNull(_cursorIndexOfArtist)) {
              _tmpArtist = null;
            } else {
              _tmpArtist = _cursor.getString(_cursorIndexOfArtist);
            }
            final String _tmpAlbumArtist;
            if (_cursor.isNull(_cursorIndexOfAlbumArtist)) {
              _tmpAlbumArtist = null;
            } else {
              _tmpAlbumArtist = _cursor.getString(_cursorIndexOfAlbumArtist);
            }
            final String _tmpAlbum;
            if (_cursor.isNull(_cursorIndexOfAlbum)) {
              _tmpAlbum = null;
            } else {
              _tmpAlbum = _cursor.getString(_cursorIndexOfAlbum);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final Integer _tmpTrackNumber;
            if (_cursor.isNull(_cursorIndexOfTrackNumber)) {
              _tmpTrackNumber = null;
            } else {
              _tmpTrackNumber = _cursor.getInt(_cursorIndexOfTrackNumber);
            }
            final Integer _tmpDiscNumber;
            if (_cursor.isNull(_cursorIndexOfDiscNumber)) {
              _tmpDiscNumber = null;
            } else {
              _tmpDiscNumber = _cursor.getInt(_cursorIndexOfDiscNumber);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpGenre;
            if (_cursor.isNull(_cursorIndexOfGenre)) {
              _tmpGenre = null;
            } else {
              _tmpGenre = _cursor.getString(_cursorIndexOfGenre);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpYoutubeId;
            if (_cursor.isNull(_cursorIndexOfYoutubeId)) {
              _tmpYoutubeId = null;
            } else {
              _tmpYoutubeId = _cursor.getString(_cursorIndexOfYoutubeId);
            }
            final String _tmpCoverUri;
            if (_cursor.isNull(_cursorIndexOfCoverUri)) {
              _tmpCoverUri = null;
            } else {
              _tmpCoverUri = _cursor.getString(_cursorIndexOfCoverUri);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final Integer _tmpSampleRate;
            if (_cursor.isNull(_cursorIndexOfSampleRate)) {
              _tmpSampleRate = null;
            } else {
              _tmpSampleRate = _cursor.getInt(_cursorIndexOfSampleRate);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Float _tmpReplayGainTrack;
            if (_cursor.isNull(_cursorIndexOfReplayGainTrack)) {
              _tmpReplayGainTrack = null;
            } else {
              _tmpReplayGainTrack = _cursor.getFloat(_cursorIndexOfReplayGainTrack);
            }
            final Float _tmpReplayGainAlbum;
            if (_cursor.isNull(_cursorIndexOfReplayGainAlbum)) {
              _tmpReplayGainAlbum = null;
            } else {
              _tmpReplayGainAlbum = _cursor.getFloat(_cursorIndexOfReplayGainAlbum);
            }
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpPlayCount;
            _tmpPlayCount = _cursor.getInt(_cursorIndexOfPlayCount);
            final Long _tmpLastPlayedAt;
            if (_cursor.isNull(_cursorIndexOfLastPlayedAt)) {
              _tmpLastPlayedAt = null;
            } else {
              _tmpLastPlayedAt = _cursor.getLong(_cursorIndexOfLastPlayedAt);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            _item = new TrackEntity(_tmpId,_tmpSource,_tmpTitle,_tmpArtist,_tmpAlbumArtist,_tmpAlbum,_tmpDurationMs,_tmpTrackNumber,_tmpDiscNumber,_tmpYear,_tmpGenre,_tmpFilePath,_tmpYoutubeId,_tmpCoverUri,_tmpBitrate,_tmpSampleRate,_tmpFileSize,_tmpReplayGainTrack,_tmpReplayGainAlbum,_tmpRating,_tmpPlayCount,_tmpLastPlayedAt,_tmpIsFavorite,_tmpAddedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TrackEntity>> getFavorites() {
    final String _sql = "SELECT * FROM tracks WHERE is_favorite = 1 ORDER BY added_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks"}, new Callable<List<TrackEntity>>() {
      @Override
      @NonNull
      public List<TrackEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "artist");
          final int _cursorIndexOfAlbumArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "album_artist");
          final int _cursorIndexOfAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "album");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_ms");
          final int _cursorIndexOfTrackNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "track_number");
          final int _cursorIndexOfDiscNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "disc_number");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfGenre = CursorUtil.getColumnIndexOrThrow(_cursor, "genre");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfYoutubeId = CursorUtil.getColumnIndexOrThrow(_cursor, "youtube_id");
          final int _cursorIndexOfCoverUri = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_uri");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfSampleRate = CursorUtil.getColumnIndexOrThrow(_cursor, "sample_rate");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfReplayGainTrack = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_track");
          final int _cursorIndexOfReplayGainAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_album");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfPlayCount = CursorUtil.getColumnIndexOrThrow(_cursor, "play_count");
          final int _cursorIndexOfLastPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_played_at");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "added_at");
          final List<TrackEntity> _result = new ArrayList<TrackEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrackEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpArtist;
            if (_cursor.isNull(_cursorIndexOfArtist)) {
              _tmpArtist = null;
            } else {
              _tmpArtist = _cursor.getString(_cursorIndexOfArtist);
            }
            final String _tmpAlbumArtist;
            if (_cursor.isNull(_cursorIndexOfAlbumArtist)) {
              _tmpAlbumArtist = null;
            } else {
              _tmpAlbumArtist = _cursor.getString(_cursorIndexOfAlbumArtist);
            }
            final String _tmpAlbum;
            if (_cursor.isNull(_cursorIndexOfAlbum)) {
              _tmpAlbum = null;
            } else {
              _tmpAlbum = _cursor.getString(_cursorIndexOfAlbum);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final Integer _tmpTrackNumber;
            if (_cursor.isNull(_cursorIndexOfTrackNumber)) {
              _tmpTrackNumber = null;
            } else {
              _tmpTrackNumber = _cursor.getInt(_cursorIndexOfTrackNumber);
            }
            final Integer _tmpDiscNumber;
            if (_cursor.isNull(_cursorIndexOfDiscNumber)) {
              _tmpDiscNumber = null;
            } else {
              _tmpDiscNumber = _cursor.getInt(_cursorIndexOfDiscNumber);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpGenre;
            if (_cursor.isNull(_cursorIndexOfGenre)) {
              _tmpGenre = null;
            } else {
              _tmpGenre = _cursor.getString(_cursorIndexOfGenre);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpYoutubeId;
            if (_cursor.isNull(_cursorIndexOfYoutubeId)) {
              _tmpYoutubeId = null;
            } else {
              _tmpYoutubeId = _cursor.getString(_cursorIndexOfYoutubeId);
            }
            final String _tmpCoverUri;
            if (_cursor.isNull(_cursorIndexOfCoverUri)) {
              _tmpCoverUri = null;
            } else {
              _tmpCoverUri = _cursor.getString(_cursorIndexOfCoverUri);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final Integer _tmpSampleRate;
            if (_cursor.isNull(_cursorIndexOfSampleRate)) {
              _tmpSampleRate = null;
            } else {
              _tmpSampleRate = _cursor.getInt(_cursorIndexOfSampleRate);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Float _tmpReplayGainTrack;
            if (_cursor.isNull(_cursorIndexOfReplayGainTrack)) {
              _tmpReplayGainTrack = null;
            } else {
              _tmpReplayGainTrack = _cursor.getFloat(_cursorIndexOfReplayGainTrack);
            }
            final Float _tmpReplayGainAlbum;
            if (_cursor.isNull(_cursorIndexOfReplayGainAlbum)) {
              _tmpReplayGainAlbum = null;
            } else {
              _tmpReplayGainAlbum = _cursor.getFloat(_cursorIndexOfReplayGainAlbum);
            }
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpPlayCount;
            _tmpPlayCount = _cursor.getInt(_cursorIndexOfPlayCount);
            final Long _tmpLastPlayedAt;
            if (_cursor.isNull(_cursorIndexOfLastPlayedAt)) {
              _tmpLastPlayedAt = null;
            } else {
              _tmpLastPlayedAt = _cursor.getLong(_cursorIndexOfLastPlayedAt);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            _item = new TrackEntity(_tmpId,_tmpSource,_tmpTitle,_tmpArtist,_tmpAlbumArtist,_tmpAlbum,_tmpDurationMs,_tmpTrackNumber,_tmpDiscNumber,_tmpYear,_tmpGenre,_tmpFilePath,_tmpYoutubeId,_tmpCoverUri,_tmpBitrate,_tmpSampleRate,_tmpFileSize,_tmpReplayGainTrack,_tmpReplayGainAlbum,_tmpRating,_tmpPlayCount,_tmpLastPlayedAt,_tmpIsFavorite,_tmpAddedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TrackEntity>> getRecentlyPlayed(final int limit) {
    final String _sql = "SELECT * FROM tracks ORDER BY last_played_at DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks"}, new Callable<List<TrackEntity>>() {
      @Override
      @NonNull
      public List<TrackEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "artist");
          final int _cursorIndexOfAlbumArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "album_artist");
          final int _cursorIndexOfAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "album");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_ms");
          final int _cursorIndexOfTrackNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "track_number");
          final int _cursorIndexOfDiscNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "disc_number");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfGenre = CursorUtil.getColumnIndexOrThrow(_cursor, "genre");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfYoutubeId = CursorUtil.getColumnIndexOrThrow(_cursor, "youtube_id");
          final int _cursorIndexOfCoverUri = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_uri");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfSampleRate = CursorUtil.getColumnIndexOrThrow(_cursor, "sample_rate");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfReplayGainTrack = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_track");
          final int _cursorIndexOfReplayGainAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_album");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfPlayCount = CursorUtil.getColumnIndexOrThrow(_cursor, "play_count");
          final int _cursorIndexOfLastPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_played_at");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "added_at");
          final List<TrackEntity> _result = new ArrayList<TrackEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrackEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpArtist;
            if (_cursor.isNull(_cursorIndexOfArtist)) {
              _tmpArtist = null;
            } else {
              _tmpArtist = _cursor.getString(_cursorIndexOfArtist);
            }
            final String _tmpAlbumArtist;
            if (_cursor.isNull(_cursorIndexOfAlbumArtist)) {
              _tmpAlbumArtist = null;
            } else {
              _tmpAlbumArtist = _cursor.getString(_cursorIndexOfAlbumArtist);
            }
            final String _tmpAlbum;
            if (_cursor.isNull(_cursorIndexOfAlbum)) {
              _tmpAlbum = null;
            } else {
              _tmpAlbum = _cursor.getString(_cursorIndexOfAlbum);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final Integer _tmpTrackNumber;
            if (_cursor.isNull(_cursorIndexOfTrackNumber)) {
              _tmpTrackNumber = null;
            } else {
              _tmpTrackNumber = _cursor.getInt(_cursorIndexOfTrackNumber);
            }
            final Integer _tmpDiscNumber;
            if (_cursor.isNull(_cursorIndexOfDiscNumber)) {
              _tmpDiscNumber = null;
            } else {
              _tmpDiscNumber = _cursor.getInt(_cursorIndexOfDiscNumber);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpGenre;
            if (_cursor.isNull(_cursorIndexOfGenre)) {
              _tmpGenre = null;
            } else {
              _tmpGenre = _cursor.getString(_cursorIndexOfGenre);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpYoutubeId;
            if (_cursor.isNull(_cursorIndexOfYoutubeId)) {
              _tmpYoutubeId = null;
            } else {
              _tmpYoutubeId = _cursor.getString(_cursorIndexOfYoutubeId);
            }
            final String _tmpCoverUri;
            if (_cursor.isNull(_cursorIndexOfCoverUri)) {
              _tmpCoverUri = null;
            } else {
              _tmpCoverUri = _cursor.getString(_cursorIndexOfCoverUri);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final Integer _tmpSampleRate;
            if (_cursor.isNull(_cursorIndexOfSampleRate)) {
              _tmpSampleRate = null;
            } else {
              _tmpSampleRate = _cursor.getInt(_cursorIndexOfSampleRate);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Float _tmpReplayGainTrack;
            if (_cursor.isNull(_cursorIndexOfReplayGainTrack)) {
              _tmpReplayGainTrack = null;
            } else {
              _tmpReplayGainTrack = _cursor.getFloat(_cursorIndexOfReplayGainTrack);
            }
            final Float _tmpReplayGainAlbum;
            if (_cursor.isNull(_cursorIndexOfReplayGainAlbum)) {
              _tmpReplayGainAlbum = null;
            } else {
              _tmpReplayGainAlbum = _cursor.getFloat(_cursorIndexOfReplayGainAlbum);
            }
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpPlayCount;
            _tmpPlayCount = _cursor.getInt(_cursorIndexOfPlayCount);
            final Long _tmpLastPlayedAt;
            if (_cursor.isNull(_cursorIndexOfLastPlayedAt)) {
              _tmpLastPlayedAt = null;
            } else {
              _tmpLastPlayedAt = _cursor.getLong(_cursorIndexOfLastPlayedAt);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            _item = new TrackEntity(_tmpId,_tmpSource,_tmpTitle,_tmpArtist,_tmpAlbumArtist,_tmpAlbum,_tmpDurationMs,_tmpTrackNumber,_tmpDiscNumber,_tmpYear,_tmpGenre,_tmpFilePath,_tmpYoutubeId,_tmpCoverUri,_tmpBitrate,_tmpSampleRate,_tmpFileSize,_tmpReplayGainTrack,_tmpReplayGainAlbum,_tmpRating,_tmpPlayCount,_tmpLastPlayedAt,_tmpIsFavorite,_tmpAddedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TrackEntity>> getMostPlayed(final int limit) {
    final String _sql = "SELECT * FROM tracks ORDER BY play_count DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks"}, new Callable<List<TrackEntity>>() {
      @Override
      @NonNull
      public List<TrackEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "artist");
          final int _cursorIndexOfAlbumArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "album_artist");
          final int _cursorIndexOfAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "album");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_ms");
          final int _cursorIndexOfTrackNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "track_number");
          final int _cursorIndexOfDiscNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "disc_number");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfGenre = CursorUtil.getColumnIndexOrThrow(_cursor, "genre");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfYoutubeId = CursorUtil.getColumnIndexOrThrow(_cursor, "youtube_id");
          final int _cursorIndexOfCoverUri = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_uri");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfSampleRate = CursorUtil.getColumnIndexOrThrow(_cursor, "sample_rate");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfReplayGainTrack = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_track");
          final int _cursorIndexOfReplayGainAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_album");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfPlayCount = CursorUtil.getColumnIndexOrThrow(_cursor, "play_count");
          final int _cursorIndexOfLastPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_played_at");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "added_at");
          final List<TrackEntity> _result = new ArrayList<TrackEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrackEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpArtist;
            if (_cursor.isNull(_cursorIndexOfArtist)) {
              _tmpArtist = null;
            } else {
              _tmpArtist = _cursor.getString(_cursorIndexOfArtist);
            }
            final String _tmpAlbumArtist;
            if (_cursor.isNull(_cursorIndexOfAlbumArtist)) {
              _tmpAlbumArtist = null;
            } else {
              _tmpAlbumArtist = _cursor.getString(_cursorIndexOfAlbumArtist);
            }
            final String _tmpAlbum;
            if (_cursor.isNull(_cursorIndexOfAlbum)) {
              _tmpAlbum = null;
            } else {
              _tmpAlbum = _cursor.getString(_cursorIndexOfAlbum);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final Integer _tmpTrackNumber;
            if (_cursor.isNull(_cursorIndexOfTrackNumber)) {
              _tmpTrackNumber = null;
            } else {
              _tmpTrackNumber = _cursor.getInt(_cursorIndexOfTrackNumber);
            }
            final Integer _tmpDiscNumber;
            if (_cursor.isNull(_cursorIndexOfDiscNumber)) {
              _tmpDiscNumber = null;
            } else {
              _tmpDiscNumber = _cursor.getInt(_cursorIndexOfDiscNumber);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpGenre;
            if (_cursor.isNull(_cursorIndexOfGenre)) {
              _tmpGenre = null;
            } else {
              _tmpGenre = _cursor.getString(_cursorIndexOfGenre);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpYoutubeId;
            if (_cursor.isNull(_cursorIndexOfYoutubeId)) {
              _tmpYoutubeId = null;
            } else {
              _tmpYoutubeId = _cursor.getString(_cursorIndexOfYoutubeId);
            }
            final String _tmpCoverUri;
            if (_cursor.isNull(_cursorIndexOfCoverUri)) {
              _tmpCoverUri = null;
            } else {
              _tmpCoverUri = _cursor.getString(_cursorIndexOfCoverUri);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final Integer _tmpSampleRate;
            if (_cursor.isNull(_cursorIndexOfSampleRate)) {
              _tmpSampleRate = null;
            } else {
              _tmpSampleRate = _cursor.getInt(_cursorIndexOfSampleRate);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Float _tmpReplayGainTrack;
            if (_cursor.isNull(_cursorIndexOfReplayGainTrack)) {
              _tmpReplayGainTrack = null;
            } else {
              _tmpReplayGainTrack = _cursor.getFloat(_cursorIndexOfReplayGainTrack);
            }
            final Float _tmpReplayGainAlbum;
            if (_cursor.isNull(_cursorIndexOfReplayGainAlbum)) {
              _tmpReplayGainAlbum = null;
            } else {
              _tmpReplayGainAlbum = _cursor.getFloat(_cursorIndexOfReplayGainAlbum);
            }
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpPlayCount;
            _tmpPlayCount = _cursor.getInt(_cursorIndexOfPlayCount);
            final Long _tmpLastPlayedAt;
            if (_cursor.isNull(_cursorIndexOfLastPlayedAt)) {
              _tmpLastPlayedAt = null;
            } else {
              _tmpLastPlayedAt = _cursor.getLong(_cursorIndexOfLastPlayedAt);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            _item = new TrackEntity(_tmpId,_tmpSource,_tmpTitle,_tmpArtist,_tmpAlbumArtist,_tmpAlbum,_tmpDurationMs,_tmpTrackNumber,_tmpDiscNumber,_tmpYear,_tmpGenre,_tmpFilePath,_tmpYoutubeId,_tmpCoverUri,_tmpBitrate,_tmpSampleRate,_tmpFileSize,_tmpReplayGainTrack,_tmpReplayGainAlbum,_tmpRating,_tmpPlayCount,_tmpLastPlayedAt,_tmpIsFavorite,_tmpAddedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TrackEntity>> getRecentlyAdded(final int limit) {
    final String _sql = "SELECT * FROM tracks ORDER BY added_at DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks"}, new Callable<List<TrackEntity>>() {
      @Override
      @NonNull
      public List<TrackEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "artist");
          final int _cursorIndexOfAlbumArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "album_artist");
          final int _cursorIndexOfAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "album");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_ms");
          final int _cursorIndexOfTrackNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "track_number");
          final int _cursorIndexOfDiscNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "disc_number");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfGenre = CursorUtil.getColumnIndexOrThrow(_cursor, "genre");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfYoutubeId = CursorUtil.getColumnIndexOrThrow(_cursor, "youtube_id");
          final int _cursorIndexOfCoverUri = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_uri");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfSampleRate = CursorUtil.getColumnIndexOrThrow(_cursor, "sample_rate");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfReplayGainTrack = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_track");
          final int _cursorIndexOfReplayGainAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_album");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfPlayCount = CursorUtil.getColumnIndexOrThrow(_cursor, "play_count");
          final int _cursorIndexOfLastPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_played_at");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "added_at");
          final List<TrackEntity> _result = new ArrayList<TrackEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrackEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpArtist;
            if (_cursor.isNull(_cursorIndexOfArtist)) {
              _tmpArtist = null;
            } else {
              _tmpArtist = _cursor.getString(_cursorIndexOfArtist);
            }
            final String _tmpAlbumArtist;
            if (_cursor.isNull(_cursorIndexOfAlbumArtist)) {
              _tmpAlbumArtist = null;
            } else {
              _tmpAlbumArtist = _cursor.getString(_cursorIndexOfAlbumArtist);
            }
            final String _tmpAlbum;
            if (_cursor.isNull(_cursorIndexOfAlbum)) {
              _tmpAlbum = null;
            } else {
              _tmpAlbum = _cursor.getString(_cursorIndexOfAlbum);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final Integer _tmpTrackNumber;
            if (_cursor.isNull(_cursorIndexOfTrackNumber)) {
              _tmpTrackNumber = null;
            } else {
              _tmpTrackNumber = _cursor.getInt(_cursorIndexOfTrackNumber);
            }
            final Integer _tmpDiscNumber;
            if (_cursor.isNull(_cursorIndexOfDiscNumber)) {
              _tmpDiscNumber = null;
            } else {
              _tmpDiscNumber = _cursor.getInt(_cursorIndexOfDiscNumber);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpGenre;
            if (_cursor.isNull(_cursorIndexOfGenre)) {
              _tmpGenre = null;
            } else {
              _tmpGenre = _cursor.getString(_cursorIndexOfGenre);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpYoutubeId;
            if (_cursor.isNull(_cursorIndexOfYoutubeId)) {
              _tmpYoutubeId = null;
            } else {
              _tmpYoutubeId = _cursor.getString(_cursorIndexOfYoutubeId);
            }
            final String _tmpCoverUri;
            if (_cursor.isNull(_cursorIndexOfCoverUri)) {
              _tmpCoverUri = null;
            } else {
              _tmpCoverUri = _cursor.getString(_cursorIndexOfCoverUri);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final Integer _tmpSampleRate;
            if (_cursor.isNull(_cursorIndexOfSampleRate)) {
              _tmpSampleRate = null;
            } else {
              _tmpSampleRate = _cursor.getInt(_cursorIndexOfSampleRate);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Float _tmpReplayGainTrack;
            if (_cursor.isNull(_cursorIndexOfReplayGainTrack)) {
              _tmpReplayGainTrack = null;
            } else {
              _tmpReplayGainTrack = _cursor.getFloat(_cursorIndexOfReplayGainTrack);
            }
            final Float _tmpReplayGainAlbum;
            if (_cursor.isNull(_cursorIndexOfReplayGainAlbum)) {
              _tmpReplayGainAlbum = null;
            } else {
              _tmpReplayGainAlbum = _cursor.getFloat(_cursorIndexOfReplayGainAlbum);
            }
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpPlayCount;
            _tmpPlayCount = _cursor.getInt(_cursorIndexOfPlayCount);
            final Long _tmpLastPlayedAt;
            if (_cursor.isNull(_cursorIndexOfLastPlayedAt)) {
              _tmpLastPlayedAt = null;
            } else {
              _tmpLastPlayedAt = _cursor.getLong(_cursorIndexOfLastPlayedAt);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            _item = new TrackEntity(_tmpId,_tmpSource,_tmpTitle,_tmpArtist,_tmpAlbumArtist,_tmpAlbum,_tmpDurationMs,_tmpTrackNumber,_tmpDiscNumber,_tmpYear,_tmpGenre,_tmpFilePath,_tmpYoutubeId,_tmpCoverUri,_tmpBitrate,_tmpSampleRate,_tmpFileSize,_tmpReplayGainTrack,_tmpReplayGainAlbum,_tmpRating,_tmpPlayCount,_tmpLastPlayedAt,_tmpIsFavorite,_tmpAddedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TrackEntity>> searchTracks(final String query) {
    final String _sql = "\n"
            + "        SELECT * FROM tracks WHERE id IN \n"
            + "        (SELECT docid FROM tracks_fts WHERE tracks_fts MATCH ?)\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks",
        "tracks_fts"}, new Callable<List<TrackEntity>>() {
      @Override
      @NonNull
      public List<TrackEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "artist");
          final int _cursorIndexOfAlbumArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "album_artist");
          final int _cursorIndexOfAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "album");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_ms");
          final int _cursorIndexOfTrackNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "track_number");
          final int _cursorIndexOfDiscNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "disc_number");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfGenre = CursorUtil.getColumnIndexOrThrow(_cursor, "genre");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfYoutubeId = CursorUtil.getColumnIndexOrThrow(_cursor, "youtube_id");
          final int _cursorIndexOfCoverUri = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_uri");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfSampleRate = CursorUtil.getColumnIndexOrThrow(_cursor, "sample_rate");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfReplayGainTrack = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_track");
          final int _cursorIndexOfReplayGainAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_album");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfPlayCount = CursorUtil.getColumnIndexOrThrow(_cursor, "play_count");
          final int _cursorIndexOfLastPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_played_at");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "added_at");
          final List<TrackEntity> _result = new ArrayList<TrackEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrackEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpArtist;
            if (_cursor.isNull(_cursorIndexOfArtist)) {
              _tmpArtist = null;
            } else {
              _tmpArtist = _cursor.getString(_cursorIndexOfArtist);
            }
            final String _tmpAlbumArtist;
            if (_cursor.isNull(_cursorIndexOfAlbumArtist)) {
              _tmpAlbumArtist = null;
            } else {
              _tmpAlbumArtist = _cursor.getString(_cursorIndexOfAlbumArtist);
            }
            final String _tmpAlbum;
            if (_cursor.isNull(_cursorIndexOfAlbum)) {
              _tmpAlbum = null;
            } else {
              _tmpAlbum = _cursor.getString(_cursorIndexOfAlbum);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final Integer _tmpTrackNumber;
            if (_cursor.isNull(_cursorIndexOfTrackNumber)) {
              _tmpTrackNumber = null;
            } else {
              _tmpTrackNumber = _cursor.getInt(_cursorIndexOfTrackNumber);
            }
            final Integer _tmpDiscNumber;
            if (_cursor.isNull(_cursorIndexOfDiscNumber)) {
              _tmpDiscNumber = null;
            } else {
              _tmpDiscNumber = _cursor.getInt(_cursorIndexOfDiscNumber);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpGenre;
            if (_cursor.isNull(_cursorIndexOfGenre)) {
              _tmpGenre = null;
            } else {
              _tmpGenre = _cursor.getString(_cursorIndexOfGenre);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpYoutubeId;
            if (_cursor.isNull(_cursorIndexOfYoutubeId)) {
              _tmpYoutubeId = null;
            } else {
              _tmpYoutubeId = _cursor.getString(_cursorIndexOfYoutubeId);
            }
            final String _tmpCoverUri;
            if (_cursor.isNull(_cursorIndexOfCoverUri)) {
              _tmpCoverUri = null;
            } else {
              _tmpCoverUri = _cursor.getString(_cursorIndexOfCoverUri);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final Integer _tmpSampleRate;
            if (_cursor.isNull(_cursorIndexOfSampleRate)) {
              _tmpSampleRate = null;
            } else {
              _tmpSampleRate = _cursor.getInt(_cursorIndexOfSampleRate);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Float _tmpReplayGainTrack;
            if (_cursor.isNull(_cursorIndexOfReplayGainTrack)) {
              _tmpReplayGainTrack = null;
            } else {
              _tmpReplayGainTrack = _cursor.getFloat(_cursorIndexOfReplayGainTrack);
            }
            final Float _tmpReplayGainAlbum;
            if (_cursor.isNull(_cursorIndexOfReplayGainAlbum)) {
              _tmpReplayGainAlbum = null;
            } else {
              _tmpReplayGainAlbum = _cursor.getFloat(_cursorIndexOfReplayGainAlbum);
            }
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpPlayCount;
            _tmpPlayCount = _cursor.getInt(_cursorIndexOfPlayCount);
            final Long _tmpLastPlayedAt;
            if (_cursor.isNull(_cursorIndexOfLastPlayedAt)) {
              _tmpLastPlayedAt = null;
            } else {
              _tmpLastPlayedAt = _cursor.getLong(_cursorIndexOfLastPlayedAt);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            _item = new TrackEntity(_tmpId,_tmpSource,_tmpTitle,_tmpArtist,_tmpAlbumArtist,_tmpAlbum,_tmpDurationMs,_tmpTrackNumber,_tmpDiscNumber,_tmpYear,_tmpGenre,_tmpFilePath,_tmpYoutubeId,_tmpCoverUri,_tmpBitrate,_tmpSampleRate,_tmpFileSize,_tmpReplayGainTrack,_tmpReplayGainAlbum,_tmpRating,_tmpPlayCount,_tmpLastPlayedAt,_tmpIsFavorite,_tmpAddedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getLocalTrackCount() {
    final String _sql = "SELECT COUNT(*) FROM tracks WHERE source = 'local'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllLocalFilePaths(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT file_path FROM tracks WHERE source = 'local'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            if (_cursor.isNull(0)) {
              _item = null;
            } else {
              _item = _cursor.getString(0);
            }
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTrackByFilePath(final String filePath,
      final Continuation<? super TrackEntity> $completion) {
    final String _sql = "SELECT * FROM tracks WHERE file_path = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, filePath);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TrackEntity>() {
      @Override
      @Nullable
      public TrackEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "artist");
          final int _cursorIndexOfAlbumArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "album_artist");
          final int _cursorIndexOfAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "album");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_ms");
          final int _cursorIndexOfTrackNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "track_number");
          final int _cursorIndexOfDiscNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "disc_number");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfGenre = CursorUtil.getColumnIndexOrThrow(_cursor, "genre");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfYoutubeId = CursorUtil.getColumnIndexOrThrow(_cursor, "youtube_id");
          final int _cursorIndexOfCoverUri = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_uri");
          final int _cursorIndexOfBitrate = CursorUtil.getColumnIndexOrThrow(_cursor, "bitrate");
          final int _cursorIndexOfSampleRate = CursorUtil.getColumnIndexOrThrow(_cursor, "sample_rate");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfReplayGainTrack = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_track");
          final int _cursorIndexOfReplayGainAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "replay_gain_album");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfPlayCount = CursorUtil.getColumnIndexOrThrow(_cursor, "play_count");
          final int _cursorIndexOfLastPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_played_at");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "added_at");
          final TrackEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpArtist;
            if (_cursor.isNull(_cursorIndexOfArtist)) {
              _tmpArtist = null;
            } else {
              _tmpArtist = _cursor.getString(_cursorIndexOfArtist);
            }
            final String _tmpAlbumArtist;
            if (_cursor.isNull(_cursorIndexOfAlbumArtist)) {
              _tmpAlbumArtist = null;
            } else {
              _tmpAlbumArtist = _cursor.getString(_cursorIndexOfAlbumArtist);
            }
            final String _tmpAlbum;
            if (_cursor.isNull(_cursorIndexOfAlbum)) {
              _tmpAlbum = null;
            } else {
              _tmpAlbum = _cursor.getString(_cursorIndexOfAlbum);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final Integer _tmpTrackNumber;
            if (_cursor.isNull(_cursorIndexOfTrackNumber)) {
              _tmpTrackNumber = null;
            } else {
              _tmpTrackNumber = _cursor.getInt(_cursorIndexOfTrackNumber);
            }
            final Integer _tmpDiscNumber;
            if (_cursor.isNull(_cursorIndexOfDiscNumber)) {
              _tmpDiscNumber = null;
            } else {
              _tmpDiscNumber = _cursor.getInt(_cursorIndexOfDiscNumber);
            }
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpGenre;
            if (_cursor.isNull(_cursorIndexOfGenre)) {
              _tmpGenre = null;
            } else {
              _tmpGenre = _cursor.getString(_cursorIndexOfGenre);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpYoutubeId;
            if (_cursor.isNull(_cursorIndexOfYoutubeId)) {
              _tmpYoutubeId = null;
            } else {
              _tmpYoutubeId = _cursor.getString(_cursorIndexOfYoutubeId);
            }
            final String _tmpCoverUri;
            if (_cursor.isNull(_cursorIndexOfCoverUri)) {
              _tmpCoverUri = null;
            } else {
              _tmpCoverUri = _cursor.getString(_cursorIndexOfCoverUri);
            }
            final Integer _tmpBitrate;
            if (_cursor.isNull(_cursorIndexOfBitrate)) {
              _tmpBitrate = null;
            } else {
              _tmpBitrate = _cursor.getInt(_cursorIndexOfBitrate);
            }
            final Integer _tmpSampleRate;
            if (_cursor.isNull(_cursorIndexOfSampleRate)) {
              _tmpSampleRate = null;
            } else {
              _tmpSampleRate = _cursor.getInt(_cursorIndexOfSampleRate);
            }
            final Long _tmpFileSize;
            if (_cursor.isNull(_cursorIndexOfFileSize)) {
              _tmpFileSize = null;
            } else {
              _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            }
            final Float _tmpReplayGainTrack;
            if (_cursor.isNull(_cursorIndexOfReplayGainTrack)) {
              _tmpReplayGainTrack = null;
            } else {
              _tmpReplayGainTrack = _cursor.getFloat(_cursorIndexOfReplayGainTrack);
            }
            final Float _tmpReplayGainAlbum;
            if (_cursor.isNull(_cursorIndexOfReplayGainAlbum)) {
              _tmpReplayGainAlbum = null;
            } else {
              _tmpReplayGainAlbum = _cursor.getFloat(_cursorIndexOfReplayGainAlbum);
            }
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpPlayCount;
            _tmpPlayCount = _cursor.getInt(_cursorIndexOfPlayCount);
            final Long _tmpLastPlayedAt;
            if (_cursor.isNull(_cursorIndexOfLastPlayedAt)) {
              _tmpLastPlayedAt = null;
            } else {
              _tmpLastPlayedAt = _cursor.getLong(_cursorIndexOfLastPlayedAt);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            _result = new TrackEntity(_tmpId,_tmpSource,_tmpTitle,_tmpArtist,_tmpAlbumArtist,_tmpAlbum,_tmpDurationMs,_tmpTrackNumber,_tmpDiscNumber,_tmpYear,_tmpGenre,_tmpFilePath,_tmpYoutubeId,_tmpCoverUri,_tmpBitrate,_tmpSampleRate,_tmpFileSize,_tmpReplayGainTrack,_tmpReplayGainAlbum,_tmpRating,_tmpPlayCount,_tmpLastPlayedAt,_tmpIsFavorite,_tmpAddedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
