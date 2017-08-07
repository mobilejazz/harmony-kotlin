package com.worldreader.core.domain.interactors.user;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.repository.UserBooksRepository;
import java.util.concurrent.Callable;
import javax.inject.Inject;

@PerActivity public class FinishBookInteractor {

  private final ListeningExecutorService executor;
  private final UserBooksRepository repository;

  @Inject public FinishBookInteractor(final ListeningExecutorService executor, final UserBooksRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<Boolean> execute(final String bookId) {
    return executor.submit(new Callable<Boolean>() {

      private Boolean result;

      @Override public Boolean call() throws Exception {
        repository.finish(bookId, new Callback<Optional<UserBook>>() {
          @Override public void onSuccess(final Optional<UserBook> optional) {
            result = optional.isPresent();
          }

          @Override public void onError(final Throwable e) {
            throw new RuntimeException(e);
          }
        });

        return result;
      }
    });
  }
}
