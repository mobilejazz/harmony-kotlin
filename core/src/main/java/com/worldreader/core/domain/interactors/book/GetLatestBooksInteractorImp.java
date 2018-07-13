package com.worldreader.core.domain.interactors.book;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.Provider;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.helper.adapter.ListAdapter;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookSort;
import com.worldreader.core.domain.model.Category;
import com.worldreader.core.domain.repository.BookRepository;
import com.worldreader.core.domain.thread.MainThread;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Named;

public class GetLatestBooksInteractorImp extends AbstractInteractor<List<Book>, ErrorCore>
    implements GetLatestBooksInteractor {

  private final BookRepository bookRepository;

  private final Provider<List<String>> localeProvider;
  private final Provider<List<String>> agesProvider;

  private final ListAdapter<Integer, Category> categoryToIntAdapter;

  private int offset;
  private int limit;

  private DomainCallback<List<Book>, ErrorCore> callback;

  @Inject public GetLatestBooksInteractorImp(
      InteractorExecutor executor,
      MainThread mainThread,
      BookRepository bookRepository,
      ListAdapter<Integer, Category> categoryToIntAdapter,
      @Named("locale.provider") final Provider<List<String>> localeProvider,
      @Named("ages.provider") final Provider<List<String>> agesProvider
  ) {
    super(executor, mainThread);
    this.bookRepository = bookRepository;
    this.categoryToIntAdapter = categoryToIntAdapter;
    this.localeProvider = localeProvider;
    this.agesProvider = agesProvider;
  }

  @Override public void execute(DomainCallback<List<Book>, ErrorCore> callback) {
    this.callback = callback;
    this.executor.run(this);
  }

  @Override
  public void execute(int index, int limit, DomainCallback<List<Book>, ErrorCore> callback) {
    this.offset = index;
    this.limit = limit;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<List<Book>> execute(final int offset, final int limit, final List<Category> categories, final Executor executor) {
    final SettableFuture<List<Book>> settableFuture = SettableFuture.create();
    executor.execute(getInteractorCallable(offset, limit, categories, settableFuture));
    return settableFuture;
  }

  @Override public void run() {
    List<BookSort> sorter = Collections.singletonList(BookSort.createBookSort(BookSort.Type.DATE, BookSort.Value.DESC));

    // Configure the get latest books request if we don't need pagination
    if (offset < 0 && limit < 0) {
      offset = 0;
      limit = 3;
    }

    final List<String> languages = localeProvider.get();
    final List<String> ages = agesProvider.get();

    bookRepository.books(null, null, sorter, false, languages, ages, offset, limit, new CompletionCallback<List<Book>>() {
      @Override public void onSuccess(List<Book> result) {
        performSuccessCallback(callback, result);
      }

      @Override public void onError(ErrorCore error) {
        performErrorCallback(callback, error);
      }
    });
  }

  private Runnable getInteractorCallable(final int offset, final int limit, final List<Category> categories, final SettableFuture<List<Book>> future) {
    return new Runnable() {
      @Override public void run() {
        List<BookSort> sorter = Collections.singletonList(BookSort.createBookSort(BookSort.Type.DATE, BookSort.Value.DESC));
        final List<Integer> categoriesToFetch = (categories != null && !categories.isEmpty()) ? categoryToIntAdapter.transform(categories) : null;

        final List<String> languages = localeProvider.get();
        final List<String> ages = agesProvider.get();

        bookRepository.books(categoriesToFetch, null, sorter, false, languages, ages, offset, limit, new CompletionCallback<List<Book>>() {
          @Override public void onSuccess(List<Book> result) {
            future.set(result);
          }

          @Override public void onError(ErrorCore error) {
            future.setException(error.getCause());
          }
        });
      }
    };
  }
}
