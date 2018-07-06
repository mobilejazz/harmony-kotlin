package com.worldreader.core.analytics.interactors;

import android.text.TextUtils;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.analytics.models.UserInfoAnalyticsModel;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

public class GetUserInfoAnalyticsInteractor {

  private final Repository<UserInfoAnalyticsModel, RepositorySpecification> repository;
  private final ListeningExecutorService listeningExecutorService;

  @Inject public GetUserInfoAnalyticsInteractor(
      final Repository<UserInfoAnalyticsModel, RepositorySpecification> repository,
      final ListeningExecutorService executor
  ) {
    this.repository = repository;
    this.listeningExecutorService = executor;
  }

  public ListenableFuture<UserInfoAnalyticsModel> execute(final Executor executor) {
    final SettableFuture<UserInfoAnalyticsModel> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() {
        repository.get(RepositorySpecification.NONE, new Callback<Optional<UserInfoAnalyticsModel>>() {
          @Override
          public void onSuccess(final Optional<UserInfoAnalyticsModel> analyticsInfoModelOptional) {
            if (analyticsInfoModelOptional.isPresent()) {
              final UserInfoAnalyticsModel info = analyticsInfoModelOptional.get();
              if (TextUtils.isEmpty(info.deviceId)) {
                info.deviceId = UUID.randomUUID().toString();
                repository.put(info, RepositorySpecification.NONE, null);
              }
              settableFuture.set(info);
              return;
            }

            settableFuture.set(UserInfoAnalyticsModel.EMPTY);
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

  public ListenableFuture<UserInfoAnalyticsModel> execute() {
    return execute(listeningExecutorService);
  }
}
