package com.app.musicplayer.feature.search;

import com.app.musicplayer.core.database.dao.TrackDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SearchViewModel_Factory implements Factory<SearchViewModel> {
  private final Provider<TrackDao> trackDaoProvider;

  public SearchViewModel_Factory(Provider<TrackDao> trackDaoProvider) {
    this.trackDaoProvider = trackDaoProvider;
  }

  @Override
  public SearchViewModel get() {
    return newInstance(trackDaoProvider.get());
  }

  public static SearchViewModel_Factory create(Provider<TrackDao> trackDaoProvider) {
    return new SearchViewModel_Factory(trackDaoProvider);
  }

  public static SearchViewModel newInstance(TrackDao trackDao) {
    return new SearchViewModel(trackDao);
  }
}
