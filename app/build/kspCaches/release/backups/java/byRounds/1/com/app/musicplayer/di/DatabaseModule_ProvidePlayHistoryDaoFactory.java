package com.app.musicplayer.di;

import com.app.musicplayer.core.database.MusicDatabase;
import com.app.musicplayer.core.database.dao.PlayHistoryDao;
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
public final class DatabaseModule_ProvidePlayHistoryDaoFactory implements Factory<PlayHistoryDao> {
  private final Provider<MusicDatabase> databaseProvider;

  public DatabaseModule_ProvidePlayHistoryDaoFactory(Provider<MusicDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public PlayHistoryDao get() {
    return providePlayHistoryDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvidePlayHistoryDaoFactory create(
      Provider<MusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvidePlayHistoryDaoFactory(databaseProvider);
  }

  public static PlayHistoryDao providePlayHistoryDao(MusicDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providePlayHistoryDao(database));
  }
}
