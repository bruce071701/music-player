package com.app.musicplayer.feature.backup;

import com.app.musicplayer.core.database.dao.EqPresetDao;
import com.app.musicplayer.core.database.dao.PlaylistDao;
import com.app.musicplayer.core.database.dao.TrackDao;
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
public final class BackupViewModel_Factory implements Factory<BackupViewModel> {
  private final Provider<TrackDao> trackDaoProvider;

  private final Provider<PlaylistDao> playlistDaoProvider;

  private final Provider<EqPresetDao> eqPresetDaoProvider;

  public BackupViewModel_Factory(Provider<TrackDao> trackDaoProvider,
      Provider<PlaylistDao> playlistDaoProvider, Provider<EqPresetDao> eqPresetDaoProvider) {
    this.trackDaoProvider = trackDaoProvider;
    this.playlistDaoProvider = playlistDaoProvider;
    this.eqPresetDaoProvider = eqPresetDaoProvider;
  }

  @Override
  public BackupViewModel get() {
    return newInstance(trackDaoProvider.get(), playlistDaoProvider.get(), eqPresetDaoProvider.get());
  }

  public static BackupViewModel_Factory create(Provider<TrackDao> trackDaoProvider,
      Provider<PlaylistDao> playlistDaoProvider, Provider<EqPresetDao> eqPresetDaoProvider) {
    return new BackupViewModel_Factory(trackDaoProvider, playlistDaoProvider, eqPresetDaoProvider);
  }

  public static BackupViewModel newInstance(TrackDao trackDao, PlaylistDao playlistDao,
      EqPresetDao eqPresetDao) {
    return new BackupViewModel(trackDao, playlistDao, eqPresetDao);
  }
}
