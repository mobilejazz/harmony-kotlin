package com.worldreader.core.domain.interactors.book;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
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

public class GetBooksFeaturedImp extends AbstractInteractor<List<Book>, ErrorCore> implements GetBooksFeatured {

  private final BookRepository bookRepository;

  private int index;
  private int limit;
  private DomainCallback<List<Book>, ErrorCore> callback;

  @Inject public GetBooksFeaturedImp(InteractorExecutor executor, MainThread mainThread, BookRepository bookRepository) {
    super(executor, mainThread);
    this.bookRepository = bookRepository;
  }

  @Override
  public void execute(int index, int limit, DomainCallback<List<Book>, ErrorCore> callback) {
    this.callback = callback;
    this.index = index;
    this.limit = limit;
    this.executor.run(this);
  }

  @Override public void run() {
    final List<BookSort> bookSorts = Collections.singletonList(BookSort.createBookSort(BookSort.Type.DATE, BookSort.Value.DESC));
    final List<String> languages = Collections.emptyList();
    final List<String> ages = Collections.emptyList();

    bookRepository.books(null, BookRepository.KEY_LIST_FEATURED, bookSorts, false, languages, ages, index, limit, new CompletionCallback<List<Book>>() {
      @Override public void onSuccess(final List<Book> result) {
        performSuccessCallback(callback, result);
      }

      @Override public void onError(final ErrorCore error) {
        performErrorCallback(callback, error);
      }
    });
  }
}
