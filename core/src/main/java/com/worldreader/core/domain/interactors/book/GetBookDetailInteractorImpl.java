package com.worldreader.core.domain.interactors.book;

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

  @Override public void run() {
    bookRepository.bookDetailLatest(bookId, forceUpdate, new CompletionCallback<Book>() {
      @Override public void onSuccess(Book result) {
        if (backgroundCallback != null) {
          backgroundCallback.onSuccess(result);
        } else {
          performSuccessCallback(callback, result);
        }
      }

      @Override public void onError(ErrorCore error) {
        if (backgroundCallback != null) {
          backgroundCallback.onError(error);
        } else {
          performErrorCallback(callback, error);
        }
      }
    });
  }
}
