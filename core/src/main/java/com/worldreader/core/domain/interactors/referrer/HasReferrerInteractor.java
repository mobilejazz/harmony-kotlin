package com.worldreader.core.domain.interactors.referrer;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.domain.model.Referrer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton public class HasReferrerInteractor {

  private final ListeningExecutorService executorService;
  private final GetReferrerInteractor getReferrerInteractor;

  @Inject public HasReferrerInteractor(ListeningExecutorService executorService, GetReferrerInteractor getReferrerInteractor) {
    this.executorService = executorService;
    this.getReferrerInteractor = getReferrerInteractor;
  }

  public ListenableFuture<Boolean> execute() {
    return this.execute(this.executorService);
  }

  public ListenableFuture<Boolean> execute(ListeningExecutorService executorService) {
    return executorService.submit(new Callable<Boolean>() {
      @Override public Boolean call() throws Exception {
        Referrer referrer = getReferrerInteractor.execute(MoreExecutors.newDirectExecutorService()).get();
        return referrer != null && (referrer.getDeviceId() != null || referrer.getUserId() != null);
      }
    });

  }

}
