package com.worldreader.core.domain.interactors.user;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.worldreader.core.datasource.storage.datasource.cache.CacheBddDataSource;
import java.util.concurrent.Callable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class ShouldUserMigrateInteractor {

  private final CacheBddDataSource dataSource;

  private final ListeningExecutorService executor;

  @Inject public ShouldUserMigrateInteractor(final CacheBddDataSource dataSource, final ListeningExecutorService executor) {
    this.dataSource = dataSource;
    this.executor = executor;
  }

  public ListenableFuture<Boolean> execute() {
    return executor.submit(new Callable<Boolean>() {
      @Override public Boolean call() throws Exception {
        return dataSource.get("TEST") != null;
      }
    });
  }

}
