package com.worldreader.core.domain.interactors.user;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.repository.UserScoreRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton public class GetCurrentUserScoreInteractor {

  private final ListeningExecutorService executorService;
  private final UserScoreRepository userScoreRepository;
  private final GetUserInteractor getUserInteractor;
  private final InteractorHandler interactorHandler;

  @Inject public GetCurrentUserScoreInteractor(final ListeningExecutorService executorService, final UserScoreRepository userScoreRepository,
      final GetUserInteractor getUserInteractor, final InteractorHandler interactorHandler) {
    this.executorService = executorService;
    this.userScoreRepository = userScoreRepository;
    this.getUserInteractor = getUserInteractor;
    this.interactorHandler = interactorHandler;
  }

  public ListenableFuture<Integer> execute() {
    return execute(executorService);
  }

  public ListenableFuture<Integer> execute(final Executor executor) {
    final SettableFuture<Integer> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final UserStorageSpecification spec = UserStorageSpecification.target(UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS);

        final ListenableFuture<User2> userLf = getUserInteractor.execute(spec, executor);

        interactorHandler.addCallback(userLf, new FutureCallback<User2>() {
          @Override public void onSuccess(final User2 result) {
            userScoreRepository.getTotalUserScore(result.getId(), new Callback<Integer>() {
              @Override public void onSuccess(final Integer value) {
                settableFuture.set(value);
              }

              @Override public void onError(final Throwable e) {
                settableFuture.setException(e);
              }
            });
          }

          @Override public void onFailure(final Throwable t) {
            settableFuture.setException(t);
          }
        }, executor);
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }
}
