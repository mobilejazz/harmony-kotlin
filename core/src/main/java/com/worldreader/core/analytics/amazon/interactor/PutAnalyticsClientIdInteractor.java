package com.worldreader.core.analytics.amazon.interactor;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.analytics.amazon.model.AnalyticsInfoModel;

import javax.inject.Inject;

public class PutAnalyticsClientIdInteractor {

  private final GetAnalyticsInfoInteractor getAnalyticsInfoInteractor;
  private final ListeningExecutorService listeningExecutorService;
  private final Repository<AnalyticsInfoModel, RepositorySpecification> repository;

  @Inject
  public PutAnalyticsClientIdInteractor(final GetAnalyticsInfoInteractor getAnalyticsInfoInteractor,
      final ListeningExecutorService listeningExecutorService,
      final Repository<AnalyticsInfoModel, RepositorySpecification> repository) {
    this.getAnalyticsInfoInteractor = getAnalyticsInfoInteractor;
    this.listeningExecutorService = listeningExecutorService;
    this.repository = repository;
  }

  public ListenableFuture<Void> execute(final String clientId) {
    final SettableFuture<Void> settableFuture = SettableFuture.create();

    listeningExecutorService.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final ListenableFuture<AnalyticsInfoModel> getAnalyticsInfoFuture =
            getAnalyticsInfoInteractor.execute(MoreExecutors.directExecutor());

        final AnalyticsInfoModel analyticsInfoModel = getAnalyticsInfoFuture.get();
        analyticsInfoModel.setClientId(clientId);

        repository.put(analyticsInfoModel, RepositorySpecification.NONE,
            new Callback<Optional<AnalyticsInfoModel>>() {
              @Override
              public void onSuccess(final Optional<AnalyticsInfoModel> analyticsInfoModelOptional) {
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
