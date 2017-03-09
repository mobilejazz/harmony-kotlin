package com.worldreader.core.domain.interactors.user;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.domain.repository.UserRepository;
import com.worldreader.core.error.general.UnexpectedErrorException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class ResetPasswordInteractor {

  private final ListeningExecutorService executor;
  private final UserRepository repository;

  @Inject public ResetPasswordInteractor(final ListeningExecutorService executor,
      final UserRepository userRepository) {
    this.executor = executor;
    this.repository = userRepository;
  }

  public ListenableFuture<Boolean> execute(final String email) {
    final SettableFuture<Boolean> future = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {
        Preconditions.checkNotNull(email, "email == null");

        repository.resetPassword(email, new Callback<Optional<Boolean>>() {
          @Override public void onSuccess(Optional<Boolean> optional) {
            if (optional.isPresent()) {
              final Boolean result = optional.get();
              future.set(result);
            } else {
              future.setException(new UnexpectedErrorException("Reset password is not defined!"));
            }
          }

          @Override public void onError(Throwable e) {
            future.setException(e);
          }
        });
      }
    });

    return future;
  }

}
