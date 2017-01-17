package com.worldreader.core.domain.interactors.book;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.repository.BookRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;

public class GetBookDetailInteractorImpl extends AbstractInteractor<Book, ErrorCore<?>>
    implements GetBookDetailInteractor {

  private BookRepository bookRepository;

  private String bookId;
  private boolean forceUpdate;

  private DomainCallback<Book, ErrorCore<?>> callback;
  private DomainBackgroundCallback<Book, ErrorCore<?>> backgroundCallback;

  @Inject public GetBookDetailInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      BookRepository repository) {
    super(executor, mainThread);
    this.bookRepository = repository;
  }

  @Override public void execute(String bookId, DomainCallback<Book, ErrorCore<?>> callback) {
    execute(bookId, false, callback);
  }

  @Override public void execute(String bookId, boolean forceUpdate,
      DomainCallback<Book, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.forceUpdate = forceUpdate;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void execute(String bookId, boolean forceUpdate,
      DomainBackgroundCallback<Book, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.forceUpdate = forceUpdate;
    this.backgroundCallback = callback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Optional<Book>> execute(final String bookId) {
    final SettableFuture<Optional<Book>> settableFuture = SettableFuture.create();

    getExecutor().execute(new Runnable() {
      @Override public void run() {
        execute(bookId, false, new Callback<Book>() {
          @Override public void onSuccess(Book book) {
            settableFuture.set(Optional.fromNullable(book));
          }

          @Override public void onError(Throwable e) {
            settableFuture.setException(e);
          }
        });
      }
    });

    return settableFuture;
  }

  @Override public void run() {
    execute(bookId, forceUpdate, new Callback<Book>() {
      @Override public void onSuccess(Book book) {
        if (backgroundCallback != null) {
          backgroundCallback.onSuccess(book);
        } else {
          performSuccessCallback(callback, book);
        }
      }

      @Override public void onError(Throwable e) {
        if (backgroundCallback != null) {
          backgroundCallback.onError(ErrorCore.of(e));
        } else {
          performErrorCallback(callback, ErrorCore.of(e));
        }
      }
    });
  }

  private void execute(final String bookId, final boolean forceUpdate,
      final Callback<Book> callback) {
    bookRepository.bookDetailLatest(bookId, forceUpdate, new CompletionCallback<Book>() {
      @Override public void onSuccess(Book result) {
        if (callback != null) {
          callback.onSuccess(result);
        }
      }

      @Override public void onError(ErrorCore error) {
        if (callback != null) {
          callback.onError(error.getCause());
        }
      }
    });
  }
}
