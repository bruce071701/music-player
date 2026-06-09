package com.app.musicplayer.feature.player;

import com.app.musicplayer.core.database.dao.PlayHistoryDao;
import com.app.musicplayer.core.database.dao.TrackDao;
import com.app.musicplayer.core.media.PlayQueueManager;
import com.app.musicplayer.core.media.PlayerController;
import com.app.musicplayer.core.media.ReplayGainProcessor;
import com.app.musicplayer.core.media.SleepTimer;
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
public final class PlayerViewModel_Factory implements Factory<PlayerViewModel> {
  private final Provider<PlayerController> playerControllerProvider;

  private final Provider<PlayQueueManager> queueManagerProvider;

  private final Provider<TrackDao> trackDaoProvider;

  private final Provider<PlayHistoryDao> playHistoryDaoProvider;

  private final Provider<ReplayGainProcessor> replayGainProcessorProvider;

  private final Provider<LyricsManager> lyricsManagerProvider;

  private final Provider<SleepTimer> sleepTimerProvider;

  public PlayerViewModel_Factory(Provider<PlayerController> playerControllerProvider,
      Provider<PlayQueueManager> queueManagerProvider, Provider<TrackDao> trackDaoProvider,
      Provider<PlayHistoryDao> playHistoryDaoProvider,
      Provider<ReplayGainProcessor> replayGainProcessorProvider,
      Provider<LyricsManager> lyricsManagerProvider, Provider<SleepTimer> sleepTimerProvider) {
    this.playerControllerProvider = playerControllerProvider;
    this.queueManagerProvider = queueManagerProvider;
    this.trackDaoProvider = trackDaoProvider;
    this.playHistoryDaoProvider = playHistoryDaoProvider;
    this.replayGainProcessorProvider = replayGainProcessorProvider;
    this.lyricsManagerProvider = lyricsManagerProvider;
    this.sleepTimerProvider = sleepTimerProvider;
  }

  @Override
  public PlayerViewModel get() {
    return newInstance(playerControllerProvider.get(), queueManagerProvider.get(), trackDaoProvider.get(), playHistoryDaoProvider.get(), replayGainProcessorProvider.get(), lyricsManagerProvider.get(), sleepTimerProvider.get());
  }

  public static PlayerViewModel_Factory create(Provider<PlayerController> playerControllerProvider,
      Provider<PlayQueueManager> queueManagerProvider, Provider<TrackDao> trackDaoProvider,
      Provider<PlayHistoryDao> playHistoryDaoProvider,
      Provider<ReplayGainProcessor> replayGainProcessorProvider,
      Provider<LyricsManager> lyricsManagerProvider, Provider<SleepTimer> sleepTimerProvider) {
    return new PlayerViewModel_Factory(playerControllerProvider, queueManagerProvider, trackDaoProvider, playHistoryDaoProvider, replayGainProcessorProvider, lyricsManagerProvider, sleepTimerProvider);
  }

  public static PlayerViewModel newInstance(PlayerController playerController,
      PlayQueueManager queueManager, TrackDao trackDao, PlayHistoryDao playHistoryDao,
      ReplayGainProcessor replayGainProcessor, LyricsManager lyricsManager, SleepTimer sleepTimer) {
    return new PlayerViewModel(playerController, queueManager, trackDao, playHistoryDao, replayGainProcessor, lyricsManager, sleepTimer);
  }
}
