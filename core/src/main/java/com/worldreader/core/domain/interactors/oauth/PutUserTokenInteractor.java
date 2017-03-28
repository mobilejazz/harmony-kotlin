package com.worldreader.core.domain.interactors.oauth;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.repository.OAuthRepository;

import javax.inject.Inject;
import java.util.concurrent.*;

@PerActivity public class PutUserTokenInteractor {

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
