package com.worldreader.core.domain.interactors.book;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.Provider;
import com.worldreader.core.domain.helper.adapter.CategoryToIntAdapter;
import com.worldreader.core.domain.helper.adapter.ListAdapter;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookSort;
import com.worldreader.core.domain.model.Category;
import com.worldreader.core.domain.repository.BookRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@PerActivity public class GetBooksNewReleasesFromCategoriesInteractor {

  private final ListeningExecutorService listeningExecutorService;
  private final BookRepository bookRepository;
  private final Provider<String> localeProvider;

  private ListAdapter<Integer, Category> categoryToIntAdapter;

  @Inject public GetBooksNewReleasesFromCategoriesInteractor(
      ListeningExecutorService listeningExecutorService, BookRepository bookRepository,
      @Named("locale.provider") final Provider<String> localeProvider) {
    this.listeningExecutorService = listeningExecutorService;
    this.bookRepository = bookRepository;
    this.localeProvider = localeProvider;

    categoryToIntAdapter = new CategoryToIntAdapter();
  }

  public ListenableFuture<Optional<List<Book>>> execute(final List<Category> categories,
      final int offset, final int limit) {
    final SettableFuture<Optional<List<Book>>> settableFuture = SettableFuture.create();

    listeningExecutorService.execute(new Runnable() {
      @Override public void run() {

        List<BookSort> bookSorts = Collections.singletonList(
            BookSort.createBookSort(BookSort.Type.DATE, BookSort.Value.DESC));
        List<Integer> categoriesList = categoryToIntAdapter.transform(categories);

        bookRepository.books(categoriesList, null /*list*/, bookSorts, false /*openCountries*/,
            localeProvider.get(), offset, limit, new CompletionCallback<List<Book>>() {
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
