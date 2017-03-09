package com.worldreader.core.domain.interactors.user.milestones;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.model.user.UserMilestone;
import com.worldreader.core.domain.repository.UserMilestonesRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.*;

@Singleton public class PutAllUserMilestonesNetworkInteractor {

  private final ListeningExecutorService executor;
  private final UserMilestonesRepository repository;

  @Inject public PutAllUserMilestonesNetworkInteractor(final ListeningExecutorService executor,
      final UserMilestonesRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<List<UserMilestone>> execute(final List<UserMilestone> milestones) {
    final SettableFuture<List<UserMilestone>> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future, milestones));
    return future;
  }

  public ListenableFuture<List<UserMilestone>> execute(final List<UserMilestone> milestones,
      final Executor executor) {
    final SettableFuture<List<UserMilestone>> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future, milestones));
    return future;
  }

  private Runnable getInteractorRunnable(final SettableFuture<List<UserMilestone>> future,
      final List<UserMilestone> milestones) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        repository.updateMilestones(milestones, new Callback<Optional<User2>>() {
          @Override public void onSuccess(final Optional<User2> optional) {
            final List<UserMilestone> milestonesUpdated = new ArrayList<>(milestones.size());
            for (final UserMilestone milestone : milestones) {
              final UserMilestone milestoneUpdated =
                  new UserMilestone.Builder(milestone).withSync(true).build();
              milestonesUpdated.add(milestoneUpdated);
            }

            future.set(milestonesUpdated);
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
