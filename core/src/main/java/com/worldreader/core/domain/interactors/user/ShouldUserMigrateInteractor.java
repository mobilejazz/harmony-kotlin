package com.worldreader.core.domain.interactors.user;

import android.content.Context;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.worldreader.core.application.di.annotation.PerActivity;

import javax.inject.Inject;
import java.io.*;
import java.util.concurrent.*;

@PerActivity public class ShouldUserMigrateInteractor {

  private static final String OLD_DB_NAME = "app.worldreader.cache.db";

  private final ListeningExecutorService executor;

  @Inject public ShouldUserMigrateInteractor(final ListeningExecutorService executor) {
    this.executor = executor;
  }

  public ListenableFuture<Boolean> execute(final Context context) {
    return executor.submit(new Callable<Boolean>() {
      @Override public Boolean call() throws Exception {
        final File oldUserDBFile = context.getDatabasePath(OLD_DB_NAME);
        return oldUserDBFile.exists();
      }
    });
  }

}
