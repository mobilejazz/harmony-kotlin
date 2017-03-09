package com.worldreader.core.domain.interactors.user.application;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.helper.Action;

import javax.inject.Inject;
import java.util.concurrent.*;

@PerActivity public class DeleteOnBoardingPassedInteractor {

  private final ListeningExecutorService executor;
  private final Action<Boolean> deleteOnboardingAction;

  @Inject public DeleteOnBoardingPassedInteractor(final ListeningExecutorService executor,
      final Action<Boolean> deleteOnboardingAction) {
    this.executor = executor;
    this.deleteOnboardingAction = deleteOnboardingAction;
  }

  public ListenableFuture<Void> execute() {
    final SettableFuture<Void> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future));
    return future;
  }

  public ListenableFuture<Void> execute(final Executor executor) {
    final SettableFuture<Void> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future));
    return future;
  }

  private Runnable getInteractorRunnable(final SettableFuture<Void> future) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        deleteOnboardingAction.perform(null /*no parameters required*/);
        future.set(null);
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    };
  }

}
