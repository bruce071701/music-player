package com.app.musicplayer.feature.library.scanner;

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
public final class MediaFileObserver_Factory implements Factory<MediaFileObserver> {
  @Override
  public MediaFileObserver get() {
    return newInstance();
  }

  public static MediaFileObserver_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MediaFileObserver newInstance() {
    return new MediaFileObserver();
  }

  private static final class InstanceHolder {
    private static final MediaFileObserver_Factory INSTANCE = new MediaFileObserver_Factory();
  }
}
