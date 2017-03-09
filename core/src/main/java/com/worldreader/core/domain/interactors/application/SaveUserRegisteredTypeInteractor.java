package com.worldreader.core.domain.interactors.application;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.datasource.helper.Action;
import com.worldreader.core.domain.interactors.user.GetUserRegisteredTypeInteractor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton public class SaveUserRegisteredTypeInteractor {

  private final ListeningExecutorService executor;
  private final Action<String> addUserRegisteredTypeAction;

  @Inject public SaveUserRegisteredTypeInteractor(final ListeningExecutorService executor,
      final Action<String> addUserRegisteredTypeAction) {
    this.executor = executor;
    this.addUserRegisteredTypeAction = addUserRegisteredTypeAction;
  }

  public ListenableFuture<Void> execute(
      @Nullable final GetUserRegisteredTypeInteractor.UserRegisteredType type) {
    final SettableFuture<Void> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(type));
    return future;
  }

  public ListenableFuture<Void> execute(
      @Nullable final GetUserRegisteredTypeInteractor.UserRegisteredType type,
      final Executor executor) {
    final SettableFuture<Void> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(type));
    return future;
  }

  @NonNull private Runnable getInteractorRunnable(
      final @Nullable GetUserRegisteredTypeInteractor.UserRegisteredType type) {
    return new Runnable() {
      @Override public void run() {
        if (type != null) {
          final String s = type.toString();
          addUserRegisteredTypeAction.perform(s);
        } else {
          addUserRegisteredTypeAction.perform(null);
        }
      }
    };
  }

}
