package com.worldreader.core.domain.interactors.reader;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
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

  @Override public void run() {
    final List<BookDownloaded> allBooksDownloaded = bookDownloadedProvider.get();

    booksDownloaded = new ArrayList<>();

    if (allBooksDownloaded.size() > 0) {
      for (BookDownloaded bookDownloaded : allBooksDownloaded) {
        bookRepository.bookDetailLatest(bookDownloaded.getBookId(), false,
            new CompletionCallback<Book>() {
              @Override public void onSuccess(Book book) {
                booksDownloaded.add(book);

                if (booksDownloaded.size() >= allBooksDownloaded.size()) {
                  performSuccessCallback(callback, booksDownloaded);
                }
              }

              @Override public void onError(ErrorCore errorCore) {
                performErrorCallback(callback, errorCore);
              }
            });
      }
    } else {
      performSuccessCallback(callback, booksDownloaded);
    }
  }
}
