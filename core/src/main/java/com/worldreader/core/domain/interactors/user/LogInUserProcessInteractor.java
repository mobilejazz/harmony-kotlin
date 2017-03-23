package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AsyncCallable;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.common.date.Dates;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.interactors.application.SaveOnBoardingPassedInteractor;
import com.worldreader.core.domain.interactors.application.SaveSessionInteractor;
import com.worldreader.core.domain.interactors.application.SaveUserRegisteredTypeInteractor;
import com.worldreader.core.domain.interactors.oauth.PutUserTokenInteractor;
import com.worldreader.core.domain.model.user.RegisterProvider;
import com.worldreader.core.domain.model.user.RegisterProviderData;
import com.worldreader.core.domain.model.user.User2;

import javax.inject.Inject;
import java.util.concurrent.*;

@PerActivity public class LogInUserProcessInteractor {

  private final ListeningExecutorService executor;

  private final LoginUserInteractor loginUserInteractor;
  private final GetUserInteractor getUserInteractor;
  private final SaveUserInteractor saveUserInteractor;
  private final SaveSessionInteractor saveSessionInteractor;
  private final SaveOnBoardingPassedInteractor saveOnBoardingPassedInteractor;
  private final SaveUserRegisteredTypeInteractor saveUserRegisteredTypeInteractor;
  private final PutUserTokenInteractor putUserTokenInteractor;
  private final Dates dateUtils;

  @Inject public LogInUserProcessInteractor(final ListeningExecutorService executor,
      final LoginUserInteractor loginUserInteractor, final GetUserInteractor getUserInteractor,
      final SaveUserInteractor saveUserInteractor,
      final SaveSessionInteractor saveSessionInteractor,
      final SaveOnBoardingPassedInteractor saveOnBoardingPassedInteractor,
      final SaveUserRegisteredTypeInteractor saveUserRegisteredTypeInteractor,
      final PutUserTokenInteractor putUserTokenInteractor, final Dates dateUtils) {
    this.executor = executor;
    this.loginUserInteractor = loginUserInteractor;
    this.getUserInteractor = getUserInteractor;
    this.saveUserInteractor = saveUserInteractor;
    this.saveSessionInteractor = saveSessionInteractor;
    this.saveOnBoardingPassedInteractor = saveOnBoardingPassedInteractor;
    this.saveUserRegisteredTypeInteractor = saveUserRegisteredTypeInteractor;
    this.putUserTokenInteractor = putUserTokenInteractor;
    this.dateUtils = dateUtils;
  }

  public ListenableFuture<User2> execute(final String userToken) {
    final SettableFuture<User2> future = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        Preconditions.checkNotNull(userToken, "userToken == null");

        final ListenableFuture<Void> putUserTokenFuture =
            putUserTokenInteractor.execute(userToken, MoreExecutors.directExecutor());

        final ListenableFuture<User2> getUserFuture =
            Futures.whenAllSucceed(putUserTokenFuture).callAsync(new AsyncCallable<User2>() {
              @Override public ListenableFuture<User2> call() throws Exception {
                return getUserInteractor.execute();
              }
            });

        // Combine with SaveUser
        final ListenableFuture<User2> saveUserFuture =
            Futures.whenAllSucceed(getUserFuture).callAsync(new AsyncCallable<User2>() {
              @Override public ListenableFuture<User2> call() throws Exception {
                final User2 user = getUserFuture.get();
                final SaveUserInteractor.Type type = SaveUserInteractor.Type.LOGGED_IN;
                return saveUserInteractor.execute(user, type);
              }
            });

        // After saving the user kickoff the onboarding
        final ListenableFuture<User2> loginUserProcessFuture =
            Futures.whenAllSucceed(saveUserFuture).call(new Callable<User2>() {
              @Override public User2 call() throws Exception {
                saveOnBoardingPassedInteractor.execute(true);
                saveSessionInteractor.execute(dateUtils.today());
                saveUserRegisteredTypeInteractor.execute(
                    GetUserRegisteredTypeInteractor.UserRegisteredType.WORLDREADER,
                    MoreExecutors.directExecutor());
                return saveUserFuture.get();
              }
            });

        Futures.addCallback(loginUserProcessFuture, new FutureCallback<User2>() {
          @Override public void onSuccess(@Nullable final User2 user) {
            future.set(user);
          }

          @Override public void onFailure(@NonNull final Throwable t) {
            future.setException(t);
          }
        });
      }

      @Override protected void onExceptionThrown(@NonNull final Throwable t) {
        future.setException(t);
      }
    });

    return future;
  }

  public ListenableFuture<User2> execute(final RegisterProvider provider,
      final RegisterProviderData<?> registerProviderData) {
    final SettableFuture<User2> future = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        Preconditions.checkNotNull(provider, "provider == null");
        Preconditions.checkNotNull(provider, "registerProviderData == null");

        // Futures (Login + GetUser)
        final ListenableFuture<Boolean> loginUserFuture =
            loginUserInteractor.execute(provider, registerProviderData);
        final ListenableFuture<User2> getUserFuture =
            Futures.whenAllSucceed(loginUserFuture).callAsync(new AsyncCallable<User2>() {
              @Override public ListenableFuture<User2> call() throws Exception {
                return getUserInteractor.execute();
              }
            });

        // Combine with SaveUser
        final ListenableFuture<User2> saveUserFuture =
            Futures.whenAllSucceed(getUserFuture).callAsync(new AsyncCallable<User2>() {
              @Override public ListenableFuture<User2> call() throws Exception {
                final User2 user = getUserFuture.get();
                final SaveUserInteractor.Type type = SaveUserInteractor.Type.LOGGED_IN;
                return saveUserInteractor.execute(user, type);
              }
            });

        // After saving the user kickoff the onboarding
        final ListenableFuture<User2> loginUserProcessFuture =
            Futures.whenAllSucceed(saveUserFuture).call(new Callable<User2>() {
              @Override public User2 call() throws Exception {
                saveOnBoardingPassedInteractor.execute(true);
                saveSessionInteractor.execute(dateUtils.today());
                saveUserRegisteredTypeInteractor.execute(toUserRegisteredType(provider),
                    MoreExecutors.directExecutor());
                return saveUserFuture.get();
              }
            });

        Futures.addCallback(loginUserProcessFuture, new FutureCallback<User2>() {
          @Override public void onSuccess(@Nullable final User2 user) {
            future.set(user);
          }

          @Override public void onFailure(@NonNull final Throwable t) {
            future.setException(t);
          }
        });
      }

      @Override protected void onExceptionThrown(@NonNull final Throwable t) {
        future.setException(t);
      }
    });

    return future;
  }

  private GetUserRegisteredTypeInteractor.UserRegisteredType toUserRegisteredType(
      final RegisterProvider provider) {
    switch (provider) {
      case FACEBOOK:
        return GetUserRegisteredTypeInteractor.UserRegisteredType.FACEBOOK;
      case GOOGLE:
        return GetUserRegisteredTypeInteractor.UserRegisteredType.GOOGLE;
      case WORLDREADER:
        return GetUserRegisteredTypeInteractor.UserRegisteredType.WORLDREADER;
    }

    return null;
  }

}
