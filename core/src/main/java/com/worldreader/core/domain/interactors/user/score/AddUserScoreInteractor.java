package com.worldreader.core.domain.interactors.user.score;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.score.UserScoreStorageSpecification;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.interactors.user.GetUserInteractor;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.model.user.UserScore;
import java.util.Date;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class AddUserScoreInteractor {

  private final ListeningExecutorService executorService;
  private final Repository<UserScore, RepositorySpecification> userScoreRepository;
  private final GetUserInteractor getUserInteractor;
  private final InteractorHandler interactorHandler;

  @Inject public AddUserScoreInteractor(final ListeningExecutorService executorService,
      final Repository<UserScore, RepositorySpecification> userScoreRepository, final GetUserInteractor getUserInteractor,
      final InteractorHandler interactorHandler) {
    this.executorService = executorService;
    this.userScoreRepository = userScoreRepository;
    this.getUserInteractor = getUserInteractor;
    this.interactorHandler = interactorHandler;
  }

  public ListenableFuture<UserScore> execute(final int amount) {
    return execute(amount, executorService);
  }

  public ListenableFuture<UserScore> execute(final int amount, Executor executor) {
    return execute(amount, false, executor);
  }

  public ListenableFuture<UserScore> execute(final int amount, final boolean synced, Executor executor) {
    final UserStorageSpecification spec = UserStorageSpecification.target(UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS);
    return execute(amount, synced, spec, executor);
  }

  public ListenableFuture<UserScore> execute(final int amount, final boolean synced, final UserStorageSpecification spec, Executor executor) {
    final SettableFuture<UserScore> settableFuture = SettableFuture.create();
    
    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final ListenableFuture<User2> userLf = getUserInteractor.execute(spec, MoreExecutors.directExecutor());
        interactorHandler.addCallback(userLf, new FutureCallback<User2>() {
          @Override public void onSuccess(final User2 result) {
            if (result != null) {
              final String userId = result.getId();

              final UserScore userScore = new UserScore.Builder().setSync(synced)
                  .setScore(amount)
                  .setCreatedAt(new Date())
                  .setUpdatedAt(new Date())
                  .setUserId(userId)
                  .build();

              userScoreRepository.put(userScore, UserScoreStorageSpecification.NONE, new Callback<com.google.common.base.Optional<UserScore>>() {
                @Override public void onSuccess(final com.google.common.base.Optional<UserScore> userScoreOptional) {
                  if (userScoreOptional.isPresent()) {
                    settableFuture.set(userScoreOptional.get());
                  } else {
                    settableFuture.setException(new UnknownError());
                  }
                }

                @Override public void onError(final Throwable e) {
                  settableFuture.setException(e);
                }
              });
            } else {
              settableFuture.setException(new UnknownError());
            }

          }

          @Override public void onFailure(final Throwable t) {
            settableFuture.setException(t);
          }
        }, MoreExecutors.directExecutor());
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }
}
