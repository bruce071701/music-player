package com.app.musicplayer.core.network.lastfm;

import com.app.musicplayer.core.database.dao.PlayHistoryDao;
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
public final class ScrobbleManager_Factory implements Factory<ScrobbleManager> {
  private final Provider<LastFmApiService> lastFmApiProvider;

  private final Provider<LastFmAuthManager> authManagerProvider;

  private final Provider<PlayHistoryDao> playHistoryDaoProvider;

  public ScrobbleManager_Factory(Provider<LastFmApiService> lastFmApiProvider,
      Provider<LastFmAuthManager> authManagerProvider,
      Provider<PlayHistoryDao> playHistoryDaoProvider) {
    this.lastFmApiProvider = lastFmApiProvider;
    this.authManagerProvider = authManagerProvider;
    this.playHistoryDaoProvider = playHistoryDaoProvider;
  }

  @Override
  public ScrobbleManager get() {
    return newInstance(lastFmApiProvider.get(), authManagerProvider.get(), playHistoryDaoProvider.get());
  }

  public static ScrobbleManager_Factory create(Provider<LastFmApiService> lastFmApiProvider,
      Provider<LastFmAuthManager> authManagerProvider,
      Provider<PlayHistoryDao> playHistoryDaoProvider) {
    return new ScrobbleManager_Factory(lastFmApiProvider, authManagerProvider, playHistoryDaoProvider);
  }

  public static ScrobbleManager newInstance(LastFmApiService lastFmApi,
      LastFmAuthManager authManager, PlayHistoryDao playHistoryDao) {
    return new ScrobbleManager(lastFmApi, authManager, playHistoryDao);
  }
}
