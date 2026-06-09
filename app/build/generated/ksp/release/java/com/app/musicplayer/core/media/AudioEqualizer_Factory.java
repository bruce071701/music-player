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
public final class AudioEqualizer_Factory implements Factory<AudioEqualizer> {
  @Override
  public AudioEqualizer get() {
    return newInstance();
  }

  public static AudioEqualizer_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AudioEqualizer newInstance() {
    return new AudioEqualizer();
  }

  private static final class InstanceHolder {
    private static final AudioEqualizer_Factory INSTANCE = new AudioEqualizer_Factory();
  }
}
