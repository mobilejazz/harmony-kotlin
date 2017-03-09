package com.worldreader.core.domain.interactors.user.userbooks;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.spec.userbooks.GetAllUserBooksFinishedStorageSpec;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.repository.UserBooksRepository;
import com.worldreader.core.error.general.UnexpectedErrorException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.*;

@Singleton public class GetFinishedUserBooksInteractor {

  private final ListeningExecutorService executor;
  private final UserBooksRepository repository;

  @Inject public GetFinishedUserBooksInteractor(ListeningExecutorService executor,
      UserBooksRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<List<UserBook>> execute(final Executor executor) {
    final SettableFuture<List<UserBook>> future = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {
        final GetAllUserBooksFinishedStorageSpec spec = new GetAllUserBooksFinishedStorageSpec();

        repository.getAll(spec, new Callback<Optional<List<UserBook>>>() {
          @Override public void onSuccess(final Optional<List<UserBook>> optional) {
            if (optional.isPresent()) {
              final List<UserBook> userBooks = optional.get();
              final List<UserBook> toReturn = Collections.unmodifiableList(userBooks);
              future.set(toReturn);
            } else {
              future.setException(new UnexpectedErrorException("List UserBook is not defined"));
            }
          }

          @Override public void onError(final Throwable e) {
            future.setException(e);
          }
        });
      }
    });

    return future;
  }

  public ListenableFuture<List<UserBook>> execute() {
    return execute(executor);
  }
}
