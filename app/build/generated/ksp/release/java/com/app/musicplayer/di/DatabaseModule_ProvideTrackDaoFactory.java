package com.app.musicplayer.di;

import com.app.musicplayer.core.database.MusicDatabase;
import com.app.musicplayer.core.database.dao.TrackDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideTrackDaoFactory implements Factory<TrackDao> {
  private final Provider<MusicDatabase> databaseProvider;

  public DatabaseModule_ProvideTrackDaoFactory(Provider<MusicDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public TrackDao get() {
    return provideTrackDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideTrackDaoFactory create(
      Provider<MusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvideTrackDaoFactory(databaseProvider);
  }

  public static TrackDao provideTrackDao(MusicDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideTrackDao(database));
  }
}
