package com.worldreader.core.domain.interactors.reader;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.BookDownloaded;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

public class IsBookDownloadedInteractorImpl extends AbstractInteractor<Boolean, ErrorCore<?>>
    implements IsBookDownloadedInteractor {

  private final GetBooksDownloadedInteractor interactor;

  private String bookId;
  private DomainCallback<Boolean, ErrorCore<?>> callback;

  @Inject public IsBookDownloadedInteractorImpl(final InteractorExecutor executor,
      final MainThread mainThread, final GetBooksDownloadedInteractor interactor) {
    super(executor, mainThread);
    this.interactor = interactor;
  }

  @Override
  public void execute(final String bookId, DomainCallback<Boolean, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    interactor.execute(new DomainBackgroundCallback<List<BookDownloaded>, ErrorCore<?>>() {
      @Override public void onSuccess(List<BookDownloaded> bookDownloaded) {
        boolean isBookDownloaded = false;

        for (BookDownloaded book : bookDownloaded) {
          if (book.getBookId().equals(bookId)) {
            isBookDownloaded = true;
            break;
          }
        }

        performSuccessCallback(callback, isBookDownloaded);
      }

      @Override public void onError(ErrorCore<?> errorCore) {
        performErrorCallback(callback, errorCore);
      }
    });
  }
}
