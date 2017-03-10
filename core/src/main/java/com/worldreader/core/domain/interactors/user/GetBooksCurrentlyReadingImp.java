package com.worldreader.core.domain.interactors.user;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.application.helper.reachability.Reachability;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.spec.userbooks.GetAllUserBooksCurrentlyReadingStorageSpec;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.interactors.book.GetBookDetailInteractor;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.repository.BookRepository;
import com.worldreader.core.domain.repository.UserBooksRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

// TODO: 09/02/2017 @Jose we should refactor this method with the new ListenableFuture approach
public class GetBooksCurrentlyReadingImp extends AbstractInteractor<List<Book>, ErrorCore>
    implements GetBooksCurrentlyReading {

  private final BookRepository bookRepository;
  private final Reachability reachability;
  private final UserBooksRepository userBookRepository;
  private final GetBookDetailInteractor getBookDetailInteractor;
  private final InteractorHandler interactorHandler;

  private boolean allBooks;
  private int limit;
  private DomainCallback<List<Book>, ErrorCore> callback;

  @Inject public GetBooksCurrentlyReadingImp(InteractorExecutor executor, MainThread mainThread,
      BookRepository bookRepository, Reachability reachability,
      final UserBooksRepository userBookRepository,
      final GetBookDetailInteractor getBookDetailInteractor,
      final InteractorHandler interactorHandler) {
    super(executor, mainThread);
    this.bookRepository = bookRepository;
    this.reachability = reachability;
    this.userBookRepository = userBookRepository;
    this.getBookDetailInteractor = getBookDetailInteractor;
    this.interactorHandler = interactorHandler;
  }

  @Override
  public void execute(int limit, boolean allBooks, DomainCallback<List<Book>, ErrorCore> callback) {
    this.limit = limit;
    this.callback = callback;
    this.allBooks = allBooks;
    this.executor.run(this);
  }

  @Override public ListenableFuture<List<Book>> execute(final int limit, final boolean allBooks,
      final Executor executor) {
    final SettableFuture<List<Book>> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        execute(limit, allBooks, new Callback<List<Book>>() {
          @Override public void onSuccess(final List<Book> books) {
            settableFuture.set(books);
          }

          @Override public void onError(final Throwable e) {
            settableFuture.setException(e);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }

  @Override public ListenableFuture<List<Book>> execute() {
    return execute(Integer.MAX_VALUE, true, getExecutor());
  }

  @Override public void run() {
    execute(limit, allBooks, new Callback<List<Book>>() {
      @Override public void onSuccess(final List<Book> books) {
        performSuccessCallback(callback, books);
      }

      @Override public void onError(final Throwable e) {
        performErrorCallback(callback, ErrorCore.of(e));
      }
    });
  }

  private void execute(final int limit, final boolean allBooks,
      final Callback<List<Book>> callback) {
    GetAllUserBooksCurrentlyReadingStorageSpec spec =
        new GetAllUserBooksCurrentlyReadingStorageSpec();

    userBookRepository.getAll(spec, new Callback<Optional<List<UserBook>>>() {
      @Override public void onSuccess(final Optional<List<UserBook>> listOptional) {
        if (listOptional.isPresent()) {
          final List<UserBook> userBooks = listOptional.get();
          final List<String> booksId = Lists.transform(userBooks, new Function<UserBook, String>() {
            @Nullable @Override public String apply(@Nullable final UserBook input) {
              final String bookId = input.getBookId();
              return !TextUtils.isEmpty(bookId) ? bookId : "";
            }
          });

          final List<String> booksCurrentlyReading;
          if (allBooks) {
            booksCurrentlyReading = booksId;
          } else {
            if (booksId.size() >= limit) {
              booksCurrentlyReading = booksId.subList(0, limit);
            } else {
              booksCurrentlyReading = booksId;
            }
          }

          List<ListenableFuture<Optional<Book>>> booksLf =
              new ArrayList<>(booksCurrentlyReading.size());
          for (final String bookId : booksCurrentlyReading) {
            booksLf.add(getBookDetailInteractor.execute(bookId, MoreExecutors.directExecutor()));
          }

          final ListenableFuture<List<Optional<Book>>> combinerLf = Futures.allAsList(booksLf);

          interactorHandler.addCallback(combinerLf, new FutureCallback<List<Optional<Book>>>() {
            @Override public void onSuccess(@Nullable final List<Optional<Book>> result) {
              final List<Book> books =
                  Lists.transform(result, new Function<Optional<Book>, Book>() {
                    @Nullable @Override public Book apply(@Nullable final Optional<Book> input) {
                      return input.get();
                    }
                  });

              if (reachability.isReachable()) {
                if (callback != null) {
                  callback.onSuccess(Lists.newArrayList(books));
                }
              } else {
                final Collection<Book> booksFilterByDownload =
                    Collections2.filter(books, new Predicate<Book>() {
                      @Override public boolean apply(@Nullable final Book input) {
                        return input.isBookDownloaded();
                      }
                    });

                if (callback != null) {
                  callback.onSuccess(Lists.newArrayList(booksFilterByDownload));
                }
              }
            }

            @Override public void onFailure(final Throwable t) {
              if (callback != null) {
                callback.onError(t);
              }
            }
          }, MoreExecutors.directExecutor());
        } else {
          if (callback != null) {
            callback.onError(new Throwable());
          }
        }
      }

      @Override public void onError(final Throwable e) {
        if (callback != null) {
          callback.onError(e);
        }
      }
    });
  }
}
