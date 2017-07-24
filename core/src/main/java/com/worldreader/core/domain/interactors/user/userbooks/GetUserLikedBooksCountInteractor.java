package com.worldreader.core.domain.interactors.user.userbooks;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.model.user.UserBook;
import java.util.List;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class GetUserLikedBooksCountInteractor {

  private final ListeningExecutorService executor;
  private final GetUserLikedBooksInteractor getUserLikedBooksInteractor;

  @Inject public GetUserLikedBooksCountInteractor(ListeningExecutorService executor, GetUserLikedBooksInteractor getUserLikedBooksInteractor) {
    this.executor = executor;
    this.getUserLikedBooksInteractor = getUserLikedBooksInteractor;
  }

  public ListenableFuture<Integer> execute() {
    final SettableFuture<Integer> future = SettableFuture.create();
    executor.execute(getInteractorCallalbe(future));
    return future;
  }

  public ListenableFuture<Integer> execute(Executor executor) {
    final SettableFuture<Integer> future = SettableFuture.create();
    executor.execute(getInteractorCallalbe(future));
    return future;
  }

  private Runnable getInteractorCallalbe(final SettableFuture<Integer> future) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final List<UserBook> userBooks = getUserLikedBooksInteractor.execute(MoreExecutors.directExecutor()).get();
        future.set(userBooks.size());
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    };
  }

}
