package com.worldreader.core.domain.interactors.reader;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.helper.Provider;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.BookDownloaded;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

public class GetBooksDownloadedInteractorImpl
    extends AbstractInteractor<List<BookDownloaded>, ErrorCore<?>>
    implements GetBooksDownloadedInteractor {

  private final Provider<List<BookDownloaded>> bookDownloadedProvider;

  private DomainBackgroundCallback<List<BookDownloaded>, ErrorCore<?>> callback;

  @Inject public GetBooksDownloadedInteractorImpl(final InteractorExecutor executor,
      final MainThread mainThread, final Provider<List<BookDownloaded>> bookDownloadedProvider) {
    super(executor, mainThread);
    this.bookDownloadedProvider = bookDownloadedProvider;
  }

  @Override
  public void execute(DomainBackgroundCallback<List<BookDownloaded>, ErrorCore<?>> callback) {
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Optional<List<BookDownloaded>>> execute() {
    return execute(this.executor.getExecutor());
  }

  @Override
  public ListenableFuture<Optional<List<BookDownloaded>>> execute(final Executor executor) {
    final SettableFuture<Optional<List<BookDownloaded>>> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final List<BookDownloaded> bookDownloadeds = bookDownloadedProvider.get();

        settableFuture.set(Optional.fromNullable(bookDownloadeds));
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }

  @Override public void run() {
    List<BookDownloaded> books = bookDownloadedProvider.get();

    if (callback != null) {
      callback.onSuccess(books);
    }
  }
}
