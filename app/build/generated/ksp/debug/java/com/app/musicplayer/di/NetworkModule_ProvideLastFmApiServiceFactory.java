package com.app.musicplayer.di;

import com.app.musicplayer.core.network.lastfm.LastFmApiService;
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
public final class NetworkModule_ProvideLastFmApiServiceFactory implements Factory<LastFmApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideLastFmApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public LastFmApiService get() {
    return provideLastFmApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvideLastFmApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideLastFmApiServiceFactory(retrofitProvider);
  }

  public static LastFmApiService provideLastFmApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideLastFmApiService(retrofit));
  }
}
