package com.worldreader.core.domain.interactors.user.userbooks;

import android.support.annotation.NonNull;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.userbooks.UserBookNetworkSpecification;
import com.worldreader.core.datasource.spec.userbooks.UserBookStorageSpecification;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.repository.UserBooksRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.*;

@Singleton public class PutAllUserBooksInteractor {

  private final ListeningExecutorService executor;
  private final UserBooksRepository repository;

  @Inject public PutAllUserBooksInteractor(ListeningExecutorService executor,
      UserBooksRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<Optional<List<UserBook>>> execute(
      final UserBookStorageSpecification specification, final List<UserBook> userBooks) {
    final SettableFuture<Optional<List<UserBook>>> settableFuture = SettableFuture.create();
    executor.execute(getInteractorRunnable(specification, userBooks, settableFuture));
    return settableFuture;
  }

  public ListenableFuture<Optional<List<UserBook>>> execute(
      final UserBookStorageSpecification specification, final List<UserBook> userBooks,
      final Executor executor) {
    final SettableFuture<Optional<List<UserBook>>> settableFuture = SettableFuture.create();
    executor.execute(getInteractorRunnable(specification, userBooks, settableFuture));
    return settableFuture;
  }

  public ListenableFuture<Optional<List<UserBook>>> execute(
      final UserBookNetworkSpecification specification, final List<UserBook> userBooks) {
    final SettableFuture<Optional<List<UserBook>>> settableFuture = SettableFuture.create();
    executor.execute(getInteractorPutAllNetworkRunnable(specification, userBooks, settableFuture));
    return settableFuture;
  }

  public ListenableFuture<Optional<List<UserBook>>> execute(
      final UserBookNetworkSpecification specification, final List<UserBook> userBooks,
      final Executor executor) {
    final SettableFuture<Optional<List<UserBook>>> settableFuture = SettableFuture.create();
    executor.execute(getInteractorPutAllNetworkRunnable(specification, userBooks, settableFuture));
    return settableFuture;
  }

  @NonNull private Runnable getInteractorRunnable(final RepositorySpecification specification,
      final List<UserBook> userBooks,
      final SettableFuture<Optional<List<UserBook>>> settableFuture) {
    return new Runnable() {
      @Override public void run() {
        Preconditions.checkNotNull(specification, "specification != null");
        repository.putAll(userBooks, specification, new Callback<Optional<List<UserBook>>>() {
          @Override public void onSuccess(final Optional<List<UserBook>> response) {
            settableFuture.set(response);
          }

          @Override public void onError(final Throwable e) {
            settableFuture.setException(e);
          }
        });
      }
    };
  }

  @NonNull
  private Runnable getInteractorPutAllNetworkRunnable(final RepositorySpecification specification,
      final List<UserBook> userBooks,
      final SettableFuture<Optional<List<UserBook>>> settableFuture) {
    return new Runnable() {
      @Override public void run() {
        Preconditions.checkNotNull(specification, "specification != null");
        repository.putAll(userBooks, specification, new Callback<Optional<List<UserBook>>>() {
          @Override public void onSuccess(final Optional<List<UserBook>> response) {
            settableFuture.set(response);
          }

          @Override public void onError(final Throwable e) {
            settableFuture.setException(e);
          }
        });
      }
    };
  }

}