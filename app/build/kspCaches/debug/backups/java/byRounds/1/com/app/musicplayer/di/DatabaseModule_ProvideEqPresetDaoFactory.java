package com.app.musicplayer.di;

import com.app.musicplayer.core.database.MusicDatabase;
import com.app.musicplayer.core.database.dao.EqPresetDao;
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
public final class DatabaseModule_ProvideEqPresetDaoFactory implements Factory<EqPresetDao> {
  private final Provider<MusicDatabase> databaseProvider;

  public DatabaseModule_ProvideEqPresetDaoFactory(Provider<MusicDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public EqPresetDao get() {
    return provideEqPresetDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideEqPresetDaoFactory create(
      Provider<MusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvideEqPresetDaoFactory(databaseProvider);
  }

  public static EqPresetDao provideEqPresetDao(MusicDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideEqPresetDao(database));
  }
}
