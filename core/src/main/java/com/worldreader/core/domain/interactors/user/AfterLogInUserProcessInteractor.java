package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.spec.milestones.PutUserMilestonesStorageSpec;
import com.worldreader.core.datasource.spec.userbooks.GetAllUserBooksNetworkSpec;
import com.worldreader.core.datasource.spec.userbooks.PutAllUserBooksStorageSpec;
import com.worldreader.core.domain.interactors.user.milestones.CreateUserMilestonesInteractor;
import com.worldreader.core.domain.interactors.user.milestones.PutAllUserMilestonesInteractor;
import com.worldreader.core.domain.interactors.user.score.UserScoreSynchronizationProcessInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.GetAllUserBookInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.PutAllUserBooksInteractor;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.model.user.UserMilestone;

import javax.inject.Inject;
import java.util.*;

@PerActivity public class AfterLogInUserProcessInteractor {

  private final ListeningExecutorService executor;

  private final GetAllUserBookInteractor getUserBooksInteractor;
  private final PutAllUserBooksInteractor putAllUserBooksInteractor;

  private final CreateUserMilestonesInteractor createUserMilestonesInteractor;
  private final PutAllUserMilestonesInteractor putAllUserMilestonesInteractor;
  private final UserScoreSynchronizationProcessInteractor userScoreSynchronizationProcessInteractor;

  @Inject public AfterLogInUserProcessInteractor(final ListeningExecutorService executor,
      final GetAllUserBookInteractor getUserBooksInteractor,
      final PutAllUserBooksInteractor putAllUserBooksInteractor,
      final CreateUserMilestonesInteractor createUserMilestonesInteractor,
      final PutAllUserMilestonesInteractor putAllUserMilestonesInteractor,
      final UserScoreSynchronizationProcessInteractor userScoreSynchronizationProcessInteractor) {
    this.executor = executor;
    this.getUserBooksInteractor = getUserBooksInteractor;
    this.putAllUserBooksInteractor = putAllUserBooksInteractor;
    this.createUserMilestonesInteractor = createUserMilestonesInteractor;
    this.putAllUserMilestonesInteractor = putAllUserMilestonesInteractor;
    this.userScoreSynchronizationProcessInteractor = userScoreSynchronizationProcessInteractor;
  }

  public ListenableFuture<User2> execute(final User2 user) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future, user));
    return future;
  }

  private Runnable getInteractorRunnable(final SettableFuture<User2> future, final User2 user) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        // First, get user userbooks from Network
        final ListenableFuture<Optional<List<UserBook>>> getUserBooksFuture =
            getUserBooksInteractor.execute(new GetAllUserBooksNetworkSpec(),
                MoreExecutors.directExecutor());

        // Second, store network userbooks in storage
        final ListenableFuture<Optional<List<UserBook>>> saveUserBooksFuture =
            Futures.transformAsync(getUserBooksFuture,
                new AsyncFunction<Optional<List<UserBook>>, Optional<List<UserBook>>>() {
                  @Override public ListenableFuture<Optional<List<UserBook>>> apply(
                      @NonNull final Optional<List<UserBook>> optional) throws Exception {
                    if (optional.isPresent()) {
                      final PutAllUserBooksStorageSpec spec = new PutAllUserBooksStorageSpec();
                      final List<UserBook> userBooks = optional.get();
                      return putAllUserBooksInteractor.execute(spec, userBooks,
                          MoreExecutors.directExecutor());
                    } else {
                      return Futures.immediateFuture(Optional.<List<UserBook>>absent());
                    }
                  }
                }, MoreExecutors.directExecutor());

        // Third, let's create UserMilestones from current user
        final ListenableFuture<List<UserMilestone>> createUserMilestonesFuture =
            Futures.transformAsync(saveUserBooksFuture,
                new AsyncFunction<Optional<List<UserBook>>, List<UserMilestone>>() {
                  @Override public ListenableFuture<List<UserMilestone>> apply(
                      @Nullable final Optional<List<UserBook>> input) throws Exception {
                    final String id = user.getId();
                    final List<Integer> milestones = user.getMilestones();
                    return createUserMilestonesInteractor.execute(id, milestones);
                  }
                }, MoreExecutors.directExecutor());

        // Fourth, store, created UserMilestones from current user
        final ListenableFuture<Optional<List<UserMilestone>>> storeUserMilestonesFuture =
            Futures.transformAsync(createUserMilestonesFuture,
                new AsyncFunction<List<UserMilestone>, Optional<List<UserMilestone>>>() {
                  @Override public ListenableFuture<Optional<List<UserMilestone>>> apply(
                      @Nullable final List<UserMilestone> input) throws Exception {
                    final PutUserMilestonesStorageSpec spec = new PutUserMilestonesStorageSpec();
                    return putAllUserMilestonesInteractor.execute(spec, input,
                        MoreExecutors.directExecutor());
                  }
                }, MoreExecutors.directExecutor());

        final ListenableFuture<Boolean> userSynchronizationProcessFuture =
            Futures.transformAsync(storeUserMilestonesFuture,
                new AsyncFunction<Optional<List<UserMilestone>>, Boolean>() {
                  @Override public ListenableFuture<Boolean> apply(
                      @Nullable final Optional<List<UserMilestone>> input) throws Exception {
                    return userScoreSynchronizationProcessInteractor.execute(
                        MoreExecutors.directExecutor());
                  }
                }, MoreExecutors.directExecutor());

        // Six, return current user,
        Futures.addCallback(userSynchronizationProcessFuture, new FutureCallback<Boolean>() {
          @Override public void onSuccess(@Nullable final Boolean result) {
            future.set(user);
          }

          @Override public void onFailure(final Throwable t) {
            future.setException(t);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    };
  }

}
