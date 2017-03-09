package com.worldreader.core.domain.interactors.user;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.model.LeaderboardStat;
import com.worldreader.core.domain.model.user.LeaderboardPeriod;
import com.worldreader.core.domain.repository.UserRepository;
import com.worldreader.core.error.general.UnexpectedErrorException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton public class GetUserLeaderboardInteractor {

  private final UserRepository repository;
  private final ListeningExecutorService executorService;

  @Inject public GetUserLeaderboardInteractor(UserRepository repository,
      final ListeningExecutorService executorService) {
    this.repository = repository;
    this.executorService = executorService;
  }

  public ListenableFuture<LeaderboardStat> execute(final LeaderboardPeriod leaderboardPeriod,
      final Executor executor) {
    final SettableFuture<LeaderboardStat> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        repository.leaderboardStats(leaderboardPeriod, new Callback<Optional<LeaderboardStat>>() {
          @Override public void onSuccess(Optional<LeaderboardStat> optional) {
            if (optional.isPresent()) {
              settableFuture.set(optional.get());
            } else {
              settableFuture.setException(
                  new UnexpectedErrorException("LeaderboardStat is not defined!"));
            }
          }

          @Override public void onError(Throwable e) {
            settableFuture.setException(e);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }

  public ListenableFuture<LeaderboardStat> execute(LeaderboardPeriod leaderboardPeriod) {
    return execute(leaderboardPeriod, executorService);
  }

}
