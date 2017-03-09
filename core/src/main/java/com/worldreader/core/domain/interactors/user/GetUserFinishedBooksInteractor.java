package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.interactors.user.userbooks.GetFinishedUserBooksInteractor;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.repository.BookRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class GetUserFinishedBooksInteractor {

  private final ListeningExecutorService executor;
  private final InteractorHandler interactorHandler;
  private final BookRepository bookRepository;
  private final GetFinishedUserBooksInteractor getFinishedUserBooksInteractor;

  @Inject public GetUserFinishedBooksInteractor(ListeningExecutorService executor,
      InteractorHandler interactorHandler, BookRepository bookRepository,
      GetFinishedUserBooksInteractor getFinishedUserBooksInteractor) {
    this.executor = executor;
    this.interactorHandler = interactorHandler;
    this.bookRepository = bookRepository;
    this.getFinishedUserBooksInteractor = getFinishedUserBooksInteractor;
  }

  public ListenableFuture<List<Book>> execute() {
    final SettableFuture<List<Book>> future = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {
        final ListenableFuture<List<UserBook>> finishedBooksFuture =
            getFinishedUserBooksInteractor.execute();

        interactorHandler.addCallback(finishedBooksFuture, new FutureCallback<List<UserBook>>() {
          @Override public void onSuccess(@Nullable final List<UserBook> result) {
            final List<String> finishedBooksIds = toBookIdList(result);
            final List<Book> fetchedBooks = Lists.newArrayListWithCapacity(finishedBooksIds.size());
            for (final String bookId : finishedBooksIds) {
              // This call is from the old BookRepository so is a complete sync call
              bookRepository.bookDetailLatest(bookId, false, new CompletionCallback<Book>() {
                @Override public void onSuccess(final Book result) {
                  if (result != null) {
                    fetchedBooks.add(result);
                  }
                }

                @Override public void onError(final ErrorCore error) {
                  future.setException(error.getCause());
                }
              });
            }
            future.set(Collections.unmodifiableList(fetchedBooks));
          }

          @Override public void onFailure(@NonNull final Throwable t) {
            future.setException(t);
          }
        });

      }
    });

    return future;
  }

  private List<String> toBookIdList(final List<UserBook> userBooks) {
    if (userBooks == null) {
      return Collections.emptyList();
    } else {
      final List<String> bookIds = Lists.newArrayListWithCapacity(userBooks.size());
      for (final UserBook userBook : userBooks) {
        bookIds.add(userBook.getBookId());
      }
      return Collections.unmodifiableList(bookIds);
    }
  }

}
