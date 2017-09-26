package com.worldreader.core.domain.interactors.reader;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.qualifiers.AddBookDownloaded;
import com.worldreader.core.common.date.Dates;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.helper.Action;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.BookDownloaded;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

public class AddBookDownloadedInteractorImpl extends AbstractInteractor<Boolean, ErrorCore<?>>
    implements AddBookDownloadedInteractor {

  private final Action<BookDownloaded, Boolean> addBookDownloadedAction;
  private final Dates dates;

  private String bookId;
  private Date time;
  private DomainCallback<Boolean, ErrorCore<?>> callback;

  @Inject public AddBookDownloadedInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      @AddBookDownloaded final Action<BookDownloaded, Boolean> addBookDownloadedAction, final Dates dates) {
    super(executor, mainThread);
    this.addBookDownloadedAction = addBookDownloadedAction;
    this.dates = dates;
  }

  @Override
  public void execute(String bookId, Date time, DomainCallback<Boolean, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.time = time;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Boolean> execute(final String bookId) {
    return execute(bookId, getExecutor());
  }

  @Override public ListenableFuture<Boolean> execute(final String bookId, final Executor executor) {
    final SettableFuture<Boolean> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final BookDownloaded bookDownloaded = BookDownloaded.create(bookId, dates.today());

        final boolean isAdded = addBookDownloadedAction.perform(bookDownloaded);

        settableFuture.set(isAdded);
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }

  @Override public void run() {
    BookDownloaded bookDownloaded = BookDownloaded.create(bookId, time);

    boolean isAdded = addBookDownloadedAction.perform(bookDownloaded);

    performSuccessCallback(callback, isAdded);
  }
}
