package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.domain.repository.UserRepository;
import com.worldreader.core.error.general.UnexpectedErrorException;
import java.util.Date;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SendReadPagesInteractor {

  private final ListeningExecutorService executor;
  private final UserRepository repository;

  @Inject public SendReadPagesInteractor(ListeningExecutorService executor, UserRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<Boolean> execute(final String bookId, final int pages, final Date current) {
    return execute(bookId, pages, current, executor);
  }

  public ListenableFuture<Boolean> execute(final String bookId, final int pages, final Date current, final Executor executor) {
    final SettableFuture<Boolean> future = SettableFuture.create();
    executor.execute(getInteractorCallable(bookId, pages, current, future));
    return future;
  }

  @NonNull Runnable getInteractorCallable(final String bookId, final int pages, final Date current, final SettableFuture<Boolean> future) {
    return new Runnable() {

      @Override public void run() {
        repository.updateReadingStats(bookId, pages, current, new Callback<Optional<Boolean>>() {
          @Override public void onSuccess(Optional<Boolean> optional) {
            if (optional.isPresent()) {
              final Boolean result = optional.get();
              future.set(result);
            } else {
              onError(new UnexpectedErrorException("Boolean result is not defined!"));
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
