package com.worldreader.core.domain.interactors.reader;

import com.worldreader.core.application.di.qualifiers.AddBookDownloaded;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.Action;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.BookDownloaded;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

public class AddBookDownloadedInteractorImpl extends AbstractInteractor<Boolean, ErrorCore<?>>
    implements AddBookDownloadedInteractor {

  private final Action<BookDownloaded> addBookDownloadedAction;

  private String bookId;
  private Date time;
  private DomainCallback<Boolean, ErrorCore<?>> callback;

  @Inject public AddBookDownloadedInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      @AddBookDownloaded final Action<BookDownloaded> addBookDownloadedAction) {
    super(executor, mainThread);
    this.addBookDownloadedAction = addBookDownloadedAction;
  }

  @Override
  public void execute(String bookId, Date time, DomainCallback<Boolean, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.time = time;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    BookDownloaded bookDownloaded = BookDownloaded.create(bookId, time);

    boolean isAdded = addBookDownloadedAction.perform(bookDownloaded);

    performSuccessCallback(callback, isAdded);
  }
}
