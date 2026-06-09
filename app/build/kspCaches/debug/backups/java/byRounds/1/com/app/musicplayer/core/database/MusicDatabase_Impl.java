package com.app.musicplayer.core.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.FtsTableInfo;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.app.musicplayer.core.database.dao.EqPresetDao;
import com.app.musicplayer.core.database.dao.EqPresetDao_Impl;
import com.app.musicplayer.core.database.dao.PlayHistoryDao;
import com.app.musicplayer.core.database.dao.PlayHistoryDao_Impl;
import com.app.musicplayer.core.database.dao.PlaylistDao;
import com.app.musicplayer.core.database.dao.PlaylistDao_Impl;
import com.app.musicplayer.core.database.dao.TrackDao;
import com.app.musicplayer.core.database.dao.TrackDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MusicDatabase_Impl extends MusicDatabase {
  private volatile TrackDao _trackDao;

  private volatile PlaylistDao _playlistDao;

  private volatile PlayHistoryDao _playHistoryDao;

  private volatile EqPresetDao _eqPresetDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `tracks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `source` TEXT NOT NULL, `title` TEXT NOT NULL, `artist` TEXT, `album_artist` TEXT, `album` TEXT, `duration_ms` INTEGER NOT NULL, `track_number` INTEGER, `disc_number` INTEGER, `year` INTEGER, `genre` TEXT, `file_path` TEXT, `youtube_id` TEXT, `cover_uri` TEXT, `bitrate` INTEGER, `sample_rate` INTEGER, `file_size` INTEGER, `replay_gain_track` REAL, `replay_gain_album` REAL, `rating` INTEGER NOT NULL, `play_count` INTEGER NOT NULL, `last_played_at` INTEGER, `is_favorite` INTEGER NOT NULL, `added_at` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_title` ON `tracks` (`title`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_artist` ON `tracks` (`artist`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_album` ON `tracks` (`album`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_genre` ON `tracks` (`genre`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_tracks_file_path` ON `tracks` (`file_path`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_play_count` ON `tracks` (`play_count`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_last_played_at` ON `tracks` (`last_played_at`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_is_favorite` ON `tracks` (`is_favorite`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_added_at` ON `tracks` (`added_at`)");
        db.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `tracks_fts` USING FTS4(`title` TEXT NOT NULL, `artist` TEXT, `album` TEXT, content=`tracks`)");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_tracks_fts_BEFORE_UPDATE BEFORE UPDATE ON `tracks` BEGIN DELETE FROM `tracks_fts` WHERE `docid`=OLD.`rowid`; END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_tracks_fts_BEFORE_DELETE BEFORE DELETE ON `tracks` BEGIN DELETE FROM `tracks_fts` WHERE `docid`=OLD.`rowid`; END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_tracks_fts_AFTER_UPDATE AFTER UPDATE ON `tracks` BEGIN INSERT INTO `tracks_fts`(`docid`, `title`, `artist`, `album`) VALUES (NEW.`rowid`, NEW.`title`, NEW.`artist`, NEW.`album`); END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_tracks_fts_AFTER_INSERT AFTER INSERT ON `tracks` BEGIN INSERT INTO `tracks_fts`(`docid`, `title`, `artist`, `album`) VALUES (NEW.`rowid`, NEW.`title`, NEW.`artist`, NEW.`album`); END");
        db.execSQL("CREATE TABLE IF NOT EXISTS `playlists` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `smart_rules` TEXT, `cover_uri` TEXT, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `playlist_tracks` (`playlist_id` INTEGER NOT NULL, `track_id` INTEGER NOT NULL, `position` INTEGER NOT NULL, `added_at` INTEGER NOT NULL, PRIMARY KEY(`playlist_id`, `track_id`), FOREIGN KEY(`playlist_id`) REFERENCES `playlists`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`track_id`) REFERENCES `tracks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_playlist_tracks_playlist_id` ON `playlist_tracks` (`playlist_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_playlist_tracks_track_id` ON `playlist_tracks` (`track_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `play_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `track_id` INTEGER NOT NULL, `played_at` INTEGER NOT NULL, `duration_played_ms` INTEGER, `scrobbled` INTEGER NOT NULL, FOREIGN KEY(`track_id`) REFERENCES `tracks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_play_history_track_id` ON `play_history` (`track_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_play_history_played_at` ON `play_history` (`played_at`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `eq_presets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `is_builtin` INTEGER NOT NULL, `bands_json` TEXT NOT NULL, `preamp` REAL NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_eq_presets_name` ON `eq_presets` (`name`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '21e4db22204e2defc6d0aa51f1de54f9')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `tracks`");
        db.execSQL("DROP TABLE IF EXISTS `tracks_fts`");
        db.execSQL("DROP TABLE IF EXISTS `playlists`");
        db.execSQL("DROP TABLE IF EXISTS `playlist_tracks`");
        db.execSQL("DROP TABLE IF EXISTS `play_history`");
        db.execSQL("DROP TABLE IF EXISTS `eq_presets`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_tracks_fts_BEFORE_UPDATE BEFORE UPDATE ON `tracks` BEGIN DELETE FROM `tracks_fts` WHERE `docid`=OLD.`rowid`; END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_tracks_fts_BEFORE_DELETE BEFORE DELETE ON `tracks` BEGIN DELETE FROM `tracks_fts` WHERE `docid`=OLD.`rowid`; END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_tracks_fts_AFTER_UPDATE AFTER UPDATE ON `tracks` BEGIN INSERT INTO `tracks_fts`(`docid`, `title`, `artist`, `album`) VALUES (NEW.`rowid`, NEW.`title`, NEW.`artist`, NEW.`album`); END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_tracks_fts_AFTER_INSERT AFTER INSERT ON `tracks` BEGIN INSERT INTO `tracks_fts`(`docid`, `title`, `artist`, `album`) VALUES (NEW.`rowid`, NEW.`title`, NEW.`artist`, NEW.`album`); END");
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsTracks = new HashMap<String, TableInfo.Column>(24);
        _columnsTracks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("artist", new TableInfo.Column("artist", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("album_artist", new TableInfo.Column("album_artist", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("album", new TableInfo.Column("album", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("duration_ms", new TableInfo.Column("duration_ms", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("track_number", new TableInfo.Column("track_number", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("disc_number", new TableInfo.Column("disc_number", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("year", new TableInfo.Column("year", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("genre", new TableInfo.Column("genre", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("file_path", new TableInfo.Column("file_path", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("youtube_id", new TableInfo.Column("youtube_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("cover_uri", new TableInfo.Column("cover_uri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("bitrate", new TableInfo.Column("bitrate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("sample_rate", new TableInfo.Column("sample_rate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("file_size", new TableInfo.Column("file_size", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("replay_gain_track", new TableInfo.Column("replay_gain_track", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("replay_gain_album", new TableInfo.Column("replay_gain_album", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("rating", new TableInfo.Column("rating", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("play_count", new TableInfo.Column("play_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("last_played_at", new TableInfo.Column("last_played_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("is_favorite", new TableInfo.Column("is_favorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("added_at", new TableInfo.Column("added_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTracks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTracks = new HashSet<TableInfo.Index>(9);
        _indicesTracks.add(new TableInfo.Index("index_tracks_title", false, Arrays.asList("title"), Arrays.asList("ASC")));
        _indicesTracks.add(new TableInfo.Index("index_tracks_artist", false, Arrays.asList("artist"), Arrays.asList("ASC")));
        _indicesTracks.add(new TableInfo.Index("index_tracks_album", false, Arrays.asList("album"), Arrays.asList("ASC")));
        _indicesTracks.add(new TableInfo.Index("index_tracks_genre", false, Arrays.asList("genre"), Arrays.asList("ASC")));
        _indicesTracks.add(new TableInfo.Index("index_tracks_file_path", true, Arrays.asList("file_path"), Arrays.asList("ASC")));
        _indicesTracks.add(new TableInfo.Index("index_tracks_play_count", false, Arrays.asList("play_count"), Arrays.asList("ASC")));
        _indicesTracks.add(new TableInfo.Index("index_tracks_last_played_at", false, Arrays.asList("last_played_at"), Arrays.asList("ASC")));
        _indicesTracks.add(new TableInfo.Index("index_tracks_is_favorite", false, Arrays.asList("is_favorite"), Arrays.asList("ASC")));
        _indicesTracks.add(new TableInfo.Index("index_tracks_added_at", false, Arrays.asList("added_at"), Arrays.asList("ASC")));
        final TableInfo _infoTracks = new TableInfo("tracks", _columnsTracks, _foreignKeysTracks, _indicesTracks);
        final TableInfo _existingTracks = TableInfo.read(db, "tracks");
        if (!_infoTracks.equals(_existingTracks)) {
          return new RoomOpenHelper.ValidationResult(false, "tracks(com.app.musicplayer.core.database.entity.TrackEntity).\n"
                  + " Expected:\n" + _infoTracks + "\n"
                  + " Found:\n" + _existingTracks);
        }
        final HashSet<String> _columnsTracksFts = new HashSet<String>(3);
        _columnsTracksFts.add("title");
        _columnsTracksFts.add("artist");
        _columnsTracksFts.add("album");
        final FtsTableInfo _infoTracksFts = new FtsTableInfo("tracks_fts", _columnsTracksFts, "CREATE VIRTUAL TABLE IF NOT EXISTS `tracks_fts` USING FTS4(`title` TEXT NOT NULL, `artist` TEXT, `album` TEXT, content=`tracks`)");
        final FtsTableInfo _existingTracksFts = FtsTableInfo.read(db, "tracks_fts");
        if (!_infoTracksFts.equals(_existingTracksFts)) {
          return new RoomOpenHelper.ValidationResult(false, "tracks_fts(com.app.musicplayer.core.database.entity.TrackFtsEntity).\n"
                  + " Expected:\n" + _infoTracksFts + "\n"
                  + " Found:\n" + _existingTracksFts);
        }
        final HashMap<String, TableInfo.Column> _columnsPlaylists = new HashMap<String, TableInfo.Column>(7);
        _columnsPlaylists.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("smart_rules", new TableInfo.Column("smart_rules", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("cover_uri", new TableInfo.Column("cover_uri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlaylists = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPlaylists = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPlaylists = new TableInfo("playlists", _columnsPlaylists, _foreignKeysPlaylists, _indicesPlaylists);
        final TableInfo _existingPlaylists = TableInfo.read(db, "playlists");
        if (!_infoPlaylists.equals(_existingPlaylists)) {
          return new RoomOpenHelper.ValidationResult(false, "playlists(com.app.musicplayer.core.database.entity.PlaylistEntity).\n"
                  + " Expected:\n" + _infoPlaylists + "\n"
                  + " Found:\n" + _existingPlaylists);
        }
        final HashMap<String, TableInfo.Column> _columnsPlaylistTracks = new HashMap<String, TableInfo.Column>(4);
        _columnsPlaylistTracks.put("playlist_id", new TableInfo.Column("playlist_id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylistTracks.put("track_id", new TableInfo.Column("track_id", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylistTracks.put("position", new TableInfo.Column("position", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylistTracks.put("added_at", new TableInfo.Column("added_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlaylistTracks = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysPlaylistTracks.add(new TableInfo.ForeignKey("playlists", "CASCADE", "NO ACTION", Arrays.asList("playlist_id"), Arrays.asList("id")));
        _foreignKeysPlaylistTracks.add(new TableInfo.ForeignKey("tracks", "CASCADE", "NO ACTION", Arrays.asList("track_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesPlaylistTracks = new HashSet<TableInfo.Index>(2);
        _indicesPlaylistTracks.add(new TableInfo.Index("index_playlist_tracks_playlist_id", false, Arrays.asList("playlist_id"), Arrays.asList("ASC")));
        _indicesPlaylistTracks.add(new TableInfo.Index("index_playlist_tracks_track_id", false, Arrays.asList("track_id"), Arrays.asList("ASC")));
        final TableInfo _infoPlaylistTracks = new TableInfo("playlist_tracks", _columnsPlaylistTracks, _foreignKeysPlaylistTracks, _indicesPlaylistTracks);
        final TableInfo _existingPlaylistTracks = TableInfo.read(db, "playlist_tracks");
        if (!_infoPlaylistTracks.equals(_existingPlaylistTracks)) {
          return new RoomOpenHelper.ValidationResult(false, "playlist_tracks(com.app.musicplayer.core.database.entity.PlaylistTrackEntity).\n"
                  + " Expected:\n" + _infoPlaylistTracks + "\n"
                  + " Found:\n" + _existingPlaylistTracks);
        }
        final HashMap<String, TableInfo.Column> _columnsPlayHistory = new HashMap<String, TableInfo.Column>(5);
        _columnsPlayHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayHistory.put("track_id", new TableInfo.Column("track_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayHistory.put("played_at", new TableInfo.Column("played_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayHistory.put("duration_played_ms", new TableInfo.Column("duration_played_ms", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayHistory.put("scrobbled", new TableInfo.Column("scrobbled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlayHistory = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysPlayHistory.add(new TableInfo.ForeignKey("tracks", "CASCADE", "NO ACTION", Arrays.asList("track_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesPlayHistory = new HashSet<TableInfo.Index>(2);
        _indicesPlayHistory.add(new TableInfo.Index("index_play_history_track_id", false, Arrays.asList("track_id"), Arrays.asList("ASC")));
        _indicesPlayHistory.add(new TableInfo.Index("index_play_history_played_at", false, Arrays.asList("played_at"), Arrays.asList("ASC")));
        final TableInfo _infoPlayHistory = new TableInfo("play_history", _columnsPlayHistory, _foreignKeysPlayHistory, _indicesPlayHistory);
        final TableInfo _existingPlayHistory = TableInfo.read(db, "play_history");
        if (!_infoPlayHistory.equals(_existingPlayHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "play_history(com.app.musicplayer.core.database.entity.PlayHistoryEntity).\n"
                  + " Expected:\n" + _infoPlayHistory + "\n"
                  + " Found:\n" + _existingPlayHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsEqPresets = new HashMap<String, TableInfo.Column>(5);
        _columnsEqPresets.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEqPresets.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEqPresets.put("is_builtin", new TableInfo.Column("is_builtin", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEqPresets.put("bands_json", new TableInfo.Column("bands_json", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEqPresets.put("preamp", new TableInfo.Column("preamp", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEqPresets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEqPresets = new HashSet<TableInfo.Index>(1);
        _indicesEqPresets.add(new TableInfo.Index("index_eq_presets_name", true, Arrays.asList("name"), Arrays.asList("ASC")));
        final TableInfo _infoEqPresets = new TableInfo("eq_presets", _columnsEqPresets, _foreignKeysEqPresets, _indicesEqPresets);
        final TableInfo _existingEqPresets = TableInfo.read(db, "eq_presets");
        if (!_infoEqPresets.equals(_existingEqPresets)) {
          return new RoomOpenHelper.ValidationResult(false, "eq_presets(com.app.musicplayer.core.database.entity.EqPresetEntity).\n"
                  + " Expected:\n" + _infoEqPresets + "\n"
                  + " Found:\n" + _existingEqPresets);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "21e4db22204e2defc6d0aa51f1de54f9", "347fd3e962b3a2234d60d76dd97b3894");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(1);
    _shadowTablesMap.put("tracks_fts", "tracks");
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "tracks","tracks_fts","playlists","playlist_tracks","play_history","eq_presets");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `tracks`");
      _db.execSQL("DELETE FROM `tracks_fts`");
      _db.execSQL("DELETE FROM `playlists`");
      _db.execSQL("DELETE FROM `playlist_tracks`");
      _db.execSQL("DELETE FROM `play_history`");
      _db.execSQL("DELETE FROM `eq_presets`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(TrackDao.class, TrackDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PlaylistDao.class, PlaylistDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PlayHistoryDao.class, PlayHistoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EqPresetDao.class, EqPresetDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public TrackDao trackDao() {
    if (_trackDao != null) {
      return _trackDao;
    } else {
      synchronized(this) {
        if(_trackDao == null) {
          _trackDao = new TrackDao_Impl(this);
        }
        return _trackDao;
      }
    }
  }

  @Override
  public PlaylistDao playlistDao() {
    if (_playlistDao != null) {
      return _playlistDao;
    } else {
      synchronized(this) {
        if(_playlistDao == null) {
          _playlistDao = new PlaylistDao_Impl(this);
        }
        return _playlistDao;
      }
    }
  }

  @Override
  public PlayHistoryDao playHistoryDao() {
    if (_playHistoryDao != null) {
      return _playHistoryDao;
    } else {
      synchronized(this) {
        if(_playHistoryDao == null) {
          _playHistoryDao = new PlayHistoryDao_Impl(this);
        }
        return _playHistoryDao;
      }
    }
  }

  @Override
  public EqPresetDao eqPresetDao() {
    if (_eqPresetDao != null) {
      return _eqPresetDao;
    } else {
      synchronized(this) {
        if(_eqPresetDao == null) {
          _eqPresetDao = new EqPresetDao_Impl(this);
        }
        return _eqPresetDao;
      }
    }
  }
}
