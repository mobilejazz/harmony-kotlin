package com.worldreader.core.domain.interactors.user;

import android.support.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.interactors.user.userbooks.GetUserBookInteractor;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;

public class IsBookCurrentlyReadingInteractorImpl extends AbstractInteractor<Boolean, ErrorCore>
    implements IsBookCurrentlyReadingInteractor {

  private final GetUserBookInteractor getUserBookInteractor;
  private final InteractorHandler interactorHandler;

  private String bookId;
  private DomainCallback<Boolean, ErrorCore> callback;

  @Inject
  public IsBookCurrentlyReadingInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      final GetUserBookInteractor getUserBookInteractor,
      final InteractorHandler interactorHandler) {
    super(executor, mainThread);
    this.getUserBookInteractor = getUserBookInteractor;
    this.interactorHandler = interactorHandler;
  }

  @Override public void execute(String bookId, DomainCallback<Boolean, ErrorCore> callback) {
    this.bookId = bookId;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Boolean> execute(final String bookId) {
    return Futures.transform(getUserBookInteractor.execute(bookId),
        new Function<Optional<UserBook>, Boolean>() {
          @Override public Boolean apply(final Optional<UserBook> input) {
            if (input.isPresent()) {
              final UserBook userBook = input.get();
              return userBook.isInMyBooks();
            } else {
              return false;
            }
          }
        });
  }

  @Override public void run() {
    final ListenableFuture<Optional<UserBook>> userBookLf = getUserBookInteractor.execute(bookId);

    interactorHandler.addCallback(userBookLf, new FutureCallback<Optional<UserBook>>() {
      @Override public void onSuccess(@Nullable final Optional<UserBook> result) {
        if (result.isPresent()) {
          final UserBook userBook = result.get();
          performSuccessCallback(callback, userBook.isInMyBooks());
        } else {
          performSuccessCallback(callback, false);
        }
      }

      @Override public void onFailure(final Throwable t) {

      }
    }, MoreExecutors.directExecutor());
  }
}
