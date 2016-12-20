package com.worldreader.core.domain.interactors.book;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.helper.DefaultValues;
import com.worldreader.core.domain.helper.adapter.CategoryToIntAdapter;
import com.worldreader.core.domain.helper.adapter.ListAdapter;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookSort;
import com.worldreader.core.domain.model.Category;
import com.worldreader.core.domain.repository.BookRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

public class GetMostPopularBooksInteractorImpl extends AbstractInteractor<List<Book>, ErrorCore>
    implements GetMostPopularBooksInteractor {

  private final BookRepository bookRepository;

  private ListAdapter<Integer, Category> categoryToIntAdapter;

  private int offset;
  private int limit;
  private List<Category> categories;

  private DomainCallback<List<Book>, ErrorCore> callback;

  @Inject
  public GetMostPopularBooksInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      BookRepository bookRepository) {
    super(executor, mainThread);
    this.bookRepository = bookRepository;
    this.categoryToIntAdapter = new CategoryToIntAdapter();
  }

  @Override public void execute(int offset, int limit, List<Category> categories,
      DomainCallback<List<Book>, ErrorCore> callback) {
    this.offset = offset;
    this.limit = limit;
    this.categories = categories;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    List<Integer> categoriesInt = categoryToIntAdapter.transform(this.categories);
    List<BookSort> sortedBy =
        Arrays.asList(BookSort.createBookSort(BookSort.Type.OPENS, BookSort.Value.DESC),
            BookSort.createBookSort(BookSort.Type.DATE, BookSort.Value.DESC));

    bookRepository.books(categoriesInt, null /*list*/, sortedBy, true,
        DefaultValues.DEFAULT_LANGUAGE, offset, limit, new CompletionCallback<List<Book>>() {
          @Override public void onSuccess(final List<Book> result) {
            performSuccessCallback(callback, result);
          }

          @Override public void onError(final ErrorCore error) {
            performErrorCallback(callback, error);
          }
        });
  }
}
