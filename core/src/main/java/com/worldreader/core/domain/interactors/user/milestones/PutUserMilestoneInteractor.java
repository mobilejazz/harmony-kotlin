package com.worldreader.core.domain.interactors.user.milestones;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.spec.milestones.GetUserMilestoneStorageSpec;
import com.worldreader.core.datasource.spec.milestones.UserMilestoneStorageSpecification;
import com.worldreader.core.domain.model.user.UserMilestone;
import com.worldreader.core.domain.repository.UserMilestonesRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.worldreader.core.datasource.spec.user.UserStorageSpecification.UserTarget;

@Singleton public class PutUserMilestoneInteractor {

  private final ListeningExecutorService executor;
  private final UserMilestonesRepository repository;

  @Inject public PutUserMilestoneInteractor(final ListeningExecutorService executor,
      final UserMilestonesRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<UserMilestone> execute(final UserMilestone milestone) {
    final SettableFuture<UserMilestone> future = SettableFuture.create();
    executor.execute(new Runnable() {
      @Override public void run() {
        final UserMilestoneStorageSpecification spec =
            new GetUserMilestoneStorageSpec(UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS,
                milestone.getMilestoneId());
        repository.put(milestone, spec, new Callback<Optional<UserMilestone>>() {
          @Override public void onSuccess(final Optional<UserMilestone> optional) {
            final UserMilestone userMilestone = optional.get();
            future.set(userMilestone);
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
