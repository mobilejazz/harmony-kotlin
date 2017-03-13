package com.worldreader.core.domain.interactors.book;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.Action;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;

public class SaveBookCurrentlyReadingInteractorImpl extends AbstractInteractor<Boolean, ErrorCore<?>> implements SaveBookCurrentlyReadingInteractor {

  private final Action<Book, Boolean> putBookReadingAction;

  private Book book;
  private DomainCallback<Boolean, ErrorCore<?>> callback;

  @Inject public SaveBookCurrentlyReadingInteractorImpl(InteractorExecutor executor, MainThread mainThread, Action<Book, Boolean> action) {
    super(executor, mainThread);
    this.putBookReadingAction = action;
  }

  @Override public void execute(Book book, DomainCallback<Boolean, ErrorCore<?>> callback) {
    this.book = book;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    boolean result = putBookReadingAction.perform(book);
    performSuccessCallback(callback, result);
    callback = null;
  }
}
