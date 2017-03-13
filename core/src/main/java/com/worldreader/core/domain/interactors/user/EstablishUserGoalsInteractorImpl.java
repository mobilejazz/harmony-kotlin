package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.helper.Action;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.concurrent.*;

@Deprecated public class EstablishUserGoalsInteractorImpl
    extends AbstractInteractor<Boolean, ErrorCore<?>> implements EstablishUserGoalsInteractor {

  private final ListeningExecutorService executorService;
  private final Action<Boolean, Boolean> completeGoalsSettingsAction;

  @Inject public EstablishUserGoalsInteractorImpl(InteractorExecutor interactorExecutor,
      MainThread mainThread, ListeningExecutorService executor,
      final Action<Boolean, Boolean> completeGoalsSettingsAction) {
    super(interactorExecutor, mainThread);
    this.completeGoalsSettingsAction = completeGoalsSettingsAction;
    this.executorService = executor;
  }

  @Override public ListenableFuture<Boolean> execute() {
    final SettableFuture<Boolean> future = SettableFuture.create();
    executorService.execute(getInteractorRunnable(future));
    return future;
  }

  @Override public ListenableFuture<Boolean> execute(final Executor executor) {
    final SettableFuture<Boolean> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future));
    return future;
  }

  @Override public void run() {
    // Ignored
  }

  @NonNull private SafeRunnable getInteractorRunnable(final SettableFuture<Boolean> future) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final boolean result = completeGoalsSettingsAction.perform(null /*non parameters*/);
        future.set(result);
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    };
  }
}
