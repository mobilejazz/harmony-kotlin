package com.worldreader.core.domain.interactors.reader;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.Provider;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.BookDownloaded;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

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

  @Override public void run() {
    List<BookDownloaded> books = bookDownloadedProvider.get();

    if (callback != null) {
      callback.onSuccess(books);
    }
  }
}
