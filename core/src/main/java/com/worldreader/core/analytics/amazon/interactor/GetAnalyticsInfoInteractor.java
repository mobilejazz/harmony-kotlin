package com.worldreader.core.analytics.amazon.interactor;

import android.text.TextUtils;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.analytics.amazon.model.AnalyticsInfoModel;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

public class GetAnalyticsInfoInteractor {

  private final Repository<AnalyticsInfoModel, RepositorySpecification> repository;
  private final ListeningExecutorService listeningExecutorService;

  @Inject public GetAnalyticsInfoInteractor(
      final Repository<AnalyticsInfoModel, RepositorySpecification> repository,
      final ListeningExecutorService listeningExecutorService) {
    this.repository = repository;
    this.listeningExecutorService = listeningExecutorService;
  }

  public ListenableFuture<AnalyticsInfoModel> execute(final Executor executor) {
    final SettableFuture<AnalyticsInfoModel> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        repository.get(RepositorySpecification.NONE, new Callback<Optional<AnalyticsInfoModel>>() {
          @Override
          public void onSuccess(final Optional<AnalyticsInfoModel> analyticsInfoModelOptional) {
            if (analyticsInfoModelOptional.isPresent()) {
              final AnalyticsInfoModel analyticsInfoModel = analyticsInfoModelOptional.get();

              if (TextUtils.isEmpty(analyticsInfoModel.getDeviceId())) {
                analyticsInfoModel.setDeviceId(UUID.randomUUID().toString());
                repository.put(analyticsInfoModel, RepositorySpecification.NONE, null);
              }

              settableFuture.set(analyticsInfoModel);
            } else {
              settableFuture.set(AnalyticsInfoModel.EMPTY);
            }
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

  public ListenableFuture<AnalyticsInfoModel> execute() {
    return execute(listeningExecutorService);
  }
}
