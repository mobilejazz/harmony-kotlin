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
import com.worldreader.core.error.general.UnexpectedErrorException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton public class SaveUserInteractor {

  public enum Type {
    ANONYMOUS, LOGGED_IN
  }

  private final ListeningExecutorService executor;
  private final UserRepository repository;

  @Inject public SaveUserInteractor(final ListeningExecutorService executor,
      final UserRepository userRepository) {
    this.executor = executor;
    this.repository = userRepository;
  }

  public ListenableFuture<User2> execute(final User2 user, final Type type) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(user, type, future));
    return future;
  }

  public ListenableFuture<User2> execute(final User2 user, final Type type,
      final Executor executor) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(user, type, future));
    return future;
  }

  @NonNull SafeRunnable getInteractorRunnable(final User2 user, final Type type,
      final SettableFuture<User2> future) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        Preconditions.checkNotNull(user, "user == null");
        Preconditions.checkNotNull(type, "type == null");

        final UserStorageSpecification spec = toUserStorageSpec(type);

        repository.put(user, spec, new Callback<Optional<User2>>() {
          @Override public void onSuccess(final Optional<User2> optional) {
            if (optional.isPresent()) {
              final User2 user2 = optional.get();
              future.set(user2);
            } else {
              future.setException(new UnexpectedErrorException("Anonymous user can't be created!"));
            }
          }

          @Override public void onError(final Throwable e) {
            future.setException(e);
          }
        });

      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    };
  }

  private UserStorageSpecification toUserStorageSpec(final Type type) {
    switch (type) {
      case ANONYMOUS:
        return UserStorageSpecification.target(UserStorageSpecification.UserTarget.ANONYMOUS);
      case LOGGED_IN:
        return UserStorageSpecification.target(UserStorageSpecification.UserTarget.LOGGED_IN);
    }

    throw new IllegalArgumentException("type not correctly mapped!");
  }

}
