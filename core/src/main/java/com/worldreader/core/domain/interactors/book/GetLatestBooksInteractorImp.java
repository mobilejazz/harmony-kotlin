package com.worldreader.core.domain.interactors.book;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.Provider;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookSort;
import com.worldreader.core.domain.repository.BookRepository;
import com.worldreader.core.domain.thread.MainThread;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class GetLatestBooksInteractorImp extends AbstractInteractor<List<Book>, ErrorCore>
    implements GetLatestBooksInteractor {

  private final BookRepository bookRepository;
  private final Provider<String> localeProvider;

  private int index = -1;
  private int limit = -1;

  private DomainCallback<List<Book>, ErrorCore> callback;

  @Inject public GetLatestBooksInteractorImp(InteractorExecutor executor, MainThread mainThread, BookRepository bookRepository, @Named("locale.provider") final Provider<String> localeProvider) {
    super(executor, mainThread);
    this.bookRepository = bookRepository;
    this.localeProvider = localeProvider;
  }

  @Override public void execute(DomainCallback<List<Book>, ErrorCore> callback) {
    this.callback = callback;
    this.executor.run(this);
  }

  @Override
  public void execute(int index, int limit, DomainCallback<List<Book>, ErrorCore> callback) {
    this.index = index;
    this.limit = limit;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    List<BookSort> sorter =
        Collections.singletonList(BookSort.createBookSort(BookSort.Type.DATE, BookSort.Value.DESC));

    // Configure the get latest books request if we don't need pagination
    if (index < 0 && limit < 0) {
      index = 0;
      limit = 3;
    }

    bookRepository.books(null/*Categories*/, null/*List*/, sorter, false/*openCountry*/,
        localeProvider.get(), index, limit, new CompletionCallback<List<Book>>() {
          @Override public void onSuccess(List<Book> result) {
            performSuccessCallback(callback, result);
          }

          @Override public void onError(ErrorCore error) {
            performErrorCallback(callback, error);
          }
        });
  }
}
