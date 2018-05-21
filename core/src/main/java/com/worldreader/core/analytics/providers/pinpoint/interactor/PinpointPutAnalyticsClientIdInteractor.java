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
import javax.inject.Inject;

public class PinpointPutAnalyticsClientIdInteractor {

  private final GetUserInfoAnalyticsInteractor getAnalyticsInfoInteractor;
  private final ListeningExecutorService listeningExecutorService;
  private final Repository<UserInfoAnalyticsModel, RepositorySpecification> repository;

  @Inject
  public PinpointPutAnalyticsClientIdInteractor(
      final GetUserInfoAnalyticsInteractor getAnalyticsInfoInteractor,
      final ListeningExecutorService listeningExecutorService,
      final Repository<UserInfoAnalyticsModel, RepositorySpecification> repository
  ) {
    this.getAnalyticsInfoInteractor = getAnalyticsInfoInteractor;
    this.listeningExecutorService = listeningExecutorService;
    this.repository = repository;
  }

  public ListenableFuture<Void> execute(final String clientId) {
    final SettableFuture<Void> settableFuture = SettableFuture.create();

    listeningExecutorService.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final ListenableFuture<UserInfoAnalyticsModel> getAnalyticsInfoFuture =
            getAnalyticsInfoInteractor.execute(MoreExecutors.directExecutor());

        final UserInfoAnalyticsModel analyticsInfoModel = getAnalyticsInfoFuture.get();
        analyticsInfoModel.clientId = clientId;

        repository.put(analyticsInfoModel, RepositorySpecification.NONE,
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
}
