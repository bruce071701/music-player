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
import com.app.musicplayer.core.database.entity.EqPresetEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class EqPresetDao_Impl implements EqPresetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EqPresetEntity> __insertionAdapterOfEqPresetEntity;

  private final EntityDeletionOrUpdateAdapter<EqPresetEntity> __updateAdapterOfEqPresetEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeletePreset;

  public EqPresetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEqPresetEntity = new EntityInsertionAdapter<EqPresetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `eq_presets` (`id`,`name`,`is_builtin`,`bands_json`,`preamp`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EqPresetEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        final int _tmp = entity.isBuiltin() ? 1 : 0;
        statement.bindLong(3, _tmp);
        statement.bindString(4, entity.getBandsJson());
        statement.bindDouble(5, entity.getPreamp());
      }
    };
    this.__updateAdapterOfEqPresetEntity = new EntityDeletionOrUpdateAdapter<EqPresetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `eq_presets` SET `id` = ?,`name` = ?,`is_builtin` = ?,`bands_json` = ?,`preamp` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EqPresetEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        final int _tmp = entity.isBuiltin() ? 1 : 0;
        statement.bindLong(3, _tmp);
        statement.bindString(4, entity.getBandsJson());
        statement.bindDouble(5, entity.getPreamp());
        statement.bindLong(6, entity.getId());
      }
    };
    this.__preparedStmtOfDeletePreset = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM eq_presets WHERE id = ? AND is_builtin = 0";
        return _query;
      }
    };
  }

  @Override
  public Object insertPreset(final EqPresetEntity preset,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfEqPresetEntity.insertAndReturnId(preset);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPresets(final List<EqPresetEntity> presets,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEqPresetEntity.insert(presets);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePreset(final EqPresetEntity preset,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfEqPresetEntity.handle(preset);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePreset(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePreset.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDeletePreset.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EqPresetEntity>> getAllPresets() {
    final String _sql = "SELECT * FROM eq_presets ORDER BY is_builtin DESC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"eq_presets"}, new Callable<List<EqPresetEntity>>() {
      @Override
      @NonNull
      public List<EqPresetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIsBuiltin = CursorUtil.getColumnIndexOrThrow(_cursor, "is_builtin");
          final int _cursorIndexOfBandsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "bands_json");
          final int _cursorIndexOfPreamp = CursorUtil.getColumnIndexOrThrow(_cursor, "preamp");
          final List<EqPresetEntity> _result = new ArrayList<EqPresetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EqPresetEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final boolean _tmpIsBuiltin;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBuiltin);
            _tmpIsBuiltin = _tmp != 0;
            final String _tmpBandsJson;
            _tmpBandsJson = _cursor.getString(_cursorIndexOfBandsJson);
            final float _tmpPreamp;
            _tmpPreamp = _cursor.getFloat(_cursorIndexOfPreamp);
            _item = new EqPresetEntity(_tmpId,_tmpName,_tmpIsBuiltin,_tmpBandsJson,_tmpPreamp);
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
  public Object getPresetById(final long id,
      final Continuation<? super EqPresetEntity> $completion) {
    final String _sql = "SELECT * FROM eq_presets WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EqPresetEntity>() {
      @Override
      @Nullable
      public EqPresetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIsBuiltin = CursorUtil.getColumnIndexOrThrow(_cursor, "is_builtin");
          final int _cursorIndexOfBandsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "bands_json");
          final int _cursorIndexOfPreamp = CursorUtil.getColumnIndexOrThrow(_cursor, "preamp");
          final EqPresetEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final boolean _tmpIsBuiltin;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBuiltin);
            _tmpIsBuiltin = _tmp != 0;
            final String _tmpBandsJson;
            _tmpBandsJson = _cursor.getString(_cursorIndexOfBandsJson);
            final float _tmpPreamp;
            _tmpPreamp = _cursor.getFloat(_cursorIndexOfPreamp);
            _result = new EqPresetEntity(_tmpId,_tmpName,_tmpIsBuiltin,_tmpBandsJson,_tmpPreamp);
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
  public Object getPresetCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM eq_presets";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
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
