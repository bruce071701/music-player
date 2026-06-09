package com.app.musicplayer.core.network.musicbrainz;

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
public final class CoverArtService_Factory implements Factory<CoverArtService> {
  private final Provider<MusicBrainzApiService> apiProvider;

  public CoverArtService_Factory(Provider<MusicBrainzApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public CoverArtService get() {
    return newInstance(apiProvider.get());
  }

  public static CoverArtService_Factory create(Provider<MusicBrainzApiService> apiProvider) {
    return new CoverArtService_Factory(apiProvider);
  }

  public static CoverArtService newInstance(MusicBrainzApiService api) {
    return new CoverArtService(api);
  }
}
