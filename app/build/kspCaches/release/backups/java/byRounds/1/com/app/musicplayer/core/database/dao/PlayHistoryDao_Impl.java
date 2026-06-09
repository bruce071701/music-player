package com.app.musicplayer.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.app.musicplayer.core.database.entity.PlayHistoryEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class PlayHistoryDao_Impl implements PlayHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PlayHistoryEntity> __insertionAdapterOfPlayHistoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsScrobbled;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  public PlayHistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPlayHistoryEntity = new EntityInsertionAdapter<PlayHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `play_history` (`id`,`track_id`,`played_at`,`duration_played_ms`,`scrobbled`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PlayHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTrackId());
        statement.bindLong(3, entity.getPlayedAt());
        if (entity.getDurationPlayedMs() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getDurationPlayedMs());
        }
        final int _tmp = entity.getScrobbled() ? 1 : 0;
        statement.bindLong(5, _tmp);
      }
    };
    this.__preparedStmtOfMarkAsScrobbled = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE play_history SET scrobbled = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM play_history WHERE played_at < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertHistory(final PlayHistoryEntity history,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPlayHistoryEntity.insertAndReturnId(history);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsScrobbled(final long historyId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsScrobbled.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, historyId);
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
          __preparedStmtOfMarkAsScrobbled.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOlderThan(final long beforeTimestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOlderThan.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, beforeTimestamp);
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
          __preparedStmtOfDeleteOlderThan.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PlayHistoryEntity>> getRecentHistory(final int limit) {
    final String _sql = "SELECT * FROM play_history ORDER BY played_at DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"play_history"}, new Callable<List<PlayHistoryEntity>>() {
      @Override
      @NonNull
      public List<PlayHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTrackId = CursorUtil.getColumnIndexOrThrow(_cursor, "track_id");
          final int _cursorIndexOfPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "played_at");
          final int _cursorIndexOfDurationPlayedMs = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_played_ms");
          final int _cursorIndexOfScrobbled = CursorUtil.getColumnIndexOrThrow(_cursor, "scrobbled");
          final List<PlayHistoryEntity> _result = new ArrayList<PlayHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlayHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTrackId;
            _tmpTrackId = _cursor.getLong(_cursorIndexOfTrackId);
            final long _tmpPlayedAt;
            _tmpPlayedAt = _cursor.getLong(_cursorIndexOfPlayedAt);
            final Long _tmpDurationPlayedMs;
            if (_cursor.isNull(_cursorIndexOfDurationPlayedMs)) {
              _tmpDurationPlayedMs = null;
            } else {
              _tmpDurationPlayedMs = _cursor.getLong(_cursorIndexOfDurationPlayedMs);
            }
            final boolean _tmpScrobbled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfScrobbled);
            _tmpScrobbled = _tmp != 0;
            _item = new PlayHistoryEntity(_tmpId,_tmpTrackId,_tmpPlayedAt,_tmpDurationPlayedMs,_tmpScrobbled);
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
  public Object getUnscrobbledHistory(
      final Continuation<? super List<PlayHistoryEntity>> $completion) {
    final String _sql = "SELECT * FROM play_history WHERE scrobbled = 0 ORDER BY played_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PlayHistoryEntity>>() {
      @Override
      @NonNull
      public List<PlayHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTrackId = CursorUtil.getColumnIndexOrThrow(_cursor, "track_id");
          final int _cursorIndexOfPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "played_at");
          final int _cursorIndexOfDurationPlayedMs = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_played_ms");
          final int _cursorIndexOfScrobbled = CursorUtil.getColumnIndexOrThrow(_cursor, "scrobbled");
          final List<PlayHistoryEntity> _result = new ArrayList<PlayHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlayHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTrackId;
            _tmpTrackId = _cursor.getLong(_cursorIndexOfTrackId);
            final long _tmpPlayedAt;
            _tmpPlayedAt = _cursor.getLong(_cursorIndexOfPlayedAt);
            final Long _tmpDurationPlayedMs;
            if (_cursor.isNull(_cursorIndexOfDurationPlayedMs)) {
              _tmpDurationPlayedMs = null;
            } else {
              _tmpDurationPlayedMs = _cursor.getLong(_cursorIndexOfDurationPlayedMs);
            }
            final boolean _tmpScrobbled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfScrobbled);
            _tmpScrobbled = _tmp != 0;
            _item = new PlayHistoryEntity(_tmpId,_tmpTrackId,_tmpPlayedAt,_tmpDurationPlayedMs,_tmpScrobbled);
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
  public Object markAsScrobbled(final List<Long> ids,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE play_history SET scrobbled = 1 WHERE id IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (long _item : ids) {
          _stmt.bindLong(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
