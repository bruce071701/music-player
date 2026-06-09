package com.app.musicplayer.feature.player;

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
public final class LyricsManager_Factory implements Factory<LyricsManager> {
  @Override
  public LyricsManager get() {
    return newInstance();
  }

  public static LyricsManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static LyricsManager newInstance() {
    return new LyricsManager();
  }

  private static final class InstanceHolder {
    private static final LyricsManager_Factory INSTANCE = new LyricsManager_Factory();
  }
}
