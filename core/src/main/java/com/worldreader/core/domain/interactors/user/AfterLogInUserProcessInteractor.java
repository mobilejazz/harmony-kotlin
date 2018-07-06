package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.mobilejazz.kotlin.core.di.ActivityScope;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.analytics.providers.pinpoint.interactor.PinpointConfigAnalyticsUserIdInteractor;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.spec.milestones.PutUserMilestonesStorageSpec;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.datasource.spec.userbooks.GetAllUserBooksNetworkSpec;
import com.worldreader.core.datasource.spec.userbooks.PutAllUserBooksStorageSpec;
import com.worldreader.core.datasource.spec.userbookslike.GetAllUserBooksLikeNetworkSpec;
import com.worldreader.core.datasource.spec.userbookslike.PutAllUserBookLikeStorageSpec;
import com.worldreader.core.domain.interactors.user.milestones.CreateUserMilestonesInteractor;
import com.worldreader.core.domain.interactors.user.milestones.PutAllUserMilestonesInteractor;
import com.worldreader.core.domain.interactors.user.score.UserScoreSynchronizationProcessInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.GetAllUserBookInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.PutAllUserBooksInteractor;
import com.worldreader.core.domain.interactors.user.userbookslike.GetAllUserBookLikesInteractor;
import com.worldreader.core.domain.interactors.user.userbookslike.PutAllUserBooksLikesInteractor;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.model.user.UserBookLike;
import com.worldreader.core.domain.model.user.UserMilestone;
import com.worldreader.core.sync.WorldreaderJobCreator;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

@ActivityScope public class AfterLogInUserProcessInteractor {

  private static final String TAG = AfterLogInUserProcessInteractor.class.getSimpleName();

  private final ListeningExecutorService executor;

  private final GetAllUserBookInteractor getUserBooksInteractor;
  private final PutAllUserBooksInteractor putAllUserBooksInteractor;

  private final CreateUserMilestonesInteractor createUserMilestonesInteractor;
  private final PutAllUserMilestonesInteractor putAllUserMilestonesInteractor;
  private final UserScoreSynchronizationProcessInteractor userScoreSynchronizationProcessInteractor;
  private final GetAllUserBookLikesInteractor getAllUserBookLikesInteractor;
  private final PutAllUserBooksLikesInteractor putAllUserBooksLikesInteractor;
  private final PinpointConfigAnalyticsUserIdInteractor configAnalyticsUserIdInteractor;

  private final Logger logger;

  @Inject public AfterLogInUserProcessInteractor(final ListeningExecutorService executor, final GetAllUserBookInteractor getUserBooksInteractor,
      final PutAllUserBooksInteractor putAllUserBooksInteractor, final CreateUserMilestonesInteractor createUserMilestonesInteractor,
      final PutAllUserMilestonesInteractor putAllUserMilestonesInteractor,
      final UserScoreSynchronizationProcessInteractor userScoreSynchronizationProcessInteractor,
      final GetAllUserBookLikesInteractor getAllUserBookLikesInteractor, final PutAllUserBooksLikesInteractor putAllUserBooksLikesInteractor,
      final PinpointConfigAnalyticsUserIdInteractor configAnalyticsUserIdInteractor, final Logger logger) {
    this.executor = executor;
    this.getUserBooksInteractor = getUserBooksInteractor;
    this.putAllUserBooksInteractor = putAllUserBooksInteractor;
    this.createUserMilestonesInteractor = createUserMilestonesInteractor;
    this.putAllUserMilestonesInteractor = putAllUserMilestonesInteractor;
    this.userScoreSynchronizationProcessInteractor = userScoreSynchronizationProcessInteractor;
    this.getAllUserBookLikesInteractor = getAllUserBookLikesInteractor;
    this.putAllUserBooksLikesInteractor = putAllUserBooksLikesInteractor;
    this.configAnalyticsUserIdInteractor = configAnalyticsUserIdInteractor;
    this.logger = logger;
  }

  public ListenableFuture<User2> execute(final User2 user) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future, user));
    return future;
  }

  public ListenableFuture<User2> execute(final User2 user, final Executor executor) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future, user));
    return future;
  }

  private Runnable getInteractorRunnable(@NonNull final SettableFuture<User2> future, final User2 user) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        // First, get user userbooks from Network
        logger.d(TAG, "Obtaining UserBooks from network");
        final Optional<List<UserBook>> userBooksOptional =
            getUserBooksInteractor.execute(new GetAllUserBooksNetworkSpec(), MoreExecutors.directExecutor()).get();

        // Second, store network userbooks in storage
        if (userBooksOptional.isPresent()) {
          final List<UserBook> userBooks = userBooksOptional.get();
          if (!userBooks.isEmpty()) {
            logger.d(TAG, "Userbooks from network downloaded. Storing those in storage.");
            final PutAllUserBooksStorageSpec spec = new PutAllUserBooksStorageSpec();
            putAllUserBooksInteractor.execute(spec, userBooks, MoreExecutors.directExecutor()).get();
          }
        }

        // Third, let's create UserMilestones from current user
        logger.d(TAG, "Creating milestones for user");
        final String id = user.getId();
        final List<Integer> milestones = user.getMilestones();
        final List<UserMilestone> userMilestones = createUserMilestonesInteractor.execute(id, milestones, MoreExecutors.directExecutor()).get();

        // Fourth, store, created UserMilestones from current user
        logger.d(TAG, "Storing milestones in storage");
        final PutUserMilestonesStorageSpec spec = new PutUserMilestonesStorageSpec();
        putAllUserMilestonesInteractor.execute(spec, userMilestones, MoreExecutors.directExecutor()).get();

        // Fifth, store user score generate from current user
        logger.d(TAG, "Calling user score synchronization process");
        userScoreSynchronizationProcessInteractor.execute(MoreExecutors.directExecutor()).get();

        // Seventh, obtain userbooklikes from server
        logger.d(TAG, "Obtaining UserBooksLikes from server");
        final List<UserBookLike> userBookLikes =
            getAllUserBookLikesInteractor.execute(new GetAllUserBooksLikeNetworkSpec(), MoreExecutors.directExecutor()).get();

        // Eight, store those into db
        logger.d(TAG, "Storing UserBooksLikes in storage");
        final PutAllUserBookLikeStorageSpec userBookLikeStorageSpec =
            new PutAllUserBookLikeStorageSpec(UserStorageSpecification.UserTarget.LOGGED_IN);
        putAllUserBooksLikesInteractor.execute(userBookLikes, userBookLikeStorageSpec, MoreExecutors.directExecutor()).get();

        // Nine, add the user id to the analytics.
        logger.d(TAG, "Configuring user id in analytics");
        configAnalyticsUserIdInteractor.execute(user, MoreExecutors.directExecutor()).get();

        // Nine, schedule jobs
        logger.d(TAG, "Scheduling JobManager jobs");
        WorldreaderJobCreator.scheduleAllJobs();

        // Ten, finish procedure
        logger.d(TAG, "Finished AfterLoginUserProcess");
        future.set(user);
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    };
  }

}
