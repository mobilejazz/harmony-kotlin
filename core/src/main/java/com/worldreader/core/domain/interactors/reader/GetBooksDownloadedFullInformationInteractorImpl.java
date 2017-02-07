package com.worldreader.core.domain.interactors.reader;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.helper.Provider;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookDownloaded;
import com.worldreader.core.domain.repository.BookRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

public class GetBooksDownloadedFullInformationInteractorImpl
    extends AbstractInteractor<List<Book>, ErrorCore<?>>
    implements GetBooksDownloadedFullInformationInteractor {

  private final Provider<List<BookDownloaded>> bookDownloadedProvider;
  private final BookRepository bookRepository;

  private DomainCallback<List<Book>, ErrorCore<?>> callback;

  private List<Book> booksDownloaded;

  @Inject public GetBooksDownloadedFullInformationInteractorImpl(final InteractorExecutor executor,
      final MainThread mainThread, final Provider<List<BookDownloaded>> bookDownloadedProvider,
      final BookRepository bookRepository) {
    super(executor, mainThread);
    this.bookDownloadedProvider = bookDownloadedProvider;
    this.bookRepository = bookRepository;
  }

  @Override public void execute(DomainCallback<List<Book>, ErrorCore<?>> callback) {
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Optional<List<Book>>> execute() {
    final SettableFuture<Optional<List<Book>>> settableFuture = SettableFuture.create();

    getExecutor().execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        execute(new Callback<List<Book>>() {
          @Override public void onSuccess(final List<Book> books) {
            settableFuture.set(Optional.fromNullable(books));
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
    execute(new DomainCallback<List<Book>, ErrorCore<?>>(mainThread) {
      @Override public void onSuccessResult(final List<Book> result) {
        performSuccessCallback(callback, result);
      }

      @Override public void onErrorResult(final ErrorCore<?> result) {
        performErrorCallback(callback, result);
      }
    });
  }

  public void execute(final Callback<List<Book>> callback) {
    final List<BookDownloaded> allBooksDownloaded = bookDownloadedProvider.get();

    booksDownloaded = new ArrayList<>();

    if (allBooksDownloaded.size() > 0) {
      for (BookDownloaded bookDownloaded : allBooksDownloaded) {
        bookRepository.bookDetailLatest(bookDownloaded.getBookId(), false,
            new CompletionCallback<Book>() {
              @Override public void onSuccess(Book book) {
                booksDownloaded.add(book);

                if (booksDownloaded.size() >= allBooksDownloaded.size()) {
                  if (callback != null) {
                    callback.onSuccess(booksDownloaded);
                  }
                }
              }

              @Override public void onError(ErrorCore errorCore) {
                if (callback != null) {
                  callback.onError(errorCore.getCause());
                }
              }
            });
      }
    } else {
      if (callback != null) {
        callback.onSuccess(booksDownloaded);
      }
    }

  }
}
