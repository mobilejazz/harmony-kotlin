package com.worldreader.core.domain.interactors.user.score;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.spec.score.DeleteUnSyncedUserScoreStorageSpecification;
import com.worldreader.core.domain.repository.UserScoreRepository;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class RemoveAllUserScoreInteractor {

  private final ListeningExecutorService executor;
  private final UserScoreRepository repository;

  @Inject public RemoveAllUserScoreInteractor(final ListeningExecutorService executor, final UserScoreRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<Boolean> execute() {
    return execute(executor);
  }

  public ListenableFuture<Boolean> execute(final Executor executor) {
    final SettableFuture<Boolean> future = SettableFuture.create();
    executor.execute(getInteractorCallable(future));
    return future;
  }

  private Runnable getInteractorCallable(final SettableFuture<Boolean> future) {
    return new Runnable() {
      @Override public void run() {
        final DeleteUnSyncedUserScoreStorageSpecification spec = new DeleteUnSyncedUserScoreStorageSpecification();
        repository.removeAll(spec, new Callback<Void>() {
          @Override public void onSuccess(final Void aVoid) {
            future.set(true);
          }

          @Override public void onError(final Throwable e) {
            future.setException(e);
          }
        });
      }
    };
  }

}
