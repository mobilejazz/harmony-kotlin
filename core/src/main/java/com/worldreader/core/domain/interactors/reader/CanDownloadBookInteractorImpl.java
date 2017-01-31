package com.worldreader.core.domain.interactors.reader;

import com.worldreader.core.common.date.Dates;
import com.worldreader.core.common.deprecated.error.ErrorCore;
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

  private static final int NUMBER_DOWNLOADED_BOOKS_PER_DAY = 5;

  private final Provider<List<BookDownloaded>> bookDownloadedProvider;
  private final Dates dates;

  private DomainCallback<Boolean, ErrorCore<?>> callback;

  @Inject public CanDownloadBookInteractorImpl(final InteractorExecutor executor,
      final MainThread mainThread, final Provider<List<BookDownloaded>> bookDownloadedProvider,
      Dates dates) {
    super(executor, mainThread);
    this.bookDownloadedProvider = bookDownloadedProvider;
    this.dates = dates;
  }

  @Override public void execute(DomainCallback<Boolean, ErrorCore<?>> callback) {
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    List<BookDownloaded> allBooksDownloaded = bookDownloadedProvider.get();

    if (allBooksDownloaded.size() >= NUMBER_DOWNLOADED_BOOKS_PER_DAY) {
      // Sort the books downloaded by dates
      Collections.sort(allBooksDownloaded, new Comparator<BookDownloaded>() {
        @Override public int compare(BookDownloaded o1, BookDownloaded o2) {
          if (o1.getTimestamp() == null || o2.getTimestamp() == null) return 0;
          return o2.getTimestamp().compareTo(o1.getTimestamp());
        }
      });

      BookDownloaded mostRecentBookDownloaded = allBooksDownloaded.get(0);

      boolean isToday = dates.isToday(mostRecentBookDownloaded.getTimestamp());
      if (isToday) {
        List<BookDownloaded> latestBooksDownloaded =
            allBooksDownloaded.subList(0, NUMBER_DOWNLOADED_BOOKS_PER_DAY);

        boolean isBookDownloadedToday = true;
        for (BookDownloaded bookDownloaded : latestBooksDownloaded) {
          if (!dates.isToday(bookDownloaded.getTimestamp())) {
            isBookDownloadedToday = false;
            break;
          }
        }

        if (isBookDownloadedToday) {
          performSuccessCallback(callback, false);
        } else {
          performSuccessCallback(callback, true);
        }
      } else {
        performSuccessCallback(callback, true);
      }

    } else {
      performSuccessCallback(callback, true);
    }
  }
}
