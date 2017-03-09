package com.worldreader.core.domain.interactors.user.score;

import android.support.annotation.Nullable;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.spec.score.AddUserScoreNetworkSpecification;
import com.worldreader.core.datasource.spec.score.DeleteUnSyncedUserScoreStorageSpecification;
import com.worldreader.core.datasource.spec.score.GetUserScoreSyncedStorageSpecification;
import com.worldreader.core.datasource.spec.score.UserScoreStorageSpecification;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.interactors.user.GetUserInteractor;
import com.worldreader.core.domain.interactors.user.GetUserLeaderboardInteractor;
import com.worldreader.core.domain.model.LeaderboardStat;
import com.worldreader.core.domain.model.user.LeaderboardPeriod;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.model.user.UserScore;
import com.worldreader.core.domain.repository.UserScoreRepository;
import com.worldreader.core.error.score.UnExpectedErrorSynchronizingUserScoreException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.*;

@Singleton public class UserScoreSynchronizationProcessInteractor {

  public static final String TAG = "UserScoreSynchronizationProcessInteractor";

  private final ListeningExecutorService executorService;
  private final UserScoreRepository userScoreRepository;
  private final GetUserInteractor getUserInteractor;
  private final InteractorHandler interactorHandler;
  private final GetUserLeaderboardInteractor getUserLeaderboardInteractor;
  private final Logger logger;

  @Inject
  public UserScoreSynchronizationProcessInteractor(final ListeningExecutorService executorService,
      final UserScoreRepository userScoreRepository, final GetUserInteractor getUserInteractor,
      final InteractorHandler interactorHandler,
      final GetUserLeaderboardInteractor getUserLeaderboardInteractor, final Logger logger) {
    this.executorService = executorService;
    this.userScoreRepository = userScoreRepository;
    this.getUserInteractor = getUserInteractor;
    this.interactorHandler = interactorHandler;
    this.getUserLeaderboardInteractor = getUserLeaderboardInteractor;
    this.logger = logger;
  }

  public ListenableFuture<Boolean> execute(final Executor executor) {
    final SettableFuture<Boolean> settableFuture = SettableFuture.create();

    logger.d(TAG, "Starting user score synchronization process.");

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final UserStorageSpecification spec = UserStorageSpecification.target(
            UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS);
        final ListenableFuture<User2> userLf =
            getUserInteractor.execute(spec, MoreExecutors.directExecutor());

        logger.d(TAG, "Getting the user logged in.");

        interactorHandler.addCallback(userLf, new FutureCallback<User2>() {
          @Override public void onSuccess(final User2 user) {

            logger.d(TAG, "User logged in with id: " + user.getId());

            logger.d(TAG, "Getting all the sum of the points not synchronized.");
            // Get the sum points unsynced
            userScoreRepository.getTotalUserScoreUnSynched(user.getId(), new Callback<Integer>() {
              @Override public void onSuccess(final Integer value) {

                logger.d(TAG, "User score value not synchronized: " + value);

                if (value == 0) {

                  logger.d(TAG, "Getting the latest user score from the server.");

                  // download latest user score from the server.
                  final ListenableFuture<LeaderboardStat> userLeaderboardLf =
                      getUserLeaderboardInteractor.execute(LeaderboardPeriod.GLOBAL,
                          MoreExecutors.directExecutor());

                  interactorHandler.addCallback(userLeaderboardLf,
                      new FutureCallback<LeaderboardStat>() {
                        @Override public void onSuccess(@Nullable final LeaderboardStat result) {

                          logger.d(TAG, "Getting the user score synched in the storage");

                          final GetUserScoreSyncedStorageSpecification
                              getUserScoreSyncedStorageSpecification =
                              new GetUserScoreSyncedStorageSpecification(user.getId());
                          // Get the user score synced in the storage
                          userScoreRepository.get(getUserScoreSyncedStorageSpecification,
                              new Callback<Optional<UserScore>>() {
                                @Override public void onSuccess(
                                    final Optional<UserScore> userScoreSyncedOptional) {
                                  UserScore userScoreToUpdate = null;
                                  if (userScoreSyncedOptional.isPresent()) {
                                    final UserScore userScoreSyched = userScoreSyncedOptional.get();
                                    userScoreToUpdate =
                                        new UserScore.Builder(userScoreSyched).setScore(
                                            result.getScore()).setUpdatedAt(new Date()).build();
                                  } else {
                                    userScoreToUpdate =
                                        new UserScore.Builder().setScore(result.getScore())
                                            .setUserId(user.getId())
                                            .setCreatedAt(new Date())
                                            .setUpdatedAt(new Date())
                                            .setSync(true)
                                            .build();
                                  }

                                  logger.d(TAG,
                                      "Updating the user score synched in the storage with value: "
                                          + userScoreToUpdate.getScore());

                                  // Update the user score.
                                  userScoreRepository.put(userScoreToUpdate,
                                      UserScoreStorageSpecification.NONE,
                                      new Callback<Optional<UserScore>>() {
                                        @Override public void onSuccess(
                                            final Optional<UserScore> userScoreOptional) {
                                          logger.d(TAG, "User score updated in the storage");

                                          settableFuture.set(userScoreOptional.isPresent());
                                        }

                                        @Override public void onError(final Throwable e) {
                                          logger.e(TAG, e.toString());
                                          settableFuture.setException(e);
                                        }
                                      });
                                }

                                @Override public void onError(final Throwable e) {
                                  logger.e(TAG, e.toString());
                                  settableFuture.setException(e);
                                }
                              });

                        }

                        @Override public void onFailure(final Throwable t) {
                          logger.e(TAG, t.toString());
                          settableFuture.setException(t);
                        }
                      }, MoreExecutors.directExecutor());

                } else {

                  logger.d(TAG,
                      "Sending the user score not synched to the server with value " + value);

                  // Send the score to the server
                  final UserScore userScore = new UserScore.Builder().setScore(value).build();
                  userScoreRepository.put(userScore, AddUserScoreNetworkSpecification.DEFAULT,
                      new Callback<Optional<UserScore>>() {
                        @Override
                        public void onSuccess(final Optional<UserScore> userScoreOptional) {
                          logger.d(TAG, "Getting the user score synched in the storage");

                          if (userScoreOptional.isPresent()) {
                            final UserScore userScoreFromNetwork = userScoreOptional.get();

                            final GetUserScoreSyncedStorageSpecification
                                getUserScoreSyncedStorageSpecification =
                                new GetUserScoreSyncedStorageSpecification(user.getId());
                            // Get the user score synced in the storage.
                            userScoreRepository.get(getUserScoreSyncedStorageSpecification,
                                new Callback<Optional<UserScore>>() {
                                  @Override public void onSuccess(
                                      final Optional<UserScore> userScoreSyncedOptional) {

                                    if (userScoreSyncedOptional.isPresent()) {
                                      final UserScore userScoreSyched =
                                          userScoreSyncedOptional.get();
                                      final UserScore userScoreToUpdate =
                                          new UserScore.Builder(userScoreSyched).setScore(
                                              userScoreFromNetwork.getScore())
                                              .setUpdatedAt(new Date())
                                              .build();

                                      logger.d(TAG,
                                          "Updating the user score synched in the storage with value: "
                                              + userScoreToUpdate.getScore());

                                      // Update the synched user score with the latest score value from the server
                                      userScoreRepository.put(userScoreToUpdate,
                                          UserScoreStorageSpecification.NONE,
                                          new Callback<Optional<UserScore>>() {
                                            @Override public void onSuccess(
                                                final Optional<UserScore> userScoreOptional) {

                                              logger.d(TAG, "User score updated in the storage");
                                              logger.d(TAG,
                                                  "Removing all the user score not synched in the storage");

                                              // Remove all the unsynced user score in the storage
                                              userScoreRepository.removeAll(
                                                  DeleteUnSyncedUserScoreStorageSpecification.DEFAULT,
                                                  new Callback<Void>() {
                                                    @Override
                                                    public void onSuccess(final Void aVoid) {
                                                      logger.d(TAG,
                                                          "Removed all the user score not synched in the storage");

                                                      settableFuture.set(true);
                                                    }

                                                    @Override
                                                    public void onError(final Throwable e) {
                                                      logger.e(TAG, e.toString());
                                                      settableFuture.setException(e);
                                                    }
                                                  });
                                            }

                                            @Override public void onError(final Throwable e) {
                                              logger.e(TAG, e.toString());
                                              settableFuture.setException(e);
                                            }
                                          });

                                    } else {
                                      settableFuture.setException(
                                          new UnExpectedErrorSynchronizingUserScoreException());
                                    }
                                  }

                                  @Override public void onError(final Throwable e) {
                                    logger.e(TAG, e.toString());
                                    settableFuture.setException(e);
                                  }
                                });

                          } else {
                            settableFuture.setException(
                                new UnExpectedErrorSynchronizingUserScoreException());
                          }
                        }

                        @Override public void onError(final Throwable e) {
                          logger.e(TAG, e.toString());
                          settableFuture.setException(e);
                        }
                      });

                }
              }

              @Override public void onError(final Throwable e) {
                settableFuture.setException(e);
              }
            });

          }

          @Override public void onFailure(final Throwable t) {
            settableFuture.setException(t);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }

  public ListenableFuture<Boolean> execute() {
    return execute(executorService);
  }
}
