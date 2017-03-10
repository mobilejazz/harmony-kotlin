package com.worldreader.core.domain.interactors.user.userbooks;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.repository.UserBooksRepository;
import com.worldreader.core.error.userbook.UserBookNotFoundException;

import javax.inject.Inject;
import java.util.concurrent.*;

@PerActivity public class RemoveUserBookInteractor {

  private final ListeningExecutorService executorService;
  private final UserBooksRepository userBooksRepository;
  private final GetUserBookInteractor getUserBookInteractor;
  private final InteractorHandler interactorHandler;

  @Inject public RemoveUserBookInteractor(final ListeningExecutorService executorService,
      final UserBooksRepository userBooksRepository,
      final GetUserBookInteractor getUserBookInteractor,
      final InteractorHandler interactorHandler) {
    this.executorService = executorService;
    this.userBooksRepository = userBooksRepository;
    this.getUserBookInteractor = getUserBookInteractor;
    this.interactorHandler = interactorHandler;
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
        interactorHandler.addCallback(getUserBookFuture, new FutureCallback<Optional<UserBook>>() {
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
