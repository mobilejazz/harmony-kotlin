package com.worldreader.core.domain.interactors.user.milestones;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.spec.milestones.PutUserMilestonesStorageSpec;
import com.worldreader.core.domain.model.user.UserMilestone;
import com.worldreader.core.domain.repository.UserMilestonesRepository;
import java.util.List;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class PutAllUserMilestonesInteractor {

  private final ListeningExecutorService executor;
  private final UserMilestonesRepository repository;

  @Inject public PutAllUserMilestonesInteractor(final ListeningExecutorService executor, final UserMilestonesRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<Optional<List<UserMilestone>>> execute(final PutUserMilestonesStorageSpec spec, final List<UserMilestone> userMilestones) {
    final SettableFuture<Optional<List<UserMilestone>>> future = SettableFuture.create();
    executor.execute(getStorageInteractorRunnable(future, spec, userMilestones));
    return future;
  }

  public ListenableFuture<Optional<List<UserMilestone>>> execute(final PutUserMilestonesStorageSpec spec, final List<UserMilestone> userMilestones,
      final Executor executor) {
    final SettableFuture<Optional<List<UserMilestone>>> future = SettableFuture.create();
    executor.execute(getStorageInteractorRunnable(future, spec, userMilestones));
    return future;
  }

  private Runnable getStorageInteractorRunnable(final SettableFuture<Optional<List<UserMilestone>>> future, final PutUserMilestonesStorageSpec spec,
      final List<UserMilestone> milestones) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        repository.putAll(milestones, spec, new Callback<Optional<List<UserMilestone>>>() {
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
