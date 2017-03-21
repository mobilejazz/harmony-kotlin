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
import java.util.Date;

@Singleton public class SendPageReadsInteractor {

  private final ListeningExecutorService executor;
  private final UserRepository repository;

  @Inject public SendPageReadsInteractor(ListeningExecutorService executor, UserRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<Boolean> execute(final String bookId, final int pages, final Date current) {
    final SettableFuture<Boolean> future = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {
        repository.updateReadingStats(bookId, pages, current, new Callback<Optional<Boolean>>() {
          @Override public void onSuccess(Optional<Boolean> optional) {
            if (optional.isPresent()) {
              final Boolean result = optional.get();
              future.set(result);
            } else {
              future.setException(new UnexpectedErrorException("Boolean result is not defined!"));
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
