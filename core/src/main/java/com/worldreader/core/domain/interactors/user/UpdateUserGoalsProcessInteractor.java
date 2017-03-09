package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor.Type;
import com.worldreader.core.domain.model.user.User2;

import javax.inject.Inject;

import static com.worldreader.core.datasource.spec.user.UserStorageSpecification.UserTarget.ANONYMOUS;

@PerActivity public class UpdateUserGoalsProcessInteractor {

  private final ListeningExecutorService executor;

  private final IsAnonymousUserInteractor isAnonymousUserInteractor;
  private final GetUserInteractor getUserInteractor;
  private final SaveUserInteractor saveUserInteractor;
  private final EstablishUserGoalsInteractor establishUserGoalsInteractor;

  private final UpdateUserGoalsInteractor updateUserGoalsInteractor;

  @Inject public UpdateUserGoalsProcessInteractor(final ListeningExecutorService executor,
      final IsAnonymousUserInteractor isAnonymousUserInteractor,
      final GetUserInteractor getUserInteractor,
      final UpdateUserGoalsInteractor updateUserGoalsInteractor,
      final SaveUserInteractor saveUserInteractor,
      final EstablishUserGoalsInteractor establishUserGoalsInteractor) {
    this.executor = executor;
    this.isAnonymousUserInteractor = isAnonymousUserInteractor;
    this.getUserInteractor = getUserInteractor;
    this.updateUserGoalsInteractor = updateUserGoalsInteractor;
    this.saveUserInteractor = saveUserInteractor;
    this.establishUserGoalsInteractor = establishUserGoalsInteractor;
  }

  public ListenableFuture<User2> execute(final int pagesPerDay, final int minChildrenAge,
      final int maxChildrenAge) {
    final SettableFuture<User2> future = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        // First obtain if the user is Anonymous or not
        final ListenableFuture<Type> isAnonymousUserFuture =
            isAnonymousUserInteractor.execute(MoreExecutors.directExecutor());

        final Type type = isAnonymousUserFuture.get();
        switch (type) {
          case ANONYMOUS:
            // Obtain user
            final ListenableFuture<User2> getUserFuture =
                getUserInteractor.execute(new UserStorageSpecification(ANONYMOUS),
                    MoreExecutors.directExecutor());
            final User2 anonymousUser = getUserFuture.get();

            // Let's create another instance with values updated
            final User2 anonymousUpdateUser =
                new User2.Builder(anonymousUser).setPagesPerDay(pagesPerDay)
                    .setMinChildAge(minChildrenAge)
                    .setMaxChildAge(maxChildrenAge)
                    .build();

            // Let's store it
            final ListenableFuture<User2> saveUserFuture =
                saveUserInteractor.execute(anonymousUpdateUser, SaveUserInteractor.Type.ANONYMOUS);

            // Store that the user already has saved the preferences
            final ListenableFuture<User2> updateAnonymousUserGoalsProcessFuture =
                Futures.transformAsync(saveUserFuture, new AsyncFunction<User2, User2>() {
                  @Override public ListenableFuture<User2> apply(@Nullable final User2 input)
                      throws Exception {
                    establishUserGoalsInteractor.execute(MoreExecutors.directExecutor());
                    return Futures.immediateFuture(input);
                  }
                });

            // Notify back
            Futures.addCallback(updateAnonymousUserGoalsProcessFuture, new FutureCallback<User2>() {
              @Override public void onSuccess(@Nullable final User2 result) {
                future.set(result);
              }

              @Override public void onFailure(@NonNull final Throwable t) {
                future.setException(t);
              }
            });

            break;
          case REGISTERED:
            // Update the values and obtain the new user updated
            final ListenableFuture<User2> updateUserGoalsInteractorFuture =
                updateUserGoalsInteractor.execute(pagesPerDay, minChildrenAge, maxChildrenAge,
                    MoreExecutors.directExecutor());
            final User2 updatedUser = updateUserGoalsInteractorFuture.get();

            // Save the new obtained user
            final ListenableFuture<User2> updateUserFuture =
                saveUserInteractor.execute(updatedUser, SaveUserInteractor.Type.LOGGED_IN);

            // Keep that the user already has saved the preferences
            final ListenableFuture<User2> updateUserGoalsProcessFuture =
                Futures.transformAsync(updateUserFuture, new AsyncFunction<User2, User2>() {
                  @Override public ListenableFuture<User2> apply(@Nullable final User2 input)
                      throws Exception {
                    establishUserGoalsInteractor.execute(MoreExecutors.directExecutor());
                    return Futures.immediateFuture(input);
                  }
                });

            // Notify back
            Futures.addCallback(updateUserGoalsProcessFuture, new FutureCallback<User2>() {
              @Override public void onSuccess(@Nullable final User2 result) {
                future.set(result);
              }

              @Override public void onFailure(@NonNull final Throwable t) {
                future.setException(t);
              }
            });
            break;
          case NONE:
            throw new IllegalStateException("User should not be a non existent one!");
        }
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    });

    return future;
  }

}
