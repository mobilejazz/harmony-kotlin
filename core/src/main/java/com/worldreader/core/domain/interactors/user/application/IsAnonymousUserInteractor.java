package com.worldreader.core.domain.interactors.user.application;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.repository.spec.StorageSpecification;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UsersTable;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

import static com.worldreader.core.datasource.spec.user.UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS;
import static com.worldreader.core.datasource.spec.user.UserStorageSpecification.target;

@Singleton public class IsAnonymousUserInteractor {

  public enum Type {
    ANONYMOUS, REGISTERED, NONE // Empty database
  }

  private final ListeningExecutorService executor;
  private final UserRepository userRepository;

  @Inject public IsAnonymousUserInteractor(final ListeningExecutorService executor,
      final UserRepository userRepository) {
    this.executor = executor;
    this.userRepository = userRepository;
  }

  public ListenableFuture<IsAnonymousUserInteractor.Type> execute() {
    return execute(executor);
  }

  public ListenableFuture<IsAnonymousUserInteractor.Type> execute(final Executor executor) {
    final SettableFuture<Type> future = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final StorageSpecification spec = target(FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS);

        userRepository.get(spec, new Callback<Optional<User2>>() {
          @Override public void onSuccess(final Optional<User2> optional) {
            final boolean present = optional.isPresent();
            if (!present) {
              future.set(Type.NONE);
            } else {
              final User2 user2 = optional.get();
              final String id = user2.getId();
              final Type type =
                  id.equals(UsersTable.ANONYMOUS_USER_ID) ? Type.ANONYMOUS : Type.REGISTERED;
              future.set(type);
            }
          }

          @Override public void onError(final Throwable t) {
            future.setException(t);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    });

    return future;
  }

}
