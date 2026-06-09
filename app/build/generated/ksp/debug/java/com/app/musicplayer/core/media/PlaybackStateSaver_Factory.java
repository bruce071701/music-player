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
public final class PlaybackStateSaver_Factory implements Factory<PlaybackStateSaver> {
  private final Provider<AppPreferences> preferencesProvider;

  private final Provider<PlayerController> playerControllerProvider;

  private final Provider<PlayQueueManager> queueManagerProvider;

  public PlaybackStateSaver_Factory(Provider<AppPreferences> preferencesProvider,
      Provider<PlayerController> playerControllerProvider,
      Provider<PlayQueueManager> queueManagerProvider) {
    this.preferencesProvider = preferencesProvider;
    this.playerControllerProvider = playerControllerProvider;
    this.queueManagerProvider = queueManagerProvider;
  }

  @Override
  public PlaybackStateSaver get() {
    return newInstance(preferencesProvider.get(), playerControllerProvider.get(), queueManagerProvider.get());
  }

  public static PlaybackStateSaver_Factory create(Provider<AppPreferences> preferencesProvider,
      Provider<PlayerController> playerControllerProvider,
      Provider<PlayQueueManager> queueManagerProvider) {
    return new PlaybackStateSaver_Factory(preferencesProvider, playerControllerProvider, queueManagerProvider);
  }

  public static PlaybackStateSaver newInstance(AppPreferences preferences,
      PlayerController playerController, PlayQueueManager queueManager) {
    return new PlaybackStateSaver(preferences, playerController, queueManager);
  }
}
