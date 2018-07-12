package com.worldreader.core.domain.interactors.book;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.Provider;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.helper.adapter.CategoryToIntAdapter;
import com.worldreader.core.domain.helper.adapter.ListAdapter;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookSort;
import com.worldreader.core.domain.model.Category;
import com.worldreader.core.domain.repository.BookRepository;
import com.worldreader.core.domain.thread.MainThread;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Named;

public class GetHighestRatedBooksInteractorImpl extends AbstractInteractor<List<Book>, ErrorCore>
    implements GetHighestRatedBooksInteractor {

  private final BookRepository bookRepository;

  private final Provider<List<String>> localeProvider;
  private final Provider<List<String>> agesProvider;

  private ListAdapter<Integer, Category> categoryToIntAdapter;

  private int offset;
  private int limit;
  private List<Category> categories;

  private DomainCallback<List<Book>, ErrorCore> callback;

  @Inject
  public GetHighestRatedBooksInteractorImpl(
      InteractorExecutor executor,
      MainThread mainThread,
      BookRepository bookRepository,
      @Named("locale.provider") final Provider<List<String>> localeProvider,
      @Named("ages.provider") final Provider<List<String>> agesProvider
  ) {
    super(executor, mainThread);
    this.bookRepository = bookRepository;
    this.categoryToIntAdapter = new CategoryToIntAdapter();
    this.localeProvider = localeProvider;
    this.agesProvider = agesProvider;
  }

  @Override public void execute(int offset, int limit, List<Category> categories, DomainCallback<List<Book>, ErrorCore> callback) {
    this.offset = offset;
    this.limit = limit;
    this.categories = categories;
    this.callback = callback;

    this.executor.run(this);
  }

  @Override public ListenableFuture<Optional<List<Book>>> execute(final List<Category> categories, final int offset, final int limit) {
    return this.execute(categories, offset, limit, getExecutor());
  }

  @Override public ListenableFuture<Optional<List<Book>>> execute(final List<Category> categories, final int offset, final int limit, Executor executor) {
    return execute(categories, offset, limit, null, executor);
  }

  @Override public ListenableFuture<Optional<List<Book>>> execute(final List<Category> categories, final int offset, final int limit, final String language,
      Executor executor) {
    final SettableFuture<Optional<List<Book>>> settableFuture = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {
        execute(categories, offset, limit, language, new Callback<List<Book>>() {
          @Override public void onSuccess(List<Book> books) {
            Optional<List<Book>> booksOp =
                books == null ? Optional.<List<Book>>absent() : Optional.of(books);

            settableFuture.set(booksOp);
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
    execute(categories, offset, limit, null, new Callback<List<Book>>() {
      @Override public void onSuccess(List<Book> books) {
        performSuccessCallback(callback, books);
      }

      @Override public void onError(Throwable e) {
        performErrorCallback(callback, ErrorCore.of(e));
      }
    });
  }

  //region Private methods
  private void execute(final List<Category> categories, final int offset, final int limit, String language, final Callback<List<Book>> callback) {
    final List<String> languages = language == null ? localeProvider.get() : Collections.singletonList(language);
    final List<String> ages = agesProvider.get();

    List<Integer> categoriesInt = categoryToIntAdapter.transform(categories);
    List<BookSort> sortedBy =
        Arrays.asList(BookSort.createBookSort(BookSort.Type.SCORE, BookSort.Value.DESC), BookSort.createBookSort(BookSort.Type.DATE, BookSort.Value.DESC));

    bookRepository.books(categoriesInt, null, sortedBy, false, languages, ages, offset, limit, new CompletionCallback<List<Book>>() {
      @Override public void onSuccess(final List<Book> result) {
        if (callback != null) {
          callback.onSuccess(result);
        }
      }

      @Override public void onError(final ErrorCore error) {
        if (callback != null) {
          callback.onError(error.getCause());
        }
      }
    });
  }
  //endregion
}
