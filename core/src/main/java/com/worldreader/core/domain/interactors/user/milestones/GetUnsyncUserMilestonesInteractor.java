package com.worldreader.core.domain.interactors.user.milestones;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.spec.milestones.GetAllUnsynchronizedUserMilestonesStorageSpec;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.model.user.UserMilestone;
import com.worldreader.core.domain.repository.UserMilestonesRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.*;

@Singleton public class GetUnsyncUserMilestonesInteractor {

  private final ListeningExecutorService executor;
  private final UserMilestonesRepository repository;

  @Inject public GetUnsyncUserMilestonesInteractor(final ListeningExecutorService executor,
      final UserMilestonesRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<List<UserMilestone>> execute(final Executor executor) {
    final SettableFuture<List<UserMilestone>> future = SettableFuture.create();
    executor.execute(new Runnable() {
      @Override public void run() {
        final GetAllUnsynchronizedUserMilestonesStorageSpec spec =
            new GetAllUnsynchronizedUserMilestonesStorageSpec(
                UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS);
        repository.getAll(spec, new Callback<Optional<List<UserMilestone>>>() {
          @Override public void onSuccess(final Optional<List<UserMilestone>> optional) {
            final List<UserMilestone> userMilestones = optional.get();
            future.set(Collections.unmodifiableList(userMilestones));
          }

          @Override public void onError(final Throwable e) {
            future.setException(e);
          }
        });
      }
    });
    return future;
  }

  public ListenableFuture<List<UserMilestone>> execute() {
    return execute(executor);
  }

}
