package com.worldreader.core.domain.interactors.user.milestones;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.spec.milestones.GetAllUserMilestoneStorageSpec;
import com.worldreader.core.domain.model.user.UserMilestone;
import com.worldreader.core.domain.repository.UserMilestonesRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.*;

@Singleton public class GetAllUserMilestonesInteractor {

  private final ListeningExecutorService executor;

  private final UserMilestonesRepository repository;

  @Inject public GetAllUserMilestonesInteractor(final ListeningExecutorService executor,
      final UserMilestonesRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<Optional<List<UserMilestone>>> execute(
      final GetAllUserMilestoneStorageSpec spec) {
    final SettableFuture<Optional<List<UserMilestone>>> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future, spec));
    return future;
  }

  public ListenableFuture<Optional<List<UserMilestone>>> execute(
      final GetAllUserMilestoneStorageSpec spec, final Executor executor) {
    final SettableFuture<Optional<List<UserMilestone>>> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future, spec));
    return future;
  }

  private Runnable getInteractorRunnable(final SettableFuture<Optional<List<UserMilestone>>> future,
      final GetAllUserMilestoneStorageSpec spec) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        repository.getAll(spec, new Callback<Optional<List<UserMilestone>>>() {
          @Override public void onSuccess(final Optional<List<UserMilestone>> optional) {
            future.set(optional);
          }

          @Override public void onError(final Throwable e) {
            future.setException(e);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    };
  }
}
