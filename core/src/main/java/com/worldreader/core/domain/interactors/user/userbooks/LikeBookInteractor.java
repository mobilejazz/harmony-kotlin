package com.worldreader.core.domain.interactors.user.userbooks;

import android.support.annotation.NonNull;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.repository.UserBooksRepository;
import com.worldreader.core.error.general.UnexpectedErrorException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class LikeBookInteractor {

  private final ListeningExecutorService executor;
  private final UserBooksRepository repository;

  @Inject
  public LikeBookInteractor(ListeningExecutorService executor, UserBooksRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<UserBook> execute(@NonNull final String bookId) {
    final SettableFuture<UserBook> future = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {
        Preconditions.checkNotNull(bookId, "bookId == null");

        repository.like(bookId, new Callback<Optional<UserBook>>() {
          @Override public void onSuccess(Optional<UserBook> optional) {
            if (optional.isPresent()) {
              final UserBook userBookLike = optional.get();
              future.set(userBookLike);
            } else {
              future.setException(new UnexpectedErrorException("UserBook is not defined"));
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
