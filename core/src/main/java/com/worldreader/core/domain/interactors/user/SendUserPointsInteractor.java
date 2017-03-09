package com.worldreader.core.domain.interactors.user;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.domain.repository.UserRepository;
import com.worldreader.core.error.general.UnexpectedErrorException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SendUserPointsInteractor {

  private final ListeningExecutorService executor;
  private final UserRepository repository;

  @Inject
  public SendUserPointsInteractor(ListeningExecutorService executor, UserRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<Boolean> execute(final int points) {
    final SettableFuture<Boolean> future = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {
        repository.updatePoints(points, new Callback<Optional<Boolean>>() {
          @Override public void onSuccess(final Optional<Boolean> optional) {
            if (optional.isPresent()) {
              final Boolean resul = optional.get();
              future.set(resul);
            } else {
              future.setException(new UnexpectedErrorException("Result is not defined!"));
            }
          }

          @Override public void onError(final Throwable e) {
            future.setException(e);
          }
        });
      }
    });

    return future;
  }

}
