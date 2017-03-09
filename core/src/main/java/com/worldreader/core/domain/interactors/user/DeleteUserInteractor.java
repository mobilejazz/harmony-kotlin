package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton public class DeleteUserInteractor {

  public enum Type {
    ANONYMOUS, LOGGED_IN, ALL
  }

  private final ListeningExecutorService executor;
  private final UserRepository repository;

  @Inject public DeleteUserInteractor(final ListeningExecutorService executor,
      final UserRepository userRepository) {
    this.executor = executor;
    repository = userRepository;
  }

  public ListenableFuture<Optional<User2>> execute(final Type type) {
    final SettableFuture<Optional<User2>> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(type, future));
    return future;
  }

  public ListenableFuture<Optional<User2>> execute(final Type type, final Executor executor) {
    final SettableFuture<Optional<User2>> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(type, future));
    return future;
  }

  @NonNull SafeRunnable getInteractorRunnable(final Type type,
      final SettableFuture<Optional<User2>> future) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        Preconditions.checkNotNull(type, "type == null");

        final UserStorageSpecification spec = toUserStorageSpecification(type);

        repository.remove(null, spec, new Callback<Optional<User2>>() {
          @Override public void onSuccess(final Optional<User2> optional) {
            future.set(optional);
          }

          @Override public void onError(final Throwable t) {
            future.setException(t);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    };
  }

  private UserStorageSpecification toUserStorageSpecification(final Type type) {
    switch (type) {
      case ANONYMOUS:
        return UserStorageSpecification.target(UserStorageSpecification.UserTarget.ANONYMOUS);
      case LOGGED_IN:
        return UserStorageSpecification.target(UserStorageSpecification.UserTarget.LOGGED_IN);
      case ALL:
        return UserStorageSpecification.target(
            UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS);
    }

    throw new IllegalArgumentException("type argument not mapped correctly to method!");
  }

}
