package com.worldreader.core.domain.interactors.user.milestones;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.model.user.Milestone;
import com.worldreader.core.domain.model.user.UserMilestone;
import com.worldreader.core.domain.repository.UserMilestonesRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.*;

@Singleton public class CreateUserMilestonesInteractor {

  private final ListeningExecutorService executor;
  private final UserMilestonesRepository repository;

  @Inject public CreateUserMilestonesInteractor(final ListeningExecutorService executor, UserMilestonesRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<List<UserMilestone>> execute(final String userId, final List<Integer> rawMilestones) {
    final SettableFuture<List<UserMilestone>> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future, userId, rawMilestones));
    return future;
  }

  public ListenableFuture<List<UserMilestone>> execute(final String userId, final List<Integer> rawMilestones, final Executor executor) {
    final SettableFuture<List<UserMilestone>> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future, userId, rawMilestones));
    return future;
  }

  private Runnable getInteractorRunnable(final SettableFuture<List<UserMilestone>> future, final String userId, final List<Integer> rawMilestones) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        Preconditions.checkNotNull(rawMilestones, "rawMilestones == null");
        final List<UserMilestone> userMilestones = Lists.newArrayListWithCapacity(rawMilestones.size());
        repository.getAllUserMilestones(new Callback<Set<Milestone>>() {
          @Override public void onSuccess(final Set<Milestone> milestones) {
            for (final Milestone milestone : milestones) {
              final boolean synced = isSynced(milestone.getId(), rawMilestones);
              final UserMilestone userMilestone = new UserMilestone.Builder().withUserId(userId)
                  .withMilestoneId(String.valueOf(milestone.getId()))
                  .withScore(milestone.getPoints())
                  .withCreatedAt(new Date())
                  .withUpdatedAt(new Date())
                  .withState(synced ? UserMilestone.STATE_DONE : UserMilestone.STATE_PENDING)
                  .withSync(synced)
                  .build();
              userMilestones.add(userMilestone);
            }
            future.set(userMilestones);
          }

          @Override public void onError(final Throwable e) {
            future.setException(e);
          }

          private boolean isSynced(final int id, final List<Integer> rawMilestones) {
            for (final Integer rawMilestone : rawMilestones) {
              if (id == rawMilestone) {
                return true;
              }
            }
            return false;
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    };
  }

}
