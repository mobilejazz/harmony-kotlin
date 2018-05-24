package com.worldreader.core.analytics.providers.pinpoint.interactor;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.analytics.interactors.PutAnalyticsUserIdInteractor;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.interactors.user.GetUserInteractor;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor;
import com.worldreader.core.domain.model.user.User2;
import java.util.concurrent.Executor;
import javax.inject.Inject;

public class PinpointConfigAnalyticsUserIdInteractor {

  private final IsAnonymousUserInteractor isAnonymousUserInteractor;
  private final GetUserInteractor getUserInteractor;
  private final PutAnalyticsUserIdInteractor putAnalyticsUserIdInteractor;
  private final ListeningExecutorService executorService;
  private final PinpointConfigAnalyticsAttributesInteractor configAnalyticsAttributesInteractor;

  @Inject public PinpointConfigAnalyticsUserIdInteractor(final IsAnonymousUserInteractor isAnonymousUserInteractor,
      final GetUserInteractor getUserInteractor,
      final PutAnalyticsUserIdInteractor putAnalyticsUserIdInteractor,
      final ListeningExecutorService executorService,
      final PinpointConfigAnalyticsAttributesInteractor configAnalyticsAttributesInteractor) {
    this.isAnonymousUserInteractor = isAnonymousUserInteractor;
    this.getUserInteractor = getUserInteractor;
    this.putAnalyticsUserIdInteractor = putAnalyticsUserIdInteractor;
    this.executorService = executorService;
    this.configAnalyticsAttributesInteractor = configAnalyticsAttributesInteractor;
  }

  public ListenableFuture<Void> execute(final User2 user2, final Executor executor) {
    final SettableFuture<Void> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        User2 finalUser = user2;
        if (user2 == null) {
          final IsAnonymousUserInteractor.Type type = isAnonymousUserInteractor.execute(MoreExecutors.directExecutor()).get();
          if (type == IsAnonymousUserInteractor.Type.REGISTERED) {
            finalUser = getUserInteractor.execute(MoreExecutors.directExecutor()).get();
          }
        }

        if (finalUser != null) {
          putAnalyticsUserIdInteractor.execute(finalUser.getId(), MoreExecutors.directExecutor()).get();
          configAnalyticsAttributesInteractor.execute(MoreExecutors.directExecutor()).get();
        }

        settableFuture.set(null);
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }

  public ListenableFuture<Void> execute(final Executor executor) {
    return execute(null, executor);
  }

  public ListenableFuture<Void> execute() {
    return execute(executorService);
  }
}
