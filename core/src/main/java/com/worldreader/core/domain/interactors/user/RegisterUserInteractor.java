package com.worldreader.core.domain.interactors.user;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.domain.interactors.referrer.GetReferrerInteractor;
import com.worldreader.core.domain.model.Referrer;
import com.worldreader.core.domain.model.user.RegisterProvider;
import com.worldreader.core.domain.model.user.RegisterProviderData;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton public class RegisterUserInteractor {

  private final ListeningExecutorService executor;
  private final UserRepository repository;
  private final GetReferrerInteractor getReferrerInteractor;

  @Inject
  public RegisterUserInteractor(ListeningExecutorService executor, UserRepository repository,
      GetReferrerInteractor getReferrerInteractor) {
    this.executor = executor;
    this.repository = repository;
    this.getReferrerInteractor = getReferrerInteractor;
  }

  public ListenableFuture<Optional<User2>> execute(final RegisterProvider provider,
      final RegisterProviderData<?> data) {
    return execute(provider, data, executor);
  }

  public ListenableFuture<Optional<User2>> execute(final RegisterProvider provider,
      final RegisterProviderData<?> data, final Executor executor) {
    final SettableFuture<Optional<User2>> future = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {

        Referrer referrer = null;
        try {
          referrer = getReferrerInteractor.execute(MoreExecutors.newDirectExecutorService()).get();
        } catch (Exception ignore) {
        }

        repository.register(provider, data, referrer, new Callback<Optional<User2>>() {
          @Override public void onSuccess(Optional<User2> optional) {

            future.set(optional);
          }

          @Override public void onError(Throwable t) {
            future.setException(t);
          }
        });
      }
    });

    return future;
  }
}
