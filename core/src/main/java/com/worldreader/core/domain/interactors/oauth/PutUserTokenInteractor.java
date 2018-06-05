package com.worldreader.core.domain.interactors.oauth;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.mobilejazz.kotlin.core.di.ActivityScope;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.repository.OAuthRepository;
import java.util.concurrent.Executor;
import javax.inject.Inject;

@ActivityScope public class PutUserTokenInteractor {

  private final ListeningExecutorService executorService;
  private final OAuthRepository repository;

  @Inject public PutUserTokenInteractor(final ListeningExecutorService executorService,
      final OAuthRepository repository) {
    this.executorService = executorService;
    this.repository = repository;
  }

  public ListenableFuture<Void> execute(final String token) {
    return execute(token, executorService);
  }

  public ListenableFuture<Void> execute(final String token, final Executor executor) {
    final SettableFuture<Void> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final boolean result = repository.putUserToken(token);
        settableFuture.set(null);
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }
}
