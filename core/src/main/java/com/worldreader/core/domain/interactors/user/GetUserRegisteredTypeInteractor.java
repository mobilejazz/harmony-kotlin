package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.datasource.helper.Provider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton public class GetUserRegisteredTypeInteractor {

  public enum UserRegisteredType {
    WORLDREADER, GOOGLE, FACEBOOK, UNKNOWN
  }

  private final ListeningExecutorService executor;
  private final Provider<String> getUserRegisteredTypeProvider;

  @Inject public GetUserRegisteredTypeInteractor(final ListeningExecutorService executor,
      final Provider<String> getUserRegisteredTypeProvider) {
    this.executor = executor;
    this.getUserRegisteredTypeProvider = getUserRegisteredTypeProvider;
  }

  public ListenableFuture<UserRegisteredType> execute() {
    final SettableFuture<UserRegisteredType> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future));
    return future;
  }

  public ListenableFuture<UserRegisteredType> execute(final Executor executor) {
    final SettableFuture<UserRegisteredType> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future));
    return future;
  }

  @NonNull private Runnable getInteractorRunnable(final SettableFuture<UserRegisteredType> future) {
    return new Runnable() {
      @Override public void run() {

        final String rawType = getUserRegisteredTypeProvider.get();
        if (rawType == null) {
          future.set(UserRegisteredType.UNKNOWN);
        } else {
          final UserRegisteredType type = UserRegisteredType.valueOf(rawType);
          future.set(type);
        }
      }
    };
  }

}
