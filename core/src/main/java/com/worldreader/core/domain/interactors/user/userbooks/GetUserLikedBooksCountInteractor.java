package com.worldreader.core.domain.interactors.user.userbooks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.domain.model.user.UserBook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class GetUserLikedBooksCountInteractor {

  private final ListeningExecutorService executor;
  private final InteractorHandler interactorHandler;
  private final GetUserLikedBooksInteractor getUserLikedBooksInteractor;

  @Inject public GetUserLikedBooksCountInteractor(ListeningExecutorService executor,
      InteractorHandler interactorHandler,
      GetUserLikedBooksInteractor getUserLikedBooksInteractor) {
    this.executor = executor;
    this.interactorHandler = interactorHandler;
    this.getUserLikedBooksInteractor = getUserLikedBooksInteractor;
  }

  public ListenableFuture<Integer> execute() {
    final SettableFuture<Integer> future = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {
        final ListenableFuture<List<UserBook>> likedFuture = getUserLikedBooksInteractor.execute();
        interactorHandler.addCallback(likedFuture, new FutureCallback<List<UserBook>>() {
          @Override public void onSuccess(@Nullable final List<UserBook> result) {
            final int size = result.size();
            future.set(size);
          }

          @Override public void onFailure(@NonNull final Throwable t) {
            future.setException(t);
          }
        });
      }
    });

    return future;
  }

}
