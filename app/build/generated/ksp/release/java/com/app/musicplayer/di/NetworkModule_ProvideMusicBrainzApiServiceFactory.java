package com.app.musicplayer.di;

import com.app.musicplayer.core.network.musicbrainz.MusicBrainzApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("javax.inject.Named")
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
public final class NetworkModule_ProvideMusicBrainzApiServiceFactory implements Factory<MusicBrainzApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideMusicBrainzApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public MusicBrainzApiService get() {
    return provideMusicBrainzApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvideMusicBrainzApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideMusicBrainzApiServiceFactory(retrofitProvider);
  }

  public static MusicBrainzApiService provideMusicBrainzApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideMusicBrainzApiService(retrofit));
  }
}
