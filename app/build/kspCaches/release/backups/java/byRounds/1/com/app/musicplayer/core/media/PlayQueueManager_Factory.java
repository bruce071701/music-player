package com.app.musicplayer.core.media;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class PlayQueueManager_Factory implements Factory<PlayQueueManager> {
  @Override
  public PlayQueueManager get() {
    return newInstance();
  }

  public static PlayQueueManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PlayQueueManager newInstance() {
    return new PlayQueueManager();
  }

  private static final class InstanceHolder {
    private static final PlayQueueManager_Factory INSTANCE = new PlayQueueManager_Factory();
  }
}
