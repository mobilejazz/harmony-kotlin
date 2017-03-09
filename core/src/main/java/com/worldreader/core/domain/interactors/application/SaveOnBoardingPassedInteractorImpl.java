package com.worldreader.core.domain.interactors.application;

import android.support.annotation.NonNull;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.qualifiers.accessors.actions.CompleteOnboardingActionQualifier;
import com.worldreader.core.application.di.qualifiers.accessors.actions.DeleteOnboardingActionQualifier;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.Action;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.concurrent.*;

@Deprecated public class SaveOnBoardingPassedInteractorImpl
    extends AbstractInteractor<Boolean, ErrorCore> implements SaveOnBoardingPassedInteractor {

  private final Action<Void> completeOnBoardingAction;
  private final Action<Void> deleteOnBoardingAction;

  private boolean status;
  private DomainCallback<Boolean, ErrorCore> callback;

  @Inject public SaveOnBoardingPassedInteractorImpl(final InteractorExecutor executor,
      final MainThread mainThread,
      @CompleteOnboardingActionQualifier final Action<Void> completeOnBoardingAction,
      @DeleteOnboardingActionQualifier final Action<Void> deleteOnBoardingAction) {
    super(executor, mainThread);
    this.completeOnBoardingAction = completeOnBoardingAction;
    this.deleteOnBoardingAction = deleteOnBoardingAction;
  }

  @Override public void run() {
    performActions();
    performSuccessCallback(callback, true);
    callback = null;
  }

  @Override public void execute(boolean status, DomainCallback<Boolean, ErrorCore> callback) {
    this.status = status;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Boolean> execute(final boolean status) {
    final Executor executor = this.executor.getExecutor();
    final SettableFuture<Boolean> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(status, future));
    return future;
  }

  @NonNull Runnable getInteractorRunnable(final boolean status,
      final SettableFuture<Boolean> future) {
    return new Runnable() {
      @Override public void run() {
        performActions();
        future.set(true);
      }
    };
  }

  @Override
  public ListenableFuture<Boolean> execute(final boolean status, final Executor executor) {
    final SettableFuture<Boolean> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(status, future));
    return future;
  }

  //region Private methods
  private void performActions() {
    if (status) {
      completeOnBoardingAction.perform(null /*non parameters*/);
    } else {
      deleteOnBoardingAction.perform(null /*non parameters*/);
    }
  }
  //endregion

}
