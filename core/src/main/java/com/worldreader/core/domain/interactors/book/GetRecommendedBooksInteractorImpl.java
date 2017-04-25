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
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookSort;
import com.worldreader.core.domain.model.Category;
import com.worldreader.core.domain.repository.BookRepository;
import com.worldreader.core.domain.thread.MainThread;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class GetRecommendedBooksInteractorImpl extends AbstractInteractor<List<Book>, ErrorCore>
    implements GetRecommendedBooksInteractor {

  private BookRepository bookRepository;
  private final Provider<String> localeProvider;

  private int offset;
  private int limit;
  private Book book;
  private DomainCallback<List<Book>, ErrorCore> callback;

  @Inject
  public GetRecommendedBooksInteractorImpl(InteractorExecutor executor, MainThread mainThread, BookRepository bookRepository, @Named("locale.provider") final Provider<String> localeProvider) {
    super(executor, mainThread);
    this.bookRepository = bookRepository;
    this.localeProvider = localeProvider;
  }

  @Override public void execute(int offset, int limit, Book book,
      DomainCallback<List<Book>, ErrorCore> callback) {
    this.offset = offset;
    this.limit = limit;
    this.book = book;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Optional<List<Book>>> execute(final Book book, final int offset,
      final int limit) {
    final SettableFuture<Optional<List<Book>>> settableFuture = SettableFuture.create();

    getExecutor().execute(new Runnable() {
      @Override public void run() {
        execute(book, offset, limit, new Callback<List<Book>>() {
          @Override public void onSuccess(List<Book> books) {
            settableFuture.set(Optional.fromNullable(books));
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
    execute(book, offset, limit, new Callback<List<Book>>() {
      @Override public void onSuccess(List<Book> books) {
        performSuccessCallback(callback, books);
      }

      @Override public void onError(Throwable e) {
        performErrorCallback(callback, ErrorCore.of(e));
      }
    });
  }

  private void execute(Book book, int offset, int limit, final Callback<List<Book>> callback) {
    final List<BookSort> bookSorts =
        Arrays.asList(BookSort.createBookSort(BookSort.Type.OPENS, BookSort.Value.DESC),
            BookSort.createBookSort(BookSort.Type.DATE, BookSort.Value.DESC));

    final String language = localeProvider.get();

    final List<Integer> categoriesIds = new ArrayList<>(book.getCategories().size());
    for (Category category : book.getCategories()) {
      categoriesIds.add(category.getId());
    }

    bookRepository.books(categoriesIds, null /*list*/, bookSorts, false/*open country*/, language,
        offset, limit, new CompletionCallback<List<Book>>() {
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
