package com.worldreader.core.domain.interactors.leaderboard;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.LeaderboardStats;
import com.worldreader.core.domain.repository.LeaderboardRepository;
import com.worldreader.core.domain.thread.MainThread;
import javax.inject.Inject;

public class GetLeaderboardInteractorImpl extends AbstractInteractor<LeaderboardStats, ErrorCore> implements GetLeaderboardInteractor {

  private final LeaderboardRepository leaderboardRepository;

  private DomainCallback<LeaderboardStats, ErrorCore> callback;
  private GetLeaderboardInteractor.LeaderboardPeriod period;
  private int page;

  @Inject public GetLeaderboardInteractorImpl(InteractorExecutor executor, MainThread mainThread, LeaderboardRepository leaderboardRepository) {
    super(executor, mainThread);
    this.leaderboardRepository = leaderboardRepository;
  }

  @Override public void execute(LeaderboardPeriod period, int offset, DomainCallback<LeaderboardStats, ErrorCore> callback) {
    this.period = period;
    this.page = offset;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    leaderboardRepository.getLeaderboardStats(convertToRepositoryPeriod(period), page, new CompletionCallback<LeaderboardStats>() {
      @Override public void onSuccess(LeaderboardStats leaderboardStats) {
        if (callback != null) {
          performSuccessCallback(callback, leaderboardStats);
        }
      }

      @Override public void onError(ErrorCore errorCore) {
        if (callback != null) {
          performErrorCallback(callback, errorCore);
        }
      }
    });
  }

  //region Private methods
  private static LeaderboardRepository.LeaderboardPeriod convertToRepositoryPeriod(GetLeaderboardInteractor.LeaderboardPeriod period) {
    switch (period) {
      case GLOBAL:
      default:
        return LeaderboardRepository.LeaderboardPeriod.GLOBAL;
      case MONTHLY:
        return LeaderboardRepository.LeaderboardPeriod.MONTHLY;
      case WEEKLY:
        return LeaderboardRepository.LeaderboardPeriod.WEEKLY;
    }
  }
  //endregion

}
