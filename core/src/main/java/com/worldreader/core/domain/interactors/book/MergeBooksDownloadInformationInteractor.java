package com.worldreader.core.domain.interactors.book;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.helper.Provider;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookDownloaded;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

public class MergeBooksDownloadInformationInteractor {

  private final Provider<List<BookDownloaded>> booksDownloadListProvider;

  @Inject public MergeBooksDownloadInformationInteractor(
      final Provider<List<BookDownloaded>> booksDownloadListProvider) {
    this.booksDownloadListProvider = booksDownloadListProvider;
  }

  public ListenableFuture<Optional<List<Book>>> execute(final Optional<List<Book>> booksOp,
      final Executor executor) {
    final SettableFuture<Optional<List<Book>>> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final List<BookDownloaded> bookDownloadeds = booksDownloadListProvider.get();

        if (booksOp.isPresent()) {
          final List<Book> books = booksOp.get();

          for (final Book book : books) {
            for (final BookDownloaded bookDownloaded : bookDownloadeds) {
              if (book.getId().equals(bookDownloaded.getBookId())) {
                book.setBookDownloaded(true);
              }
            }
          }

          settableFuture.set(Optional.of(books));
        } else {
          settableFuture.set(booksOp);
        }
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }
}
