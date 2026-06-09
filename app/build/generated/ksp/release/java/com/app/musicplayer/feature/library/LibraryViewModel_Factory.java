package com.app.musicplayer.feature.library;

import com.app.musicplayer.core.database.dao.PlaylistDao;
import com.app.musicplayer.core.database.dao.TrackDao;
import com.app.musicplayer.core.datastore.AppPreferences;
import com.app.musicplayer.feature.library.scanner.MediaScanner;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class LibraryViewModel_Factory implements Factory<LibraryViewModel> {
  private final Provider<TrackDao> trackDaoProvider;

  private final Provider<PlaylistDao> playlistDaoProvider;

  private final Provider<MediaScanner> mediaScannerProvider;

  private final Provider<AppPreferences> preferencesProvider;

  public LibraryViewModel_Factory(Provider<TrackDao> trackDaoProvider,
      Provider<PlaylistDao> playlistDaoProvider, Provider<MediaScanner> mediaScannerProvider,
      Provider<AppPreferences> preferencesProvider) {
    this.trackDaoProvider = trackDaoProvider;
    this.playlistDaoProvider = playlistDaoProvider;
    this.mediaScannerProvider = mediaScannerProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public LibraryViewModel get() {
    return newInstance(trackDaoProvider.get(), playlistDaoProvider.get(), mediaScannerProvider.get(), preferencesProvider.get());
  }

  public static LibraryViewModel_Factory create(Provider<TrackDao> trackDaoProvider,
      Provider<PlaylistDao> playlistDaoProvider, Provider<MediaScanner> mediaScannerProvider,
      Provider<AppPreferences> preferencesProvider) {
    return new LibraryViewModel_Factory(trackDaoProvider, playlistDaoProvider, mediaScannerProvider, preferencesProvider);
  }

  public static LibraryViewModel newInstance(TrackDao trackDao, PlaylistDao playlistDao,
      MediaScanner mediaScanner, AppPreferences preferences) {
    return new LibraryViewModel(trackDao, playlistDao, mediaScanner, preferences);
  }
}
