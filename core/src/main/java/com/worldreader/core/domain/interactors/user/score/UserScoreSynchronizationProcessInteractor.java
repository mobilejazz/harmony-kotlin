package com.worldreader.core.domain.interactors.user.score;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.interactors.user.GetUserInteractor;
import com.worldreader.core.domain.interactors.user.GetUserLeaderboardInteractor;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor;
import com.worldreader.core.domain.model.LeaderboardStat;
import com.worldreader.core.domain.model.user.LeaderboardPeriod;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.repository.UserScoreRepository;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserScoreSynchronizationProcessInteractor {

  public static final String TAG = UserScoreSynchronizationProcessInteractor.class.getSimpleName();

  private final ListeningExecutorService executor;

  private final UserScoreRepository userScoreRepository;
  private final AddUserScoreInteractor addUserScoreInteractor;
  private final RemoveAllUserScoreInteractor removeAllUserScoreInteractor;

  private final GetUserInteractor getUserInteractor;
  private final IsAnonymousUserInteractor isAnonymousUserInteractor;

  private final GetUserLeaderboardInteractor getUserLeaderboardInteractor;

  private final Logger logger;

  @Inject public UserScoreSynchronizationProcessInteractor(final ListeningExecutorService executor, final UserScoreRepository userScoreRepository,
      final AddUserScoreInteractor addUserScoreInteractor, final RemoveAllUserScoreInteractor removeAllUserScoreInteractor,
      final IsAnonymousUserInteractor isAnonymousUserInteractor, final GetUserInteractor getUserInteractor,
      final GetUserLeaderboardInteractor getUserLeaderboardInteractor, final Logger logger) {
    this.executor = executor;
    this.userScoreRepository = userScoreRepository;
    this.addUserScoreInteractor = addUserScoreInteractor;
    this.removeAllUserScoreInteractor = removeAllUserScoreInteractor;
    this.isAnonymousUserInteractor = isAnonymousUserInteractor;
    this.getUserInteractor = getUserInteractor;
    this.getUserLeaderboardInteractor = getUserLeaderboardInteractor;
    this.logger = logger;
  }

  public ListenableFuture<Boolean> execute(final Executor executor) {
    final SettableFuture<Boolean> future = SettableFuture.create();

    logger.d(TAG, "Starting user score synchronization process.");

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final IsAnonymousUserInteractor.Type type = isAnonymousUserInteractor.execute(MoreExecutors.directExecutor()).get();
        logger.d(TAG, "Current user type: " + type.toString());

        if (type == IsAnonymousUserInteractor.Type.ANONYMOUS) {
          logger.d(TAG, "User is anonymous, ignoring this user score sync job!");
          future.set(true);
          return;
        }

        final UserStorageSpecification spec = UserStorageSpecification.target(UserStorageSpecification.UserTarget.LOGGED_IN);
        final ListenableFuture<User2> userLf = getUserInteractor.execute(spec, MoreExecutors.directExecutor());

        logger.d(TAG, "Getting the user logged in.");

        Futures.addCallback(userLf, new FutureCallback<User2>() {
          @Override public void onSuccess(final User2 user) {
            logger.d(TAG, "User logged in with id: " + user.getId());

            try {

              // 1. Get user score from server
              final ListenableFuture<LeaderboardStat> getLeaderboardFuture =
                  getUserLeaderboardInteractor.execute(LeaderboardPeriod.GLOBAL, MoreExecutors.directExecutor());

              // 2. Delete old user score for user
              removeAllUserScoreInteractor.execute(MoreExecutors.directExecutor());

              // 3. Store new user score
              final LeaderboardStat stat = getLeaderboardFuture.get();
              addUserScoreInteractor.execute(stat.getScore(), true, MoreExecutors.directExecutor());

              future.set(true);
            } catch (Exception e) {
              future.setException(e);
            }

            //logger.d(TAG, "Getting all the sum of the points not synchronized.");
            // Get the sum points unsynced
            //userScoreRepository.getTotalUserScoreUnsynced(user.getId(), new Callback<Integer>() {
            //  @Override public void onSuccess(final Integer value) {
            //
            //    logger.d(TAG, "User score value not synchronized: " + value);
            //
            //    // TODO: 05/07/2017 Remove/refactor to bottom
            //    if (value == 0) { // Solo pedir al servidor dame los nuevos puntos
            //
            //      logger.d(TAG, "Getting the latest user score from the server.");
            //
            //      // download latest user score from the server.
            //      final ListenableFuture<LeaderboardStat> userLeaderboardLf = getUserLeaderboardInteractor.execute(LeaderboardPeriod.GLOBAL,
            // MoreExecutors.directExecutor());
            //
            //      interactorHandler.addCallback(userLeaderboardLf, new FutureCallback<LeaderboardStat>() {
            //        @Override public void onSuccess(@Nullable final LeaderboardStat result) {
            //
            //          logger.d(TAG, "Getting the user score synched in the storage");
            //
            //          final GetUserScoreSyncedStorageSpecification getUserScoreSyncedStorageSpecification = new
            // GetUserScoreSyncedStorageSpecification(user.getId());
            //
            //          // Get the user score synced in the storage
            //          userScoreRepository.get(getUserScoreSyncedStorageSpecification, new Callback<Optional<UserScore>>() {
            //            @Override public void onSuccess(final Optional<UserScore> userScoreSyncedOptional) {
            //              UserScore userScoreToUpdate = null;
            //              if (userScoreSyncedOptional.isPresent()) {
            //                final UserScore userScoreSyched = userScoreSyncedOptional.get();
            //                userScoreToUpdate = new UserScore.Builder(userScoreSyched).setScore(result.getScore()).setUpdatedAt(new Date()).build();
            //              } else {
            //                userScoreToUpdate = new UserScore.Builder().setScore(result.getScore())
            //                    .setUserId(user.getId())
            //                    .setCreatedAt(new Date())
            //                    .setUpdatedAt(new Date())
            //                    .setSync(true)
            //                    .build();
            //              }
            //
            //              logger.d(TAG, "Updating the user score synched in the storage with value: " + userScoreToUpdate.getScore());
            //
            //              // Update the user score.
            //              userScoreRepository.put(userScoreToUpdate, UserScoreStorageSpecification.NONE, new Callback<Optional<UserScore>>() {
            //                @Override public void onSuccess(final Optional<UserScore> userScoreOptional) {
            //                  logger.d(TAG, "User score updated in the storage");
            //
            //                  future.set(userScoreOptional.isPresent());
            //                }
            //
            //                @Override public void onError(final Throwable e) {
            //                  logger.e(TAG, e.toString());
            //                  future.setException(e);
            //                }
            //              });
            //            }
            //
            //            @Override public void onError(final Throwable e) {
            //              logger.e(TAG, e.toString());
            //              future.setException(e);
            //            }
            //          });
            //
            //        }
            //
            //        @Override public void onFailure(final Throwable t) {
            //          logger.e(TAG, t.toString());
            //          future.setException(t);
            //        }
            //      }, MoreExecutors.directExecutor());
            //
            //    } else {
            //
            //      logger.d(TAG, "Sending the user score not synced to the server with value " + value);
            //
            //      // Send the score to the server
            //      final UserScore userScore = new UserScore.Builder().setScore(value).build();
            //      userScoreRepository.put(userScore, AddUserScoreNetworkSpecification.DEFAULT, new Callback<Optional<UserScore>>() {
            //        @Override public void onSuccess(final Optional<UserScore> userScoreOptional) {
            //          logger.d(TAG, "Getting the user score synched in the storage");
            //
            //          if (userScoreOptional.isPresent()) {
            //            final UserScore userScoreFromNetwork = userScoreOptional.get();
            //
            //            final GetUserScoreSyncedStorageSpecification getUserScoreSyncedStorageSpecification = new
            // GetUserScoreSyncedStorageSpecification(user.getId());
            //
            //            // Get the user score synced in the storage.
            //            userScoreRepository.get(getUserScoreSyncedStorageSpecification, new Callback<Optional<UserScore>>() {
            //              @Override public void onSuccess(final Optional<UserScore> userScoreSyncedOptional) {
            //
            //                if (userScoreSyncedOptional.isPresent()) {
            //                  final UserScore userScoreSynced = userScoreSyncedOptional.get();
            //                  final UserScore userScoreToUpdate = new UserScore.Builder(userScoreSynced).setScore(userScoreFromNetwork.getScore()
            // ).setUpdatedAt(new Date()).build();
            //
            //                  logger.d(TAG, "Updating the user score synced in the storage with value: " + userScoreToUpdate.getScore());
            //
            //                  // Update the synched user score with the latest score value from the server
            //                  userScoreRepository.put(userScoreToUpdate, UserScoreStorageSpecification.NONE, new Callback<Optional<UserScore>>() {
            //                    @Override public void onSuccess(final Optional<UserScore> userScoreOptional) {
            //
            //                      logger.d(TAG, "User score updated in the storage");
            //                      logger.d(TAG, "Removing all the user score not synched in the storage");
            //
            //                      // Remove all the unsynced user score in the storage
            //                      userScoreRepository.removeAll(DeleteUnSyncedUserScoreStorageSpecification.DEFAULT, new Callback<Void>() {
            //                        @Override public void onSuccess(final Void aVoid) {
            //                          logger.d(TAG, "Removed all the user score not synched in the storage");
            //
            //                          future.set(true);
            //                        }
            //
            //                        @Override public void onError(final Throwable e) {
            //                          logger.e(TAG, e.toString());
            //                          future.setException(e);
            //                        }
            //                      });
            //                    }
            //
            //                    @Override public void onError(final Throwable e) {
            //                      logger.e(TAG, e.toString());
            //                      future.setException(e);
            //                    }
            //                  });
            //
            //                } else {
            //                  future.setException(new UnExpectedErrorSynchronizingUserScoreException());
            //                }
            //              }
            //
            //              @Override public void onError(final Throwable e) {
            //                logger.e(TAG, e.toString());
            //                future.setException(e);
            //              }
            //            });
            //
            //          } else {
            //            future.setException(new UnExpectedErrorSynchronizingUserScoreException());
            //          }
            //        }
            //
            //        @Override public void onError(final Throwable e) {
            //          logger.e(TAG, e.toString());
            //          future.setException(e);
            //        }
            //      });
            //
            //    }
            //  }
            //
            //  @Override public void onError(final Throwable e) {
            //    future.setException(e);
            //  }
            //});

          }

          @Override public void onFailure(final Throwable t) {
            future.setException(t);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    });

    return future;
  }

  public ListenableFuture<Boolean> execute() {
    return execute(executor);
  }
}
