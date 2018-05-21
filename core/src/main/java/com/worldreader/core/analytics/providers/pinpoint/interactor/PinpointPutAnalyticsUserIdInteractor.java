package com.worldreader.core.analytics.providers.pinpoint.interactor;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.analytics.interactors.GetUserInfoAnalyticsInteractor;
import com.worldreader.core.analytics.models.UserInfoAnalyticsModel;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import java.util.concurrent.Executor;
import javax.inject.Inject;

public class PinpointPutAnalyticsUserIdInteractor {

  private final GetUserInfoAnalyticsInteractor getAnalyticsInfoInteractor;
  private final ListeningExecutorService listeningExecutorService;
  private final Repository<UserInfoAnalyticsModel, RepositorySpecification> repository;

  @Inject
  public PinpointPutAnalyticsUserIdInteractor(
      final GetUserInfoAnalyticsInteractor getAnalyticsInfoInteractor,
      final ListeningExecutorService listeningExecutorService,
      final Repository<UserInfoAnalyticsModel, RepositorySpecification> repository
  ) {
    this.getAnalyticsInfoInteractor = getAnalyticsInfoInteractor;
    this.listeningExecutorService = listeningExecutorService;
    this.repository = repository;
  }

  public ListenableFuture<Void> execute(final String userId, final Executor executor) {
    final SettableFuture<Void> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final UserInfoAnalyticsModel model = getAnalyticsInfoInteractor.execute(MoreExecutors.directExecutor()).get();
        model.userId = userId;

        repository.put(model, RepositorySpecification.NONE,
            new Callback<Optional<UserInfoAnalyticsModel>>() {
              @Override
              public void onSuccess(final Optional<UserInfoAnalyticsModel> analyticsInfoModelOptional) {
                settableFuture.set(null);
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

  public ListenableFuture<Void> execute(final String userId) {
    return execute(userId, listeningExecutorService);
  }
}
