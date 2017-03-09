package com.worldreader.core.domain.interactors.user.milestones;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.spec.milestones.GetUserMilestoneStorageSpec;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.model.user.UserMilestone;
import com.worldreader.core.domain.repository.UserMilestonesRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class GetUserMilestoneInteractor {

  private final ListeningExecutorService executor;
  private final UserMilestonesRepository repository;

  @Inject public GetUserMilestoneInteractor(final ListeningExecutorService executor,
      final UserMilestonesRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<Optional<UserMilestone>> execute(final String milestoneId) {
    final SettableFuture<Optional<UserMilestone>> settableFuture = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {
        final GetUserMilestoneStorageSpec spec = new GetUserMilestoneStorageSpec(
            UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS, milestoneId);
        repository.get(spec, new Callback<Optional<UserMilestone>>() {
          @Override public void onSuccess(Optional<UserMilestone> response) {
            settableFuture.set(response);
          }

          @Override public void onError(Throwable e) {
            settableFuture.setException(e);
          }
        });
      }
    });

    return settableFuture;
  }

}
