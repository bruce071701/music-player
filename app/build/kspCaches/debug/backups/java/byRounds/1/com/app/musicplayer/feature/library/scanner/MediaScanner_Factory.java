package com.app.musicplayer.feature.library.scanner;

import android.content.Context;
import com.app.musicplayer.core.database.dao.TrackDao;
import com.app.musicplayer.core.datastore.AppPreferences;
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
public final class MediaScanner_Factory implements Factory<MediaScanner> {
  private final Provider<Context> contextProvider;

  private final Provider<TrackDao> trackDaoProvider;

  private final Provider<AppPreferences> preferencesProvider;

  private final Provider<MetadataExtractor> metadataExtractorProvider;

  public MediaScanner_Factory(Provider<Context> contextProvider,
      Provider<TrackDao> trackDaoProvider, Provider<AppPreferences> preferencesProvider,
      Provider<MetadataExtractor> metadataExtractorProvider) {
    this.contextProvider = contextProvider;
    this.trackDaoProvider = trackDaoProvider;
    this.preferencesProvider = preferencesProvider;
    this.metadataExtractorProvider = metadataExtractorProvider;
  }

  @Override
  public MediaScanner get() {
    return newInstance(contextProvider.get(), trackDaoProvider.get(), preferencesProvider.get(), metadataExtractorProvider.get());
  }

  public static MediaScanner_Factory create(Provider<Context> contextProvider,
      Provider<TrackDao> trackDaoProvider, Provider<AppPreferences> preferencesProvider,
      Provider<MetadataExtractor> metadataExtractorProvider) {
    return new MediaScanner_Factory(contextProvider, trackDaoProvider, preferencesProvider, metadataExtractorProvider);
  }

  public static MediaScanner newInstance(Context context, TrackDao trackDao,
      AppPreferences preferences, MetadataExtractor metadataExtractor) {
    return new MediaScanner(context, trackDao, preferences, metadataExtractor);
  }
}
