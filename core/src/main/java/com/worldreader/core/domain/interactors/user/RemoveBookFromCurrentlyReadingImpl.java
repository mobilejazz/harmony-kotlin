package com.worldreader.core.domain.interactors.user;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.repository.UserBooksRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.concurrent.*;

public class RemoveBookFromCurrentlyReadingImpl extends AbstractInteractor<Boolean, ErrorCore>
    implements RemoveBookFromCurrentlyReading {

  private final UserBooksRepository userBooksRepository;

  private String bookId;
  private DomainCallback<Boolean, ErrorCore> callback;

  @Inject
  public RemoveBookFromCurrentlyReadingImpl(InteractorExecutor executor, MainThread mainThread,
      final UserBooksRepository userBooksRepository) {
    super(executor, mainThread);
    this.userBooksRepository = userBooksRepository;
  }

  @Override public void execute(String bookId, DomainCallback<Boolean, ErrorCore> callback) {
    this.bookId = bookId;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Boolean> execute(final String bookId) {
    return execute(bookId, getExecutor());
  }

  @Override public ListenableFuture<Boolean> execute(final String bookId, final Executor executor) {
    final SettableFuture<Boolean> settableFuture = SettableFuture.create();
    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        executeLogic(bookId, new Callback<Boolean>() {
          @Override public void onSuccess(final Boolean result) {
            settableFuture.set(result);
          }

          @Override public void onError(final Throwable e) {
            settableFuture.setException(e);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });
    return settableFuture;
  }

  @Override public void run() {
    executeLogic(bookId, new Callback<Boolean>() {
      @Override public void onSuccess(final Boolean result) {
        performSuccessCallback(callback, result);
      }

      @Override public void onError(final Throwable e) {
        performErrorCallback(callback, ErrorCore.of(e));
      }
    });
  }

  //region Private methods
  private void executeLogic(final String bookId, final Callback<Boolean> callback) {
    userBooksRepository.unmarkInMyBooks(bookId, new Callback<Optional<UserBook>>() {
      @Override public void onSuccess(final Optional<UserBook> userBookOptional) {
        if (userBookOptional.isPresent()) {
          final UserBook userBook = userBookOptional.get();

          if (callback != null) {
            callback.onSuccess(!userBook.isInMyBooks());
          }
        } else {
          if (callback != null) {
            callback.onSuccess(false);
          }
        }
      }

      @Override public void onError(final Throwable e) {
        if (callback != null) {
          callback.onError(e);
        }
      }
    });

  }
  //endregion
}
