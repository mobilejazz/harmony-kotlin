package com.worldreader.core.domain.interactors.user.userbooks;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.repository.UserBooksRepository;

import javax.inject.Inject;

import java.util.concurrent.*;

import static com.worldreader.core.datasource.repository.spec.RepositorySpecification.SimpleRepositorySpecification;

@PerActivity public class GetUserBookInteractor {

  private final ListeningExecutorService executor;
  private final UserBooksRepository repository;

  @Inject
  public GetUserBookInteractor(ListeningExecutorService executor, UserBooksRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<Optional<UserBook>> execute(final String bookId) {
    return execute(bookId, executor);
  }

  public ListenableFuture<Optional<UserBook>> execute(final String bookId,
      final Executor executor) {
    final SettableFuture<Optional<UserBook>> settableFuture = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {
        final SimpleRepositorySpecification specification =
            new SimpleRepositorySpecification(bookId);

        repository.get(specification, new Callback<Optional<UserBook>>() {
          @Override public void onSuccess(Optional<UserBook> response) {
            settableFuture.set(response);
          }

          @Override public void onError(Throwable e) {
            settableFuture.setException(e);
          }
        });
      }
    });

    return settableFuture;
  }

}
