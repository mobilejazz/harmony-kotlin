package com.worldreader.core.domain.interactors.book;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.mobilejazz.kotlin.core.di.ActivityScope;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.Provider;
import com.worldreader.core.domain.helper.adapter.CategoryToIntAdapter;
import com.worldreader.core.domain.helper.adapter.ListAdapter;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookSort;
import com.worldreader.core.domain.model.Category;
import com.worldreader.core.domain.repository.BookRepository;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Named;

@ActivityScope public class GetBooksNewReleasesFromCategoriesInteractor {

  private final ListeningExecutorService listeningExecutorService;
  private final BookRepository bookRepository;

  private final Provider<List<String>> localeProvider;
  private final Provider<List<String>> agesProvider;

  private ListAdapter<Integer, Category> categoryToIntAdapter;

  @Inject public GetBooksNewReleasesFromCategoriesInteractor(
      ListeningExecutorService listeningExecutorService,
      BookRepository bookRepository,
      @Named("locale.provider") final Provider<List<String>> localeProvider,
      @Named("ages.provider") final Provider<List<String>> agesProvider
  ) {
    this.listeningExecutorService = listeningExecutorService;
    this.bookRepository = bookRepository;
    this.localeProvider = localeProvider;
    this.agesProvider = agesProvider;
    categoryToIntAdapter = new CategoryToIntAdapter();
  }

  public ListenableFuture<Optional<List<Book>>> execute(final List<Category> categories, final int offset, final int limit) {
    return execute(categories, offset, limit, listeningExecutorService);
  }

  public ListenableFuture<Optional<List<Book>>> execute(final List<Category> categories, final int offset, final int limit, final Executor executor) {
    return execute(categories, offset, limit, null, executor);
  }

  public ListenableFuture<Optional<List<Book>>> execute(final List<Category> categories, final int offset, final int limit, final String language, final Executor executor) {
    final SettableFuture<Optional<List<Book>>> settableFuture = SettableFuture.create();

    final List<String> languages = language == null ? localeProvider.get() : Collections.singletonList(language);
    final List<String> ages = agesProvider.get();

    executor.execute(new Runnable() {
      @Override public void run() {

        List<BookSort> bookSorts = Collections.singletonList(BookSort.createBookSort(BookSort.Type.DATE, BookSort.Value.DESC));
        List<Integer> categoriesList = categoryToIntAdapter.transform(categories);

        bookRepository.books(categoriesList, null, bookSorts, false, languages, ages, offset, limit, new CompletionCallback<List<Book>>() {
              @Override public void onSuccess(final List<Book> result) {
                Optional<List<Book>> booksOp =
                    result == null ? Optional.<List<Book>>absent() : Optional.of(result);

                settableFuture.set(booksOp);
              }

              @Override public void onError(final ErrorCore error) {
                settableFuture.setException(error.getCause());
              }
            });
      }
    });

    return settableFuture;
  }
}
