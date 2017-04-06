package com.worldreader.core.domain.interactors.user.userbookslike;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.repository.spec.NetworkSpecification;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.repository.spec.StorageSpecification;
import com.worldreader.core.domain.model.user.UserBookLike;
import com.worldreader.core.domain.repository.UserBooksLikeRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

@Singleton public class PutAllUserBooksLikesInteractor {

  private final ListeningExecutorService executor;
  private final UserBooksLikeRepository repository;

  @Inject public PutAllUserBooksLikesInteractor(final ListeningExecutorService executor, final UserBooksLikeRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<List<UserBookLike>> execute(final List<UserBookLike> userBookLikes, final StorageSpecification spec) {
    return doExecute(executor, userBookLikes, spec);
  }

  public ListenableFuture<List<UserBookLike>> execute(final List<UserBookLike> userBookLikes, final StorageSpecification spec,
      final Executor executor) {
    return doExecute(executor, userBookLikes, spec);
  }

  public ListenableFuture<List<UserBookLike>> execute(final List<UserBookLike> userBookLikes, final NetworkSpecification spec) {
    return doExecute(executor, userBookLikes, spec);
  }

  public ListenableFuture<List<UserBookLike>> execute(final List<UserBookLike> userBookLikes, final NetworkSpecification spec,
      final Executor executor) {
    return doExecute(executor, userBookLikes, spec);
  }

  private ListenableFuture<List<UserBookLike>> doExecute(final Executor executor, final List<UserBookLike> userBookLikes,
      final RepositorySpecification spec) {
    final SettableFuture<List<UserBookLike>> future = SettableFuture.create();
    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        repository.putAll(userBookLikes, spec, new Callback<Optional<List<UserBookLike>>>() {
          @Override public void onSuccess(final Optional<List<UserBookLike>> optional) {
            if (!optional.isPresent()) {
              future.set(Collections.<UserBookLike>emptyList());
            } else {
              final List<UserBookLike> bookLikes = optional.get();
              future.set(bookLikes);
            }
          }

          @Override public void onError(final Throwable e) {
            future.setException(e);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    });
    return future;
  }

}
