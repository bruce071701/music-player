package com.app.musicplayer.feature.equalizer;

import com.app.musicplayer.core.database.dao.EqPresetDao;
import com.app.musicplayer.core.media.AudioEqualizer;
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
public final class EqualizerViewModel_Factory implements Factory<EqualizerViewModel> {
  private final Provider<AudioEqualizer> equalizerProvider;

  private final Provider<EqPresetDao> eqPresetDaoProvider;

  public EqualizerViewModel_Factory(Provider<AudioEqualizer> equalizerProvider,
      Provider<EqPresetDao> eqPresetDaoProvider) {
    this.equalizerProvider = equalizerProvider;
    this.eqPresetDaoProvider = eqPresetDaoProvider;
  }

  @Override
  public EqualizerViewModel get() {
    return newInstance(equalizerProvider.get(), eqPresetDaoProvider.get());
  }

  public static EqualizerViewModel_Factory create(Provider<AudioEqualizer> equalizerProvider,
      Provider<EqPresetDao> eqPresetDaoProvider) {
    return new EqualizerViewModel_Factory(equalizerProvider, eqPresetDaoProvider);
  }

  public static EqualizerViewModel newInstance(AudioEqualizer equalizer, EqPresetDao eqPresetDao) {
    return new EqualizerViewModel(equalizer, eqPresetDao);
  }
}
