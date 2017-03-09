package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.repository.UserRepository;
import com.worldreader.core.error.general.UnexpectedErrorException;

import javax.inject.Inject;
import java.util.concurrent.*;

@PerActivity public class UpdateUserGoalsInteractor {

  private final ListeningExecutorService executor;
  private final UserRepository repository;

  @Inject public UpdateUserGoalsInteractor(final ListeningExecutorService executor,
      final UserRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<User2> execute(final int pagesPerDay, final int minChildrenAge,
      final int maxChildrenAge) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future, pagesPerDay, minChildrenAge, maxChildrenAge));
    return future;
  }

  public ListenableFuture<User2> execute(final int pagesPerDay, final int minChildrenAge,
      final int maxChildrenAge, final Executor executor) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future, pagesPerDay, minChildrenAge, maxChildrenAge));
    return future;
  }

  @NonNull private SafeRunnable getInteractorRunnable(final SettableFuture<User2> future,
      final int pagesPerDay, final int minChildrenAge, final int maxChildrenAge) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        repository.updateGoals(pagesPerDay, minChildrenAge, maxChildrenAge,
            new Callback<Optional<User2>>() {
              @Override public void onSuccess(final Optional<User2> optional) {
                if (optional.isPresent()) {
                  final User2 user = optional.get();
                  future.set(user);
                } else {
                  future.setException(new UnexpectedErrorException("User can't be retrieved"));
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

}
