package com.worldreader.core.domain.interactors.user;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.spec.userbooks.GetAllUserBooksFinishedStorageSpec;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.repository.UserBooksRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

// TODO: 09/02/2017 @jose Needs to be tested when the server support the collections ids.
public class GetFinishedBooksCountInteractorImpl extends AbstractInteractor<Integer, ErrorCore> implements GetFinishedBooksCountInteractor {

  private static final String NONE_COLLECTION_ID = String.valueOf(0);

  private final UserBooksRepository userBooksRepository;
  private final GetCollectionIdMapOfUserBooksInteractor getCollectionIdMapOfUserBooksInteractor;
  private final InteractorHandler interactorHandler;

  private String collectionId;

  private DomainCallback<Integer, ErrorCore> callback;
  private DomainBackgroundCallback<Integer, ErrorCore<?>> backgroundCallback;

  @Inject
  public GetFinishedBooksCountInteractorImpl(InteractorExecutor executor, MainThread mainThread, final UserBooksRepository userBooksRepository,
      final GetCollectionIdMapOfUserBooksInteractor getCollectionIdMapOfUserBooksInteractor, final InteractorHandler interactorHandler) {
    super(executor, mainThread);
    this.userBooksRepository = userBooksRepository;
    this.getCollectionIdMapOfUserBooksInteractor = getCollectionIdMapOfUserBooksInteractor;
    this.interactorHandler = interactorHandler;
  }

  @Override public void execute(DomainCallback<Integer, ErrorCore> callback) {
    this.callback = callback;
    this.collectionId = NONE_COLLECTION_ID;
    this.executor.run(this);
  }

  @Override public void execute(DomainBackgroundCallback<Integer, ErrorCore<?>> callback) {
    this.backgroundCallback = callback;
    this.callback = null;
    this.collectionId = NONE_COLLECTION_ID;
    this.executor.run(this);
  }

  @Override public void execute(int collectionId, DomainCallback<Integer, ErrorCore> callback) {
    this.collectionId = String.valueOf(collectionId);
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Integer> execute() {
    return execute(executor.getExecutor());
  }

  @Override public ListenableFuture<Integer> execute(final Executor executor) {
    final SettableFuture<Integer> future = SettableFuture.create();
    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        perform(Integer.parseInt(collectionId), new Callback<Integer>() {
          @Override public void onSuccess(final Integer integer) {
            future.set(integer);
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

  @Override public ListenableFuture<Integer> execute(final int collectionId) {
    return execute(collectionId, executor.getExecutor());
  }

  @Override public ListenableFuture<Integer> execute(final int collectionId, final Executor executor) {
    final SettableFuture<Integer> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        perform(collectionId, new Callback<Integer>() {
          @Override public void onSuccess(final Integer integer) {
            settableFuture.set(integer);
          }

          @Override public void onError(final Throwable e) {
            settableFuture.setException(e);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }

  @Override public void run() {
    perform(Integer.parseInt(collectionId), new Callback<Integer>() {
      @Override public void onSuccess(final Integer integer) {
        if (backgroundCallback != null) {
          backgroundCallback.onSuccess(integer);
        } else {
          performSuccessCallback(callback, integer);
        }
      }

      @Override public void onError(final Throwable e) {
        notifyError(ErrorCore.of(new Throwable()), callback);
      }
    });
  }

  //region Private methods
  private void perform(final int collectionId, final Callback<Integer> callback) {
    final GetAllUserBooksFinishedStorageSpec spec = new GetAllUserBooksFinishedStorageSpec();

    userBooksRepository.getAll(spec, new Callback<Optional<List<UserBook>>>() {
      @Override public void onSuccess(final Optional<List<UserBook>> listOptional) {
        if (listOptional.isPresent()) {
          final List<UserBook> userBooks = listOptional.get();

          final ListenableFuture<Map<String, Set<String>>> getCollectionIdMapFuture =
              getCollectionIdMapOfUserBooksInteractor.execute(userBooks, MoreExecutors.directExecutor());
          interactorHandler.addCallback(getCollectionIdMapFuture, new FutureCallback<Map<String, Set<String>>>() {
            @Override public void onSuccess(@Nullable final Map<String, Set<String>> result) {
              // TODO: 23/02/2017 @Jose double verification with the NONE_COLLECTION_ID

              final Set<String> booksFinishedByCollection = result.get(String.valueOf(collectionId));
              final int response = booksFinishedByCollection == null ? 0 : booksFinishedByCollection.size();

              if (callback != null) {
                callback.onSuccess(response);
              }
            }

            @Override public void onFailure(final Throwable t) {
              if (callback != null) {
                callback.onError(t);
              }
            }
          });
        } else {
          if (callback != null) {
            callback.onError(new Throwable());
          }
        }
      }

      @Override public void onError(final Throwable e) {
        if (callback != null) {
          callback.onError(e);
        }
      }
    });
  }

  private void notifyError(final ErrorCore of, final DomainCallback<Integer, ErrorCore> callback) {
    if (backgroundCallback != null) {
      backgroundCallback.onError(of);
    } else {
      performErrorCallback(callback, of);
    }
  }
  //endregion
}
