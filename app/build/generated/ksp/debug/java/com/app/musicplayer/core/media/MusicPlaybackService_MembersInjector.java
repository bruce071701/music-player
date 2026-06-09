package com.app.musicplayer.core.media;

import com.app.musicplayer.core.datastore.AppPreferences;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MusicPlaybackService_MembersInjector implements MembersInjector<MusicPlaybackService> {
  private final Provider<AppPreferences> preferencesProvider;

  public MusicPlaybackService_MembersInjector(Provider<AppPreferences> preferencesProvider) {
    this.preferencesProvider = preferencesProvider;
  }

  public static MembersInjector<MusicPlaybackService> create(
      Provider<AppPreferences> preferencesProvider) {
    return new MusicPlaybackService_MembersInjector(preferencesProvider);
  }

  @Override
  public void injectMembers(MusicPlaybackService instance) {
    injectPreferences(instance, preferencesProvider.get());
  }

  @InjectedFieldSignature("com.app.musicplayer.core.media.MusicPlaybackService.preferences")
  public static void injectPreferences(MusicPlaybackService instance, AppPreferences preferences) {
    instance.preferences = preferences;
  }
}
