package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.spec.user.UpdateUserCategoriesSpecification;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.repository.UserRepository;
import com.worldreader.core.error.general.UnexpectedErrorException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.*;

@Singleton public class SaveUserCategoriesInteractor {

  private final ListeningExecutorService executor;
  private final UserRepository repository;

  @Inject public SaveUserCategoriesInteractor(ListeningExecutorService executor,
      UserRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<User2> execute(final List<Integer> categories) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(categories, future));
    return future;
  }

  public ListenableFuture<User2> execute(final List<Integer> categories, final Executor executor) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(categories, future));
    return future;
  }

  @NonNull Runnable getInteractorRunnable(final List<Integer> categories,
      final SettableFuture<User2> future) {
    return new Runnable() {
      @Override public void run() {
        final UpdateUserCategoriesSpecification spec =
            new UpdateUserCategoriesSpecification(categories);
        repository.put(null, spec, new Callback<Optional<User2>>() {
          @Override public void onSuccess(Optional<User2> optional) {
            if (optional.isPresent()) {
              final User2 user = optional.get();
              future.set(user);
            } else {
              future.setException(new UnexpectedErrorException("User is not defined!"));
            }
          }

          @Override public void onError(Throwable e) {
            future.setException(e);
          }
        });
      }
    };
  }

}
