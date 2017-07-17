package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.model.user.UserScore;
import com.worldreader.core.domain.repository.UserScoreRepository;
import java.util.List;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class GetBookPagesUserScoreInteractor {

  private final ListeningExecutorService executorService;
  private final UserScoreRepository userScoreRepository;
  private final GetUserInteractor getUserInteractor;
  private final InteractorHandler interactorHandler;

  @Inject public GetBookPagesUserScoreInteractor(final ListeningExecutorService executorService, final UserScoreRepository userScoreRepository,
      final GetUserInteractor getUserInteractor, final InteractorHandler interactorHandler) {
    this.executorService = executorService;
    this.userScoreRepository = userScoreRepository;
    this.getUserInteractor = getUserInteractor;
    this.interactorHandler = interactorHandler;
  }

  public ListenableFuture<List<UserScore>> execute() {
    return execute(executorService);
  }

  public ListenableFuture<List<UserScore>> execute(final Executor executor) {
    final SettableFuture<List<UserScore>> future = SettableFuture.create();
    executor.execute(getInteractorCallable(executor, future));
    return future;
  }

  @NonNull private SafeRunnable getInteractorCallable(final Executor executor, final SettableFuture<List<UserScore>> future) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final UserStorageSpecification spec =
            UserStorageSpecification.target(UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS);

        final ListenableFuture<User2> getUserFuture = getUserInteractor.execute(spec, executor);

        interactorHandler.addCallback(getUserFuture, new FutureCallback<User2>() {
          @Override public void onSuccess(final User2 result) {
            final String userId = result.getId();

            userScoreRepository.getBookPagesUserScore(userId, new Callback<List<UserScore>>() {
              @Override public void onSuccess(final List<UserScore> userScores) {
                future.set(userScores);
              }

              @Override public void onError(final Throwable e) {
                future.setException(e);
              }
            });
          }

          @Override public void onFailure(final Throwable t) {
            future.setException(t);
          }
        }, executor);
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    };
  }
}