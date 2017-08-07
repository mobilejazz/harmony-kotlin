package com.worldreader.core.domain.interactors.application;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.datasource.helper.Action;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SaveSessionInteractor {

  private final ListeningExecutorService executor;
  private final Action<Date, Boolean> addSessionAction;

  @Inject public SaveSessionInteractor(final ListeningExecutorService executor1, final Action<Date, Boolean> addSessionAction) {
    this.executor = executor1;
    this.addSessionAction = addSessionAction;
  }

  public ListenableFuture<Boolean> execute(final Date date) {
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
}
