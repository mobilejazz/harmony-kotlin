package com.worldreader.core.domain.interactors.user.userbooks;

import android.support.annotation.NonNull;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.model.user.UserBookLike;
import com.worldreader.core.domain.repository.UserBooksLikeRepository;
import com.worldreader.core.domain.repository.UserBooksRepository;
import com.worldreader.core.error.general.UnexpectedErrorException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton public class UnlikeBookInteractor {

  private final ListeningExecutorService executor;
  private final UserBooksRepository repository;
  private final UserBooksLikeRepository userBooksLikeRepository;

  @Inject
  public UnlikeBookInteractor(ListeningExecutorService executor, UserBooksRepository repository,
      final UserBooksLikeRepository userBooksLikeRepository) {
    this.executor = executor;
    this.repository = repository;
    this.userBooksLikeRepository = userBooksLikeRepository;
  }

  public ListenableFuture<UserBook> execute(@NonNull final String bookId) {
    return execute(bookId, executor);
  }

  public ListenableFuture<UserBook> execute(@NonNull final String bookId,
      @NonNull final Executor executor) {
    final SettableFuture<UserBook> future = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        Preconditions.checkNotNull(bookId, "bookId == null");

        userBooksLikeRepository.unlike(bookId, new Callback<Optional<UserBookLike>>() {
          @Override public void onSuccess(final Optional<UserBookLike> userBookLikeOptional) {
            repository.unlike(bookId, new Callback<Optional<UserBook>>() {
              @Override public void onSuccess(final Optional<UserBook> optional) {
                if (optional.isPresent()) {
                  final UserBook userBookLike = optional.get();
                  future.set(userBookLike);
                } else {
                  future.setException(new UnexpectedErrorException("UserBook is not defined"));
                }
              }

              @Override public void onError(final Throwable e) {
                future.setException(e);
              }
            });
          }

          @Override public void onError(final Throwable e) {
            future.setException(e);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    });

    return future;
  }

}
