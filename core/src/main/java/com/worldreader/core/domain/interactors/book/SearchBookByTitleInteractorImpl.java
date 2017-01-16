package com.worldreader.core.domain.interactors.book;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.repository.BookRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

public class SearchBookByTitleInteractorImpl extends AbstractInteractor<List<Book>, ErrorCore>
    implements SearchBookByTitleInteractor {

  private BookRepository bookRepository;

  private int index;
  private int limit;
  private String query;
  private DomainCallback<List<Book>, ErrorCore> callback;

  @Inject public SearchBookByTitleInteractorImpl(InteractorExecutor executor, MainThread mainThread,
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

  @Override public void run() {
    bookRepository.searchBooks(index, limit, query, null/*author*/, null/*author*/,
        new CompletionCallback<List<Book>>() {
          @Override public void onSuccess(final List<Book> result) {
            performSuccessCallback(callback, result);
          }

          @Override public void onError(final ErrorCore error) {
            performErrorCallback(callback, error);
          }
        });
  }
}
