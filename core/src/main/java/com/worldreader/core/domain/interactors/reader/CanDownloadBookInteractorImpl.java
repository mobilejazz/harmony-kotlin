package com.worldreader.core.domain.interactors.reader;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.qualifiers.DownloadedBooksPerDayLimit;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.date.Dates;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.helper.Provider;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.BookDownloaded;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

public class CanDownloadBookInteractorImpl extends AbstractInteractor<Boolean, ErrorCore<?>>
    implements CanDownloadBookInteractor {

  //private static final int NUMBER_DOWNLOADED_BOOKS_PER_DAY = 5;

  private final Provider<List<BookDownloaded>> bookDownloadedProvider;
  private final Dates dates;
  private final int downloadedBooksPerDayLimit;

  private DomainCallback<Boolean, ErrorCore<?>> callback;

  @Inject public CanDownloadBookInteractorImpl(final InteractorExecutor executor,
      final MainThread mainThread, final Provider<List<BookDownloaded>> bookDownloadedProvider,
      Dates dates, @DownloadedBooksPerDayLimit int downloadedBooksPerDayLimit) {
    super(executor, mainThread);
    this.bookDownloadedProvider = bookDownloadedProvider;
    this.dates = dates;
    this.downloadedBooksPerDayLimit = downloadedBooksPerDayLimit;
  }

  @Override public void execute(DomainCallback<Boolean, ErrorCore<?>> callback) {
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Boolean> execute() {
    final SettableFuture<Boolean> settableFuture = SettableFuture.create();

    getExecutor().execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        execute(new Callback<Boolean>() {
          @Override public void onSuccess(final Boolean result) {
            settableFuture.set(result);
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

  @Override public void run() {
    execute(new Callback<Boolean>() {
      @Override public void onSuccess(final Boolean result) {
        performSuccessCallback(callback, result);
      }

      @Override public void onError(final Throwable e) {
        performErrorCallback(callback, ErrorCore.of(e));
      }
    });

    //if (allBooksDownloaded.size() >= NUMBER_DOWNLOADED_BOOKS_PER_DAY) {
    //  // Sort the books downloaded by dates
    //  Collections.sort(allBooksDownloaded, new Comparator<BookDownloaded>() {
    //    @Override public int compare(BookDownloaded o1, BookDownloaded o2) {
    //      if (o1.getTimestamp() == null || o2.getTimestamp() == null) return 0;
    //      return o2.getTimestamp().compareTo(o1.getTimestamp());
    //    }
    //  });
    //
    //  BookDownloaded mostRecentBookDownloaded = allBooksDownloaded.get(0);
    //
    //  boolean isToday = dates.isToday(mostRecentBookDownloaded.getTimestamp());
    //  if (isToday) {
    //    List<BookDownloaded> latestBooksDownloaded =
    //        allBooksDownloaded.subList(0, NUMBER_DOWNLOADED_BOOKS_PER_DAY);
    //
    //    boolean isBookDownloadedToday = true;
    //    for (BookDownloaded bookDownloaded : latestBooksDownloaded) {
    //      if (!dates.isToday(bookDownloaded.getTimestamp())) {
    //        isBookDownloadedToday = false;
    //        break;
    //      }
    //    }
    //
    //    if (isBookDownloadedToday) {
    //      performSuccessCallback(callback, false);
    //    } else {
    //      performSuccessCallback(callback, true);
    //    }
    //  } else {
    //    performSuccessCallback(callback, true);
    //  }
    //
    //} else {
    //  performSuccessCallback(callback, true);
    //}
  }

  private void execute(Callback<Boolean> callback) {
    List<BookDownloaded> allBooksDownloaded = bookDownloadedProvider.get();

    final Collection<BookDownloaded> booksDownloadedToday =
        Collections2.filter(allBooksDownloaded, new Predicate<BookDownloaded>() {
          @Override public boolean apply(final BookDownloaded input) {
            return dates.isToday(input.getTimestamp());
          }
        });

    if (callback != null) {
      callback.onSuccess(downloadedBooksPerDayLimit < 0 || booksDownloadedToday.size() < downloadedBooksPerDayLimit);
    }
  }
}
