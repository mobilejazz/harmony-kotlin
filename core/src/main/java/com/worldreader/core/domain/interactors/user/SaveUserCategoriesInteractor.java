package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.user.UpdateUserCategoriesSpecification;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.repository.UserRepository;
import com.worldreader.core.error.general.UnexpectedErrorException;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.*;

@Singleton public class SaveUserCategoriesInteractor {

  private final ListeningExecutorService executor;
  private final UserRepository repository;
  private final IsAnonymousUserInteractor isAnonymousUserInteractor;
  private final GetUserInteractor getUserInteractor;
  private final SaveUserInteractor saveUserInteractor;
  private final InteractorHandler interactorHandler;

  @Inject
  public SaveUserCategoriesInteractor(ListeningExecutorService executor, UserRepository repository,
      final IsAnonymousUserInteractor isAnonymousUserInteractor,
      final GetUserInteractor getUserInteractor, final SaveUserInteractor saveUserInteractor,
      final InteractorHandler interactorHandler) {
    this.executor = executor;
    this.repository = repository;
    this.isAnonymousUserInteractor = isAnonymousUserInteractor;
    this.getUserInteractor = getUserInteractor;
    this.saveUserInteractor = saveUserInteractor;
    this.interactorHandler = interactorHandler;
  }

  public ListenableFuture<User2> execute(final List<Integer> categories) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(categories, future));
    return future;
  }

  public ListenableFuture<User2> execute(final List<Integer> categories, final Executor executor) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(categories, future));
    return future;
  }

  @NonNull Runnable getInteractorRunnable(final List<Integer> categories,
      final SettableFuture<User2> future) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final IsAnonymousUserInteractor.Type type =
            isAnonymousUserInteractor.execute(MoreExecutors.directExecutor()).get();

        final RepositorySpecification specification;

        if (type == IsAnonymousUserInteractor.Type.ANONYMOUS) {
          specification =
              new UserStorageSpecification(UserStorageSpecification.UserTarget.ANONYMOUS);
        } else {
          specification = new UpdateUserCategoriesSpecification(categories);
        }

        final List<String> categoriesTransformed =
            Lists.transform(categories, new Function<Integer, String>() {
              @Nullable @Override public String apply(@Nullable final Integer input) {
                return String.valueOf(input);
              }
            });

        final User2 user2 = getUserInteractor.execute(UserStorageSpecification.target(
            UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS),
            MoreExecutors.directExecutor()).get();
        final User2 user2ToUpdate =
            new User2.Builder(user2).setFavoriteCategories(categoriesTransformed).build();

        repository.put(user2ToUpdate, specification, new Callback<Optional<User2>>() {
          @Override public void onSuccess(Optional<User2> optional) {
            if (optional.isPresent()) {
              final User2 user = optional.get();

              final SaveUserInteractor.Type userType =
                  type == IsAnonymousUserInteractor.Type.ANONYMOUS
                  ? SaveUserInteractor.Type.ANONYMOUS : SaveUserInteractor.Type.LOGGED_IN;
              final ListenableFuture<User2> saveUserLf =
                  saveUserInteractor.execute(user, userType, MoreExecutors.directExecutor());

              interactorHandler.addCallback(saveUserLf, new FutureCallback<User2>() {
                @Override public void onSuccess(@Nullable final User2 result) {
                  future.set(result);
                }

                @Override public void onFailure(final Throwable t) {
                  future.setException(t);
                }
              });
            } else {
              future.setException(new UnexpectedErrorException("User is not defined!"));
            }
          }

          @Override public void onError(Throwable e) {
            future.setException(e);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    };
  }

}
