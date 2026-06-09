package com.app.musicplayer;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.app.musicplayer.core.database.MusicDatabase;
import com.app.musicplayer.core.database.dao.EqPresetDao;
import com.app.musicplayer.core.database.dao.PlayHistoryDao;
import com.app.musicplayer.core.database.dao.PlaylistDao;
import com.app.musicplayer.core.database.dao.TrackDao;
import com.app.musicplayer.core.datastore.AppPreferences;
import com.app.musicplayer.core.datastore.LanguageManager;
import com.app.musicplayer.core.media.AudioEqualizer;
import com.app.musicplayer.core.media.MusicPlaybackService;
import com.app.musicplayer.core.media.MusicPlaybackService_MembersInjector;
import com.app.musicplayer.core.media.PlayQueueManager;
import com.app.musicplayer.core.media.PlayerController;
import com.app.musicplayer.core.media.ReplayGainProcessor;
import com.app.musicplayer.core.media.SleepTimer;
import com.app.musicplayer.di.AppModule_ProvideAppPreferencesFactory;
import com.app.musicplayer.di.DatabaseModule_ProvideDatabaseFactory;
import com.app.musicplayer.di.DatabaseModule_ProvideEqPresetDaoFactory;
import com.app.musicplayer.di.DatabaseModule_ProvidePlayHistoryDaoFactory;
import com.app.musicplayer.di.DatabaseModule_ProvidePlaylistDaoFactory;
import com.app.musicplayer.di.DatabaseModule_ProvideTrackDaoFactory;
import com.app.musicplayer.feature.backup.BackupViewModel;
import com.app.musicplayer.feature.backup.BackupViewModel_HiltModules;
import com.app.musicplayer.feature.equalizer.EqualizerViewModel;
import com.app.musicplayer.feature.equalizer.EqualizerViewModel_HiltModules;
import com.app.musicplayer.feature.library.LibraryViewModel;
import com.app.musicplayer.feature.library.LibraryViewModel_HiltModules;
import com.app.musicplayer.feature.library.scanner.MediaScanner;
import com.app.musicplayer.feature.library.scanner.MetadataExtractor;
import com.app.musicplayer.feature.player.LyricsManager;
import com.app.musicplayer.feature.player.PlayerViewModel;
import com.app.musicplayer.feature.player.PlayerViewModel_HiltModules;
import com.app.musicplayer.feature.search.SearchViewModel;
import com.app.musicplayer.feature.search.SearchViewModel_HiltModules;
import com.app.musicplayer.feature.settings.SettingsViewModel;
import com.app.musicplayer.feature.settings.SettingsViewModel_HiltModules;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerMusicPlayerApplication_HiltComponents_SingletonC {
  private DaggerMusicPlayerApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public MusicPlayerApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements MusicPlayerApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public MusicPlayerApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements MusicPlayerApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public MusicPlayerApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements MusicPlayerApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public MusicPlayerApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements MusicPlayerApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MusicPlayerApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements MusicPlayerApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MusicPlayerApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements MusicPlayerApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public MusicPlayerApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements MusicPlayerApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public MusicPlayerApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends MusicPlayerApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends MusicPlayerApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends MusicPlayerApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends MusicPlayerApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(6).put(LazyClassKeyProvider.com_app_musicplayer_feature_backup_BackupViewModel, BackupViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_app_musicplayer_feature_equalizer_EqualizerViewModel, EqualizerViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_app_musicplayer_feature_library_LibraryViewModel, LibraryViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_app_musicplayer_feature_player_PlayerViewModel, PlayerViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_app_musicplayer_feature_search_SearchViewModel, SearchViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_app_musicplayer_feature_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectAppPreferences(instance, singletonCImpl.provideAppPreferencesProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_app_musicplayer_feature_settings_SettingsViewModel = "com.app.musicplayer.feature.settings.SettingsViewModel";

      static String com_app_musicplayer_feature_search_SearchViewModel = "com.app.musicplayer.feature.search.SearchViewModel";

      static String com_app_musicplayer_feature_backup_BackupViewModel = "com.app.musicplayer.feature.backup.BackupViewModel";

      static String com_app_musicplayer_feature_player_PlayerViewModel = "com.app.musicplayer.feature.player.PlayerViewModel";

      static String com_app_musicplayer_feature_equalizer_EqualizerViewModel = "com.app.musicplayer.feature.equalizer.EqualizerViewModel";

      static String com_app_musicplayer_feature_library_LibraryViewModel = "com.app.musicplayer.feature.library.LibraryViewModel";

      @KeepFieldType
      SettingsViewModel com_app_musicplayer_feature_settings_SettingsViewModel2;

      @KeepFieldType
      SearchViewModel com_app_musicplayer_feature_search_SearchViewModel2;

      @KeepFieldType
      BackupViewModel com_app_musicplayer_feature_backup_BackupViewModel2;

      @KeepFieldType
      PlayerViewModel com_app_musicplayer_feature_player_PlayerViewModel2;

      @KeepFieldType
      EqualizerViewModel com_app_musicplayer_feature_equalizer_EqualizerViewModel2;

      @KeepFieldType
      LibraryViewModel com_app_musicplayer_feature_library_LibraryViewModel2;
    }
  }

  private static final class ViewModelCImpl extends MusicPlayerApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<BackupViewModel> backupViewModelProvider;

    private Provider<EqualizerViewModel> equalizerViewModelProvider;

    private Provider<LibraryViewModel> libraryViewModelProvider;

    private Provider<PlayerViewModel> playerViewModelProvider;

    private Provider<SearchViewModel> searchViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.backupViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.equalizerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.libraryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.playerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.searchViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(6).put(LazyClassKeyProvider.com_app_musicplayer_feature_backup_BackupViewModel, ((Provider) backupViewModelProvider)).put(LazyClassKeyProvider.com_app_musicplayer_feature_equalizer_EqualizerViewModel, ((Provider) equalizerViewModelProvider)).put(LazyClassKeyProvider.com_app_musicplayer_feature_library_LibraryViewModel, ((Provider) libraryViewModelProvider)).put(LazyClassKeyProvider.com_app_musicplayer_feature_player_PlayerViewModel, ((Provider) playerViewModelProvider)).put(LazyClassKeyProvider.com_app_musicplayer_feature_search_SearchViewModel, ((Provider) searchViewModelProvider)).put(LazyClassKeyProvider.com_app_musicplayer_feature_settings_SettingsViewModel, ((Provider) settingsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_app_musicplayer_feature_library_LibraryViewModel = "com.app.musicplayer.feature.library.LibraryViewModel";

      static String com_app_musicplayer_feature_equalizer_EqualizerViewModel = "com.app.musicplayer.feature.equalizer.EqualizerViewModel";

      static String com_app_musicplayer_feature_search_SearchViewModel = "com.app.musicplayer.feature.search.SearchViewModel";

      static String com_app_musicplayer_feature_settings_SettingsViewModel = "com.app.musicplayer.feature.settings.SettingsViewModel";

      static String com_app_musicplayer_feature_backup_BackupViewModel = "com.app.musicplayer.feature.backup.BackupViewModel";

      static String com_app_musicplayer_feature_player_PlayerViewModel = "com.app.musicplayer.feature.player.PlayerViewModel";

      @KeepFieldType
      LibraryViewModel com_app_musicplayer_feature_library_LibraryViewModel2;

      @KeepFieldType
      EqualizerViewModel com_app_musicplayer_feature_equalizer_EqualizerViewModel2;

      @KeepFieldType
      SearchViewModel com_app_musicplayer_feature_search_SearchViewModel2;

      @KeepFieldType
      SettingsViewModel com_app_musicplayer_feature_settings_SettingsViewModel2;

      @KeepFieldType
      BackupViewModel com_app_musicplayer_feature_backup_BackupViewModel2;

      @KeepFieldType
      PlayerViewModel com_app_musicplayer_feature_player_PlayerViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.app.musicplayer.feature.backup.BackupViewModel 
          return (T) new BackupViewModel(singletonCImpl.trackDao(), singletonCImpl.playlistDao(), singletonCImpl.eqPresetDao());

          case 1: // com.app.musicplayer.feature.equalizer.EqualizerViewModel 
          return (T) new EqualizerViewModel(singletonCImpl.audioEqualizerProvider.get(), singletonCImpl.eqPresetDao());

          case 2: // com.app.musicplayer.feature.library.LibraryViewModel 
          return (T) new LibraryViewModel(singletonCImpl.trackDao(), singletonCImpl.playlistDao(), singletonCImpl.mediaScannerProvider.get(), singletonCImpl.provideAppPreferencesProvider.get());

          case 3: // com.app.musicplayer.feature.player.PlayerViewModel 
          return (T) new PlayerViewModel(singletonCImpl.playerControllerProvider.get(), singletonCImpl.playQueueManagerProvider.get(), singletonCImpl.trackDao(), singletonCImpl.playHistoryDao(), singletonCImpl.replayGainProcessorProvider.get(), singletonCImpl.lyricsManagerProvider.get(), singletonCImpl.sleepTimerProvider.get());

          case 4: // com.app.musicplayer.feature.search.SearchViewModel 
          return (T) new SearchViewModel(singletonCImpl.trackDao());

          case 5: // com.app.musicplayer.feature.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.provideAppPreferencesProvider.get(), singletonCImpl.mediaScannerProvider.get(), singletonCImpl.languageManagerProvider.get(), singletonCImpl.sleepTimerProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends MusicPlayerApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends MusicPlayerApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectMusicPlaybackService(MusicPlaybackService musicPlaybackService) {
      injectMusicPlaybackService2(musicPlaybackService);
    }

    private MusicPlaybackService injectMusicPlaybackService2(MusicPlaybackService instance) {
      MusicPlaybackService_MembersInjector.injectPreferences(instance, singletonCImpl.provideAppPreferencesProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends MusicPlayerApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<AppPreferences> provideAppPreferencesProvider;

    private Provider<MusicDatabase> provideDatabaseProvider;

    private Provider<AudioEqualizer> audioEqualizerProvider;

    private Provider<MetadataExtractor> metadataExtractorProvider;

    private Provider<MediaScanner> mediaScannerProvider;

    private Provider<PlayQueueManager> playQueueManagerProvider;

    private Provider<PlayerController> playerControllerProvider;

    private Provider<ReplayGainProcessor> replayGainProcessorProvider;

    private Provider<LyricsManager> lyricsManagerProvider;

    private Provider<SleepTimer> sleepTimerProvider;

    private Provider<LanguageManager> languageManagerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private TrackDao trackDao() {
      return DatabaseModule_ProvideTrackDaoFactory.provideTrackDao(provideDatabaseProvider.get());
    }

    private PlaylistDao playlistDao() {
      return DatabaseModule_ProvidePlaylistDaoFactory.providePlaylistDao(provideDatabaseProvider.get());
    }

    private EqPresetDao eqPresetDao() {
      return DatabaseModule_ProvideEqPresetDaoFactory.provideEqPresetDao(provideDatabaseProvider.get());
    }

    private PlayHistoryDao playHistoryDao() {
      return DatabaseModule_ProvidePlayHistoryDaoFactory.providePlayHistoryDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideAppPreferencesProvider = DoubleCheck.provider(new SwitchingProvider<AppPreferences>(singletonCImpl, 0));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<MusicDatabase>(singletonCImpl, 1));
      this.audioEqualizerProvider = DoubleCheck.provider(new SwitchingProvider<AudioEqualizer>(singletonCImpl, 2));
      this.metadataExtractorProvider = DoubleCheck.provider(new SwitchingProvider<MetadataExtractor>(singletonCImpl, 4));
      this.mediaScannerProvider = DoubleCheck.provider(new SwitchingProvider<MediaScanner>(singletonCImpl, 3));
      this.playQueueManagerProvider = DoubleCheck.provider(new SwitchingProvider<PlayQueueManager>(singletonCImpl, 6));
      this.playerControllerProvider = DoubleCheck.provider(new SwitchingProvider<PlayerController>(singletonCImpl, 5));
      this.replayGainProcessorProvider = DoubleCheck.provider(new SwitchingProvider<ReplayGainProcessor>(singletonCImpl, 7));
      this.lyricsManagerProvider = DoubleCheck.provider(new SwitchingProvider<LyricsManager>(singletonCImpl, 8));
      this.sleepTimerProvider = DoubleCheck.provider(new SwitchingProvider<SleepTimer>(singletonCImpl, 9));
      this.languageManagerProvider = DoubleCheck.provider(new SwitchingProvider<LanguageManager>(singletonCImpl, 10));
    }

    @Override
    public void injectMusicPlayerApplication(MusicPlayerApplication musicPlayerApplication) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.app.musicplayer.core.datastore.AppPreferences 
          return (T) AppModule_ProvideAppPreferencesFactory.provideAppPreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.app.musicplayer.core.database.MusicDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.app.musicplayer.core.media.AudioEqualizer 
          return (T) new AudioEqualizer();

          case 3: // com.app.musicplayer.feature.library.scanner.MediaScanner 
          return (T) new MediaScanner(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.trackDao(), singletonCImpl.provideAppPreferencesProvider.get(), singletonCImpl.metadataExtractorProvider.get());

          case 4: // com.app.musicplayer.feature.library.scanner.MetadataExtractor 
          return (T) new MetadataExtractor();

          case 5: // com.app.musicplayer.core.media.PlayerController 
          return (T) new PlayerController(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.playQueueManagerProvider.get());

          case 6: // com.app.musicplayer.core.media.PlayQueueManager 
          return (T) new PlayQueueManager();

          case 7: // com.app.musicplayer.core.media.ReplayGainProcessor 
          return (T) new ReplayGainProcessor(singletonCImpl.provideAppPreferencesProvider.get());

          case 8: // com.app.musicplayer.feature.player.LyricsManager 
          return (T) new LyricsManager();

          case 9: // com.app.musicplayer.core.media.SleepTimer 
          return (T) new SleepTimer();

          case 10: // com.app.musicplayer.core.datastore.LanguageManager 
          return (T) new LanguageManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
