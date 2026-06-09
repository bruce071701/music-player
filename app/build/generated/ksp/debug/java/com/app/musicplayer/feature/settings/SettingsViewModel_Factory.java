package com.app.musicplayer.feature.settings;

import com.app.musicplayer.core.datastore.AppPreferences;
import com.app.musicplayer.core.datastore.LanguageManager;
import com.app.musicplayer.core.media.SleepTimer;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<AppPreferences> preferencesProvider;

  private final Provider<MediaScanner> mediaScannerProvider;

  private final Provider<LanguageManager> languageManagerProvider;

  private final Provider<SleepTimer> sleepTimerProvider;

  public SettingsViewModel_Factory(Provider<AppPreferences> preferencesProvider,
      Provider<MediaScanner> mediaScannerProvider,
      Provider<LanguageManager> languageManagerProvider, Provider<SleepTimer> sleepTimerProvider) {
    this.preferencesProvider = preferencesProvider;
    this.mediaScannerProvider = mediaScannerProvider;
    this.languageManagerProvider = languageManagerProvider;
    this.sleepTimerProvider = sleepTimerProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(preferencesProvider.get(), mediaScannerProvider.get(), languageManagerProvider.get(), sleepTimerProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<AppPreferences> preferencesProvider,
      Provider<MediaScanner> mediaScannerProvider,
      Provider<LanguageManager> languageManagerProvider, Provider<SleepTimer> sleepTimerProvider) {
    return new SettingsViewModel_Factory(preferencesProvider, mediaScannerProvider, languageManagerProvider, sleepTimerProvider);
  }

  public static SettingsViewModel newInstance(AppPreferences preferences, MediaScanner mediaScanner,
      LanguageManager languageManager, SleepTimer sleepTimer) {
    return new SettingsViewModel(preferences, mediaScanner, languageManager, sleepTimer);
  }
}
