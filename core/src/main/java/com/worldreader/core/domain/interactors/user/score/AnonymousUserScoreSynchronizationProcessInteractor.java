package com.worldreader.core.domain.interactors.user.score;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.spec.score.AddUserScoreNetworkSpecification;
import com.worldreader.core.datasource.spec.score.UserScoreStorageSpecification;
import com.worldreader.core.domain.model.user.UserScore;
import com.worldreader.core.domain.repository.UserScoreRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.concurrent.Executor;

@Singleton public class AnonymousUserScoreSynchronizationProcessInteractor {

  private final ListeningExecutorService executorService;
  private final UserScoreRepository userScoreRepository;

  @Inject public AnonymousUserScoreSynchronizationProcessInteractor(
      final ListeningExecutorService executorService,
      final UserScoreRepository userScoreRepository) {
    this.executorService = executorService;
    this.userScoreRepository = userScoreRepository;
  }

  public ListenableFuture<Boolean> execute(final String anonymousUserId, final String userId,
      final Executor executor) {
    final SettableFuture<Boolean> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        // Get the sum of points from the anonymous user
        userScoreRepository.getTotalUserScoreUnsynced(anonymousUserId, new Callback<Integer>() {
          @Override public void onSuccess(final Integer value) {

            final UserScore userScore = new UserScore.Builder().setScore(value).build();
            userScoreRepository.put(userScore, AddUserScoreNetworkSpecification.DEFAULT,
                new Callback<Optional<UserScore>>() {
                  @Override public void onSuccess(final Optional<UserScore> userScoreOptional) {
                    UserScore userScoreToUpdate = new UserScore.Builder().setScore(value)
                        .setUserId(userId)
                        .setCreatedAt(new Date())
                        .setUpdatedAt(new Date())
                        .setSync(true)
                        .build();

                    // Update the user score.
                    userScoreRepository.put(userScoreToUpdate, UserScoreStorageSpecification.NONE,
                        new Callback<Optional<UserScore>>() {
                          @Override
                          public void onSuccess(final Optional<UserScore> userScoreOptional) {
                            settableFuture.set(userScoreOptional.isPresent());
                          }

                          @Override public void onError(final Throwable e) {
                            settableFuture.setException(e);
                          }
                        });
                  }

                  @Override public void onError(final Throwable e) {
                    settableFuture.setException(e);
                  }
                });
          }

          @Override public void onError(final Throwable e) {
            settableFuture.setException(e);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }

  public ListenableFuture<Boolean> execute(final String anonymousUserId, final String userId) {
    return execute(anonymousUserId, userId, executorService);
  }
}
