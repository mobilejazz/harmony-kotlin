package com.worldreader.core.domain.interactors.user.userbooks;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.spec.userbooks.UserBookStorageSpecification;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.repository.UserBooksRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class PutUserBookInteractor {

  private final ListeningExecutorService executor;
  private final UserBooksRepository repository;

  @Inject public PutUserBookInteractor(final ListeningExecutorService executor, final UserBooksRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<Optional<UserBook>> execute(final UserBookStorageSpecification specification, final UserBook userbook) {
    final SettableFuture<Optional<UserBook>> settableFuture = SettableFuture.create();
    executor.execute(getInteractorRunnable(specification, userbook, settableFuture));
    return settableFuture;
  }

  private Runnable getInteractorRunnable(final UserBookStorageSpecification specification, final UserBook userbook,
      final SettableFuture<Optional<UserBook>> future) {
    return new Runnable() {
      @Override public void run() {
        repository.put(userbook, specification, new Callback<Optional<UserBook>>() {
          @Override public void onSuccess(final Optional<UserBook> optional) {
            future.set(optional);
          }

          @Override public void onError(final Throwable e) {
            future.setException(e);
          }
        });
      }
    };
  }

}
