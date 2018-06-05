package com.worldreader.core.domain.interactors.user.userbooks;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.mobilejazz.kotlin.core.di.ActivityScope;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.repository.UserBooksRepository;
import com.worldreader.core.error.userbook.UserBookNotFoundException;
import java.util.concurrent.Executor;
import javax.inject.Inject;

@ActivityScope public class RemoveUserBookInteractor {

  private final ListeningExecutorService executorService;
  private final UserBooksRepository userBooksRepository;
  private final GetUserBookInteractor getUserBookInteractor;

  @Inject public RemoveUserBookInteractor(final ListeningExecutorService executorService,
      final UserBooksRepository userBooksRepository,
      final GetUserBookInteractor getUserBookInteractor) {
    this.executorService = executorService;
    this.userBooksRepository = userBooksRepository;
    this.getUserBookInteractor = getUserBookInteractor;
  }

  public ListenableFuture<Optional<UserBook>> execute(final String bookId) {
    return execute(bookId, executorService);
  }

  public ListenableFuture<Optional<UserBook>> execute(final String bookId,
      final Executor executor) {
    final SettableFuture<Optional<UserBook>> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final ListenableFuture<Optional<UserBook>> getUserBookFuture =
            getUserBookInteractor.execute(bookId, MoreExecutors.directExecutor());
        Futures.addCallback(getUserBookFuture, new FutureCallback<Optional<UserBook>>() {
          @Override public void onSuccess(final Optional<UserBook> result) {
            if (result.isPresent()) {
              final UserBook userBook = result.get();
              userBooksRepository.remove(userBook, RepositorySpecification.NONE,
                  new Callback<Optional<UserBook>>() {
                    @Override public void onSuccess(final Optional<UserBook> userBookOptional) {
                      settableFuture.set(userBookOptional);
                    }

                    @Override public void onError(final Throwable e) {
                      settableFuture.setException(e);
                    }
                  });
            } else {
              settableFuture.setException(new UserBookNotFoundException());
            }
          }

          @Override public void onFailure(final Throwable t) {
            settableFuture.setException(t);
          }
        }, MoreExecutors.directExecutor());
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }
}
