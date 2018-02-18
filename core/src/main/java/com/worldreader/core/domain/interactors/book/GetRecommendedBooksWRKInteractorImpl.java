package com.worldreader.core.domain.interactors.book;

import android.support.v4.util.ArraySet;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.Provider;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookSort;
import com.worldreader.core.domain.model.Category;
import com.worldreader.core.domain.repository.BookRepository;
import com.worldreader.core.domain.thread.MainThread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

public class GetRecommendedBooksWRKInteractorImpl extends AbstractInteractor<List<Book>, ErrorCore> implements GetRecommendedBooksWRKInteractor {

  private BookRepository bookRepository;
  private final Provider<String> localeProvider;

  private int offset;
  private int limit;
  private Book book;
  private DomainCallback<List<Book>, ErrorCore> callback;
  private Set<Category> rootCategories;

  @Inject public GetRecommendedBooksWRKInteractorImpl(InteractorExecutor executor, MainThread mainThread, BookRepository bookRepository,
                                                      @Named("locale.provider") final Provider<String> localeProvider) {
    super(executor, mainThread);
    this.bookRepository = bookRepository;
    this.localeProvider = localeProvider;
  }

  @Override public void execute(int offset, int limit, Book book, DomainCallback<List<Book>, ErrorCore> callback) {
     execute(offset,limit,book,callback, new ArraySet<Category>());
  }

  @Override public void execute(int offset, int limit, Book book, DomainCallback<List<Book>, ErrorCore> callback, Set<Category> rootCategories) {
    this.offset = offset;
    this.limit = limit;
    this.book = book;
    this.callback = callback;
    this.executor.run(this);
    this.rootCategories = rootCategories;
  }

  @Override public ListenableFuture<Optional<List<Book>>> execute(final Book book, final int offset, final int limit) {
    return execute(book, offset, limit, getExecutor(), new ArraySet<Category>());
  }

  @Override public ListenableFuture<Optional<List<Book>>> execute(final Book book, final int offset, final int limit, Executor executor) {
    return execute(book, offset, limit, executor, new ArraySet<Category>());
  }

  public ListenableFuture<Optional<List<Book>>> execute(final Book book, final int offset, final int limit, final String language, Executor executor) {
    return execute(book, offset, limit, language, executor, new ArraySet<Category>());
  }

  public ListenableFuture<Optional<List<Book>>> execute(final Book book, final int offset, final int limit, Executor executor,final Set<Category> rootCategories)  {
    return execute(book, offset, limit, null, executor, rootCategories);
  }

  public ListenableFuture<Optional<List<Book>>> execute(final Book book, final int offset, final int limit, Set<Category> rootCategories){
    return execute(book, offset, limit, null, getExecutor(), rootCategories);
  }

  @Override
  public ListenableFuture<Optional<List<Book>>> execute(final Book book, final int offset, final int limit, final String language, Executor executor, final Set<Category> rootCategories) {
    final SettableFuture<Optional<List<Book>>> settableFuture = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {
        execute(book, offset, limit, language, new Callback<List<Book>>() {
          @Override public void onSuccess(List<Book> books) {
            settableFuture.set(Optional.fromNullable(books));
          }

          @Override public void onError(Throwable e) {
            settableFuture.setException(e);
          }
        }, rootCategories);
      }
    });

    return settableFuture;
  }

  @Override public void run() {
    execute(book, offset, limit, null, new Callback<List<Book>>() {
      @Override public void onSuccess(List<Book> books) {
        performSuccessCallback(callback, books);
      }

      @Override public void onError(Throwable e) {
        performErrorCallback(callback, ErrorCore.of(e));
      }
    },rootCategories);
  }

  private Set<Integer> getCategoriesIds(Set<Category> categories){
    final Set<Integer> categoriesIds = new ArraySet<>();
    for (Category category : categories) {
      categoriesIds.add(category.getId());
    }
    return categoriesIds;
  }


  private void execute(Book book, int offset, int limit, String language, final Callback<List<Book>> callback, Set<Category> rootCategories) {
    final List<BookSort> bookSorts = Arrays.asList(BookSort.createBookSort(BookSort.Type.OPENS, BookSort.Value.DESC),
        BookSort.createBookSort(BookSort.Type.DATE, BookSort.Value.DESC));


    final List<Integer> filteredBookCategories;
    final Set<Integer> categoriesIds = new ArraySet<>(book.getCategories().size());
    for (Category category : book.getCategories()) {
      categoriesIds.add(category.getId());
    }

    if(!rootCategories.isEmpty()){
      final Sets.SetView<Integer> intersection = Sets.intersection(getCategoriesIds(rootCategories), categoriesIds);
      filteredBookCategories = new ArrayList<>(intersection);
    }else {
      filteredBookCategories = new ArrayList<>(categoriesIds);
    }

    bookRepository.books(filteredBookCategories, null /*list*/, bookSorts, false/*open country*/, language == null ? localeProvider.get() : language, offset, limit,
        new CompletionCallback<List<Book>>() {
          @Override public void onSuccess(List<Book> books) {
            if (callback != null) {
              callback.onSuccess(books);
            }
          }

          @Override public void onError(ErrorCore errorCore) {
            if (callback != null) {
              callback.onError(errorCore.getCause());
            }
          }
        });
  }
}
