package com.worldreader.core.domain.interactors.user.score;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.score.UserScoreStorageSpecification;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.interactors.user.GetUserInteractor;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.model.user.UserScore;
import java.util.Date;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class AddUserScoreInteractor {

  private final ListeningExecutorService executor;
  private final Repository<UserScore, RepositorySpecification> userScoreRepository;
  private final GetUserInteractor getUserInteractor;
  private final InteractorHandler interactorHandler;

  @Inject
  public AddUserScoreInteractor(final ListeningExecutorService executor, final Repository<UserScore, RepositorySpecification> userScoreRepository,
      final GetUserInteractor getUserInteractor, final InteractorHandler interactorHandler) {
    this.executor = executor;
    this.userScoreRepository = userScoreRepository;
    this.getUserInteractor = getUserInteractor;
    this.interactorHandler = interactorHandler;
  }

  public ListenableFuture<UserScore> execute(final String bookId, final int pages) {
    return execute(bookId, pages, executor);
  }

  public ListenableFuture<UserScore> execute(final String bookId, final int pages, Executor executor) {
    final UserStorageSpecification spec = UserStorageSpecification.target(UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS);
    final SettableFuture<UserScore> settableFuture = SettableFuture.create();
    executor.execute(getInteractorCallable(bookId, pages, false, spec, settableFuture));
    return settableFuture;
  }

  public ListenableFuture<UserScore> execute(final int amount) {
    return execute(amount, executor);
  }

  public ListenableFuture<UserScore> execute(final int amount, Executor executor) {
    return execute(amount, false, executor);
  }

  public ListenableFuture<UserScore> execute(final int amount, final boolean synced, Executor executor) {
    final UserStorageSpecification spec = UserStorageSpecification.target(UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS);
    return execute(amount, synced, spec, executor);
  }

  public ListenableFuture<UserScore> execute(final int amount, final boolean synced, final UserStorageSpecification spec, Executor executor) {
    final SettableFuture<UserScore> settableFuture = SettableFuture.create();
    executor.execute(getInteractorCallable(null, amount, synced, spec, settableFuture));
    return settableFuture;
  }

  @NonNull private SafeRunnable getInteractorCallable(@Nullable final String bookId, final int amount, final boolean synced,
      final UserStorageSpecification spec, final SettableFuture<UserScore> future) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final ListenableFuture<User2> userLf = getUserInteractor.execute(spec, MoreExecutors.directExecutor());

        interactorHandler.addCallback(userLf, new FutureCallback<User2>() {
          @Override public void onSuccess(final User2 result) {
            if (result != null) {
              final String userId = result.getId();
              final UserScore userScore;

              // If bookId param is null then we just want to append a normal user score with amount
              if (bookId == null) {
                userScore = new UserScore.Builder().setUserId(userId)
                    .setSync(synced)
                    .setScore(amount)
                    .setCreatedAt(new Date())
                    .setUpdatedAt(new Date())
                    .build();
              } else {
                // Otherwise we count this user score as an special one with amount in pages
                userScore = new UserScore.Builder().setUserId(userId)
                    .setBookId(bookId)
                    .setPages(amount)
                    .setSync(synced)
                    .setCreatedAt(new Date())
                    .setUpdatedAt(new Date())
                    .build();
              }

              userScoreRepository.put(userScore, UserScoreStorageSpecification.NONE, new Callback<Optional<UserScore>>() {
                @Override public void onSuccess(final Optional<UserScore> userScoreOptional) {
                  if (userScoreOptional.isPresent()) {
                    future.set(userScoreOptional.get());
                  } else {
                    future.setException(new UnknownError());
                  }
                }

                @Override public void onError(final Throwable e) {
                  future.setException(e);
                }
              });
            } else {
              future.setException(new UnknownError());
            }

          }

          @Override public void onFailure(final Throwable t) {
            future.setException(t);
          }
        }, MoreExecutors.directExecutor());
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    };
  }
}
