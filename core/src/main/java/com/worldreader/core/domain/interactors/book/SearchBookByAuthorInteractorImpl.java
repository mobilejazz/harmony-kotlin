package com.worldreader.core.domain.interactors.book;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.repository.BookRepository;
import com.worldreader.core.domain.thread.MainThread;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class SearchBookByAuthorInteractorImpl extends AbstractInteractor<List<Book>, ErrorCore>
    implements SearchBookByAuthorInteractor {

  private BookRepository bookRepository;

  private int index;
  private int limit;
  private String query;
  private DomainCallback<List<Book>, ErrorCore> callback;

  @Inject
  public SearchBookByAuthorInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      BookRepository bookRepository) {
    super(executor, mainThread);
    this.bookRepository = bookRepository;
  }

  @Override public void execute(int index, int limit, String query,
      DomainCallback<List<Book>, ErrorCore> callback) {
    this.query = query;
    this.callback = callback;
    this.index = index;
    this.limit = limit;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Optional<List<Book>>> execute(final String query,
      final List<Integer> categories, final int index, final int limit) {
    final SettableFuture<Optional<List<Book>>> settableFuture = SettableFuture.create();

    getExecutor().execute(new Runnable() {
      @Override public void run() {
        execute(index, limit, query, categories, new Callback<List<Book>>() {
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
    execute(index, limit, query, null /*categories*/, new Callback<List<Book>>() {
      @Override public void onSuccess(List<Book> books) {
        performSuccessCallback(callback, books);
      }

      @Override public void onError(Throwable e) {
        performErrorCallback(callback, ErrorCore.of(e));
      }
    });
  }

  private void execute(final int index, final int limit, final String query,
      final List<Integer> categories, final Callback<List<Book>> callback) {
    bookRepository.search(index, limit, categories, null, query, null, Collections.<String>emptyList(), Collections.<String>emptyList(),
        new Callback<List<Book>>() {
          @Override public void onSuccess(List<Book> books) {
            if (callback != null) {
              callback.onSuccess(books);
            }
          }

          @Override public void onError(Throwable e) {
            if (callback != null) {
              callback.onError(e);
            }
          }
        });

    //bookRepository.searchBooks(index, limit, null/*title*/, query, null/*author*/,
    //    new CompletionCallback<List<Book>>() {
    //      @Override public void onSuccess(final List<Book> result) {
    //        if (callback != null) {
    //          callback.onSuccess(result);
    //        }
    //      }
    //
    //      @Override public void onError(final ErrorCore error) {
    //        if (callback != null) {
    //          callback.onError(error.getCause());
    //        }
    //      }
    //    });
  }
}
