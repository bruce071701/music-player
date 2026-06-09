package com.app.musicplayer.core.media;

import com.app.musicplayer.core.datastore.AppPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ReplayGainProcessor_Factory implements Factory<ReplayGainProcessor> {
  private final Provider<AppPreferences> preferencesProvider;

  public ReplayGainProcessor_Factory(Provider<AppPreferences> preferencesProvider) {
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public ReplayGainProcessor get() {
    return newInstance(preferencesProvider.get());
  }

  public static ReplayGainProcessor_Factory create(Provider<AppPreferences> preferencesProvider) {
    return new ReplayGainProcessor_Factory(preferencesProvider);
  }

  public static ReplayGainProcessor newInstance(AppPreferences preferences) {
    return new ReplayGainProcessor(preferences);
  }
}
