package com.worldreader.core.domain.interactors.reader;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.BookDownloaded;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

public class IsBookDownloadedInteractorImpl extends AbstractInteractor<Boolean, ErrorCore<?>>
    implements IsBookDownloadedInteractor {

  private final GetBooksDownloadedInteractor getBooksDownloadedInteractor;

  private String bookId;
  private DomainCallback<Boolean, ErrorCore<?>> callback;

  @Inject public IsBookDownloadedInteractorImpl(final InteractorExecutor executor,
      final MainThread mainThread,
      final GetBooksDownloadedInteractor getBooksDownloadedInteractor) {
    super(executor, mainThread);
    this.getBooksDownloadedInteractor = getBooksDownloadedInteractor;
  }

  @Override
  public void execute(final String bookId, DomainCallback<Boolean, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Boolean> execute(final String bookId) {
    final SettableFuture<Boolean> settableFuture = SettableFuture.create();

    getExecutor().execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final ListenableFuture<Optional<List<BookDownloaded>>> getBooksDownloadedInteractorLf =
            getBooksDownloadedInteractor.execute(MoreExecutors.directExecutor());

        final Optional<List<BookDownloaded>> booksDownlaodedOp =
            getBooksDownloadedInteractorLf.get();

        if (booksDownlaodedOp.isPresent()) {
          final List<BookDownloaded> booksDownloaded = booksDownlaodedOp.get();
          settableFuture.set(isBookDownloaded(bookId, booksDownloaded));

        } else {
          settableFuture.set(false);
        }
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }

  @Override public void run() {
    getBooksDownloadedInteractor.execute(
        new DomainBackgroundCallback<List<BookDownloaded>, ErrorCore<?>>() {
          @Override public void onSuccess(List<BookDownloaded> bookDownloaded) {
            boolean isBookDownloaded = isBookDownloaded(bookId, bookDownloaded);

            performSuccessCallback(callback, isBookDownloaded);
          }

          @Override public void onError(ErrorCore<?> errorCore) {
            performErrorCallback(callback, errorCore);
          }
        });
  }

  private boolean isBookDownloaded(final String bookId, final List<BookDownloaded> bookDownloaded) {
    boolean isBookDownloaded = false;

    for (BookDownloaded book : bookDownloaded) {
      if (book.getBookId().equals(bookId)) {
        isBookDownloaded = true;
        break;
      }
    }
    return isBookDownloaded;
  }
}
