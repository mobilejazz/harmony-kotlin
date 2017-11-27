package com.worldreader.core.domain.interactors.user;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.domain.model.user.UserReadingStats;
import com.worldreader.core.domain.repository.UserRepository;
import com.worldreader.core.error.general.UnexpectedErrorException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class GetUserReadingStatsInteractor {

  private final ListeningExecutorService executor;
  private final UserRepository repository;

  @Inject public GetUserReadingStatsInteractor(ListeningExecutorService executor, UserRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<UserReadingStats> execute(final Date from, final Date to) {
    final SettableFuture<UserReadingStats> future = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {
        Preconditions.checkNotNull(from, "from == null");
        Preconditions.checkNotNull(to, "to == null");
        repository.readingStats(from, to, new Callback<Optional<UserReadingStats>>() {
          @Override public void onSuccess(final Optional<UserReadingStats> optional) {
            if (optional.isPresent()) {
              final UserReadingStats userReadingStats = optional.get();
              future.set(userReadingStats);
            } else {
              future.setException(new UnexpectedErrorException("UserReadingStats is not defined!"));
            }
          }

          @Override public void onError(final Throwable e) {
            future.setException(e);
          }
        });
      }
    });

    return future;
  }

}
