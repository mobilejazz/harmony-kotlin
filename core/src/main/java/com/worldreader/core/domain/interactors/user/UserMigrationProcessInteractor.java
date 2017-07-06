package com.worldreader.core.domain.interactors.user;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.worldreader.core.application.di.qualifiers.WorldreaderNetworkCacheDb;
import com.worldreader.core.application.helper.reachability.Reachability;
import com.worldreader.core.datasource.storage.datasource.cache.CacheBddDataSource;
import java.util.concurrent.Callable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserMigrationProcessInteractor {

  private final ListeningExecutorService executor;

  private final Reachability reachability;
  private final CacheBddDataSource dataSource;

  @Inject
  public UserMigrationProcessInteractor(@WorldreaderNetworkCacheDb final CacheBddDataSource dataSource, final ListeningExecutorService executor,
      final Reachability reachability) {
    this.dataSource = dataSource;
    this.executor = executor;
    this.reachability = reachability;
  }

  public ListenableFuture<Boolean> execute() {
    return executor.submit(getInteractorCallable());
  }

  private Callable<Boolean> getInteractorCallable() {
    return new Callable<Boolean>() {
      @Override public Boolean call() throws Exception {

        return null;
      }
    };
  }

}
