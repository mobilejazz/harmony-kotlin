package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.NetworkSpecification;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.error.user.UserNotFoundException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton public class GetUserInteractor {

  private final ListeningExecutorService executor;
  private final Repository<User2, RepositorySpecification> repository;

  @Inject public GetUserInteractor(ListeningExecutorService executor,
      Repository<User2, RepositorySpecification> repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<User2> execute() {
    return execute(new NetworkSpecification(), executor);
  }

  public ListenableFuture<User2> execute(final Executor executor) {
    final NetworkSpecification spec = new NetworkSpecification();
    return execute(spec, executor);
  }

  public ListenableFuture<User2> execute(UserStorageSpecification spec) {
    return execute(spec, executor);
  }

  public ListenableFuture<User2> execute(final UserStorageSpecification spec,
      final Executor executor) {
    final SettableFuture<User2> settableFuture = SettableFuture.create();
    executor.execute(getInteractorRunnable(settableFuture, spec));
    return settableFuture;
  }

  public ListenableFuture<User2> execute(final NetworkSpecification spec, final Executor executor) {
    final SettableFuture<User2> settableFuture = SettableFuture.create();
    executor.execute(getInteractorRunnable(settableFuture, spec));
    return settableFuture;
  }

  @NonNull private Runnable getInteractorRunnable(final SettableFuture<User2> settableFuture,
      final RepositorySpecification spec) {
    return new Runnable() {
      @Override public void run() {
        repository.get(spec, new Callback<Optional<User2>>() {
          @Override public void onSuccess(Optional<User2> optional) {
            if (optional.isPresent()) {
              final User2 user = optional.get();
              settableFuture.set(user);
            } else {
              settableFuture.setException(new UserNotFoundException());
            }
          }

          @Override public void onError(Throwable e) {
            settableFuture.setException(e);
          }
        });
      }
    };
  }
}
