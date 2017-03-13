package com.worldreader.core.domain.interactors.application;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.Action;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

public class SaveSessionInteractorImpl extends AbstractInteractor<Boolean, ErrorCore<?>>
    implements SaveSessionInteractor {

  private final Action<Date, Boolean> addSessionAction;

  private Date date;
  private DomainCallback<Boolean, ErrorCore<?>> callback;

  @Inject
  public SaveSessionInteractorImpl(final InteractorExecutor executor, final MainThread mainThread,
      final Action<Date, Boolean> addSessionAction) {
    super(executor, mainThread);
    this.addSessionAction = addSessionAction;
  }

  @Override public void execute(Date date, DomainCallback<Boolean, ErrorCore<?>> callback) {
    this.date = date;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Boolean> execute(final Date date) {
    final Executor executor = getExecutor();
    final SettableFuture<Boolean> future = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {
        Preconditions.checkNotNull(date, "date == null");
        final boolean result = addSessionAction.perform(date);
        future.set(result);
      }
    });

    return future;
  }

  @Override public void run() {
    final boolean result = addSessionAction.perform(date);
    performSuccessCallback(callback, result);
    callback = null;
  }
}
