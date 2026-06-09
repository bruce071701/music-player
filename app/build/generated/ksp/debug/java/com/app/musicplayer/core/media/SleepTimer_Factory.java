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
public final class SleepTimer_Factory implements Factory<SleepTimer> {
  @Override
  public SleepTimer get() {
    return newInstance();
  }

  public static SleepTimer_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SleepTimer newInstance() {
    return new SleepTimer();
  }

  private static final class InstanceHolder {
    private static final SleepTimer_Factory INSTANCE = new SleepTimer_Factory();
  }
}
