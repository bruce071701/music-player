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
public final class MetadataExtractor_Factory implements Factory<MetadataExtractor> {
  @Override
  public MetadataExtractor get() {
    return newInstance();
  }

  public static MetadataExtractor_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MetadataExtractor newInstance() {
    return new MetadataExtractor();
  }

  private static final class InstanceHolder {
    private static final MetadataExtractor_Factory INSTANCE = new MetadataExtractor_Factory();
  }
}
