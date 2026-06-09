package com.app.musicplayer.core.media;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class PlayerController_Factory implements Factory<PlayerController> {
  private final Provider<Context> contextProvider;

  private final Provider<PlayQueueManager> queueManagerProvider;

  public PlayerController_Factory(Provider<Context> contextProvider,
      Provider<PlayQueueManager> queueManagerProvider) {
    this.contextProvider = contextProvider;
    this.queueManagerProvider = queueManagerProvider;
  }

  @Override
  public PlayerController get() {
    return newInstance(contextProvider.get(), queueManagerProvider.get());
  }

  public static PlayerController_Factory create(Provider<Context> contextProvider,
      Provider<PlayQueueManager> queueManagerProvider) {
    return new PlayerController_Factory(contextProvider, queueManagerProvider);
  }

  public static PlayerController newInstance(Context context, PlayQueueManager queueManager) {
    return new PlayerController(context, queueManager);
  }
}
