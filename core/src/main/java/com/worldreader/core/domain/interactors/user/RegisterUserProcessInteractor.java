package com.worldreader.core.domain.interactors.user;

import android.support.annotation.Nullable;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.mobilejazz.kotlin.core.di.ActivityScope;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.model.user.RegisterProvider;
import com.worldreader.core.domain.model.user.RegisterProviderData;
import com.worldreader.core.domain.model.user.User2;
import javax.inject.Inject;

@ActivityScope public class RegisterUserProcessInteractor {

  private final ListeningExecutorService executor;

  private final RegisterUserInteractor registerUserInteractor;
  private final LoginUserInteractor loginUserInteractor;
  private final GetUserInteractor getUserInteractor;

  @Inject public RegisterUserProcessInteractor(final ListeningExecutorService executor,
      final RegisterUserInteractor registerUserInteractor,
      final LoginUserInteractor loginUserInteractor, final GetUserInteractor getUserInteractor) {
    this.executor = executor;
    this.registerUserInteractor = registerUserInteractor;
    this.loginUserInteractor = loginUserInteractor;
    this.getUserInteractor = getUserInteractor;
  }

  public ListenableFuture<User2> execute(final RegisterProvider provider,
      final RegisterProviderData<?> registerProviderData) {
    final SettableFuture<User2> future = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        // Validation
        Preconditions.checkNotNull(provider, "provider == null");
        Preconditions.checkNotNull(registerProviderData, "registerProviderData == null");

        final ListenableFuture<Optional<User2>> registerUserFuture =
            registerUserInteractor.execute(provider, registerProviderData,
                MoreExecutors.directExecutor());
        Futures.addCallback(registerUserFuture, new FutureCallback<Optional<User2>>() {
          @Override public void onSuccess(@Nullable final Optional<User2> result) {
            loginProcessAfterRegister(provider, registerProviderData, future);
          }

          @Override public void onFailure(final Throwable t) {
            //loginProcessAfterRegister(provider, registerProviderData, future);
            future.setException(t);
          }
        });

        //....

        //// Futures
        //final ListenableFuture<Optional<User2>> registerUserFuture = registerUserInteractor.execute(provider, registerProviderData, MoreExecutors.directExecutor());
        //final ListenableFuture<Boolean> loginUserFuture = loginUserInteractor.execute(provider, registerProviderData, MoreExecutors.directExecutor());
        //final ListenableFuture<User2> getUserFuture = getUserInteractor.execute(MoreExecutors.directExecutor());
        //
        //// Combiner
        //final Callable<Pair<Boolean, User2>> combinerFunction = new Callable<Pair<Boolean, User2>>() {
        //  @Override public Pair<Boolean, User2> call() throws Exception {
        //    final Boolean loginResult = loginUserFuture.get();
        //    final User2 user2 = getUserFuture.get();
        //    return Pair.with(loginResult, user2);
        //  }
        //};
        //
        //// Grouped future
        //final ListenableFuture<Pair<Boolean, User2>> registerUserProcessFuture =
        //    Futures.whenAllSucceed(registerUserFuture, loginUserFuture, getUserFuture).call(combinerFunction);
        //
        //Futures.addCallback(registerUserProcessFuture, new FutureCallback<Pair<Boolean, User2>>() {
        //  @Override public void onSuccess(@Nullable final Pair<Boolean, User2> result) {
        //    // Process it
        //    if (result != null) {
        //      final User2 user2 = result.getValue1();
        //      future.set(user2);
        //    } else {
        //      future.setException(new UnexpectedErrorException("User could not be registered!"));
        //    }
        //  }
        //
        //  @Override public void onFailure(@NonNull final Throwable t) {
        //    future.setException(t);
        //  }
        //});
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    });

    return future;
  }

  private void loginProcessAfterRegister(final RegisterProvider provider,
      final RegisterProviderData<?> registerProviderData, final SettableFuture<User2> future) {
    final ListenableFuture<Boolean> loginUserFuture =
        loginUserInteractor.execute(provider, registerProviderData, MoreExecutors.directExecutor());

    final ListenableFuture<User2> registerProcessCombinerFuture =
        Futures.transformAsync(loginUserFuture, new AsyncFunction<Boolean, User2>() {
          @Override public ListenableFuture<User2> apply(@Nullable final Boolean input)
              throws Exception {
            return getUserInteractor.execute(MoreExecutors.directExecutor());
          }
        }, MoreExecutors.directExecutor());

    Futures.addCallback(registerProcessCombinerFuture, new FutureCallback<User2>() {
      @Override public void onSuccess(@Nullable final User2 result) {
        future.set(result);
      }

      @Override public void onFailure(final Throwable t) {
        future.setException(t);
      }
    });
  }
}
