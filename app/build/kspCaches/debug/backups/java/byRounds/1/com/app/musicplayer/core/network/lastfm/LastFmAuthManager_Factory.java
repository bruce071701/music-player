package com.app.musicplayer.core.network.lastfm;

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
public final class LastFmAuthManager_Factory implements Factory<LastFmAuthManager> {
  private final Provider<Context> contextProvider;

  private final Provider<LastFmApiService> lastFmApiProvider;

  public LastFmAuthManager_Factory(Provider<Context> contextProvider,
      Provider<LastFmApiService> lastFmApiProvider) {
    this.contextProvider = contextProvider;
    this.lastFmApiProvider = lastFmApiProvider;
  }

  @Override
  public LastFmAuthManager get() {
    return newInstance(contextProvider.get(), lastFmApiProvider.get());
  }

  public static LastFmAuthManager_Factory create(Provider<Context> contextProvider,
      Provider<LastFmApiService> lastFmApiProvider) {
    return new LastFmAuthManager_Factory(contextProvider, lastFmApiProvider);
  }

  public static LastFmAuthManager newInstance(Context context, LastFmApiService lastFmApi) {
    return new LastFmAuthManager(context, lastFmApi);
  }
}
