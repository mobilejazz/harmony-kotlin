package com.worldreader.core.domain.interactors.user.score;

import android.support.annotation.NonNull;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.interactors.user.GetBookPagesUserScoreInteractor;
import com.worldreader.core.domain.interactors.user.GetUserInteractor;
import com.worldreader.core.domain.interactors.user.GetUserLeaderboardInteractor;
import com.worldreader.core.domain.interactors.user.SendReadPagesInteractor;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor;
import com.worldreader.core.domain.model.LeaderboardStat;
import com.worldreader.core.domain.model.user.LeaderboardPeriod;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.model.user.UserScore;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.*;

@Singleton public class UserScoreSynchronizationProcessInteractor {

  public static final String TAG = UserScoreSynchronizationProcessInteractor.class.getSimpleName();

  private final ListeningExecutorService executor;

  private final AddUserScoreInteractor addUserScoreInteractor;
  private final RemoveAllUserScoreInteractor removeAllUserScoreInteractor;
  private final IsAnonymousUserInteractor isAnonymousUserInteractor;
  private final GetUserInteractor getUserInteractor;
  private final GetBookPagesUserScoreInteractor getBookPagesUserScoreInteractor;
  private final GetUserLeaderboardInteractor getUserLeaderboardInteractor;
  private final SendReadPagesInteractor sendPageReadsInteractor;

  private final Logger logger;

  @Inject
  public UserScoreSynchronizationProcessInteractor(final ListeningExecutorService executor, final AddUserScoreInteractor addUserScoreInteractor,
      final RemoveAllUserScoreInteractor removeAllUserScoreInteractor, final IsAnonymousUserInteractor isAnonymousUserInteractor,
      final GetUserInteractor getUserInteractor, final GetBookPagesUserScoreInteractor getBookPagesUserScoreInteractor,
      final GetUserLeaderboardInteractor getUserLeaderboardInteractor, final SendReadPagesInteractor sendPageReadsInteractor, final Logger logger) {
    this.executor = executor;
    this.addUserScoreInteractor = addUserScoreInteractor;
    this.removeAllUserScoreInteractor = removeAllUserScoreInteractor;
    this.isAnonymousUserInteractor = isAnonymousUserInteractor;
    this.getUserInteractor = getUserInteractor;
    this.getBookPagesUserScoreInteractor = getBookPagesUserScoreInteractor;
    this.getUserLeaderboardInteractor = getUserLeaderboardInteractor;
    this.sendPageReadsInteractor = sendPageReadsInteractor;
    this.logger = logger;
  }

  public ListenableFuture<Boolean> execute() {
    return execute(executor);
  }

  public ListenableFuture<Boolean> execute(final Executor executor) {
    final SettableFuture<Boolean> future = SettableFuture.create();
    executor.execute(getInteractorCallable(future));
    return future;
  }

  @NonNull private SafeRunnable getInteractorCallable(final SettableFuture<Boolean> future) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        logger.d(TAG, "Starting user score synchronization process.");

        final IsAnonymousUserInteractor.Type type = isAnonymousUserInteractor.execute(MoreExecutors.directExecutor()).get();
        logger.d(TAG, "Current user type: " + type.toString());

        if (type == IsAnonymousUserInteractor.Type.ANONYMOUS) {
          logger.d(TAG, "User is anonymous, ignoring this user score synchronization job!");
          future.set(true);
          return;
        }

        logger.d(TAG, "Retrieving logged in user!");

        final UserStorageSpecification spec = UserStorageSpecification.target(UserStorageSpecification.UserTarget.LOGGED_IN);
        final User2 user = getUserInteractor.execute(spec, MoreExecutors.directExecutor()).get();

        logger.d(TAG, "User logged in with id: " + user.getId());

        // 1. Get user score for bookId != null and not in sync
        logger.d(TAG, "Obtaining UserScore with bookId != null");
        final List<UserScore> pagesUserScores = getBookPagesUserScoreInteractor.execute(MoreExecutors.directExecutor()).get();
        if (!pagesUserScores.isEmpty()) {
          logger.d(TAG, "Sending UserScore related to pages read");
          for (final UserScore userScore : pagesUserScores) {
            final String bookId = userScore.getBookId();
            final int pagesRead = userScore.getPages();
            final Date updatedAt = userScore.getUpdatedAt();
            sendPageReadsInteractor.execute(bookId, pagesRead, updatedAt, MoreExecutors.directExecutor()).get();
          }
        }

        // 2. Get user score from server
        logger.d(TAG, "Obtaining user score from server");
        final LeaderboardStat stat = getUserLeaderboardInteractor.execute(LeaderboardPeriod.GLOBAL, MoreExecutors.directExecutor()).get();
        logger.d(TAG, "Server user score: " + stat.getScore());

        // 3. Delete old user score for user
        logger.d(TAG, "Deleting old user score for user");
        removeAllUserScoreInteractor.execute(MoreExecutors.directExecutor()).get();

        // 4. Store new user score
        logger.d(TAG, "Storing new user score for user");
        addUserScoreInteractor.execute(stat.getScore(), true, MoreExecutors.directExecutor()).get();

        // 4. Return successfully
        logger.d(TAG, "Process done");
        future.set(true);
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    };
  }
}
