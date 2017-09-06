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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

@PerActivity public class GetMostPopularBooksInteractor {

  private final ListeningExecutorService executor;
  private final BookRepository bookRepository;
  private final Provider<String> localeProvider;
  private final ListAdapter<Integer, Category> categoryToIntAdapter;

  @Inject public GetMostPopularBooksInteractor(final ListeningExecutorService executor, final BookRepository bookRepository,
                                               @Named("locale.provider") final Provider<String> localeProvider) {
    this.executor = executor;
    this.bookRepository = bookRepository;
    this.localeProvider = localeProvider;
    this.categoryToIntAdapter = new CategoryToIntAdapter();
  }

  public ListenableFuture<Optional<List<Book>>> execute(final List<Category> categories, final int offset, final int limit) {
    return execute(categories, offset, limit, executor);
  }

  public ListenableFuture<Optional<List<Book>>> execute(final List<Category> categories, final int offset, final int limit, Executor executor) {
    final SettableFuture<Optional<List<Book>>> future = SettableFuture.create();
    executor.execute(getInteractorCallable(categories, offset, limit, future));
    return future;
  }

  private Runnable getInteractorCallable(final List<Category> categories, final int offset, final int limit,
                                         final SettableFuture<Optional<List<Book>>> future) {
    return new Runnable() {
      @Override public void run() {
        final List<Integer> categoriesInt = categoryToIntAdapter.transform(categories);
        final List<BookSort> sortedBy = Arrays.asList(BookSort.createBookSort(BookSort.Type.OPENS, BookSort.Value.DESC),
            BookSort.createBookSort(BookSort.Type.DATE, BookSort.Value.DESC));

        bookRepository.books(categoriesInt, null /*list*/, sortedBy, true, localeProvider.get(), offset, limit, new CompletionCallback<List<Book>>() {
          @Override public void onSuccess(final List<Book> result) {
            future.set(Optional.fromNullable(result));
          }

          @Override public void onError(final ErrorCore error) {
            future.setException(error.getCause());
          }
        });
      }
    };
  }
}
