package com.worldreader.core.domain.interactors.user;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.model.user.FacebookRegisterProviderData;
import com.worldreader.core.domain.model.user.GoogleProviderData;
import com.worldreader.core.domain.model.user.ReadToKidsProviderData;
import com.worldreader.core.domain.model.user.RegisterProvider;
import com.worldreader.core.domain.model.user.RegisterProviderData;
import com.worldreader.core.domain.model.user.WorldreaderProviderData;
import com.worldreader.core.domain.repository.OAuthRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton public class LoginUserInteractor {

  private final ListeningExecutorService executor;
  private final OAuthRepository repository;

  @Inject public LoginUserInteractor(ListeningExecutorService executor, OAuthRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<Boolean> execute(final RegisterProvider provider, final RegisterProviderData<?> data) {
    return execute(provider, data, executor);
  }

  public ListenableFuture<Boolean> execute(final RegisterProvider provider, final RegisterProviderData<?> data, final Executor executor) {
    final SettableFuture<Boolean> future = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        Preconditions.checkNotNull(provider, "Provider == null");
        Preconditions.checkNotNull(data, "Data == null");

        switch (provider) {
          case FACEBOOK:
            final String facebookToken = ((FacebookRegisterProviderData) data).get();
            final boolean isLoggedFacebook = repository.loginWithFacebook(facebookToken);
            future.set(isLoggedFacebook);
            break;
          case GOOGLE:
            final GoogleProviderData.DomainGoogleRegisterData googleRegisterData = ((GoogleProviderData) data).get();
            final boolean isLoggedGoogle = repository.loginWithGoogle(googleRegisterData.getGoogleId(), googleRegisterData.getEmail());
            future.set(isLoggedGoogle);
            break;
          case WORLDREADER:
            final String username;
            final String password;

            if (data instanceof WorldreaderProviderData) {
              final WorldreaderProviderData.DomainWorldreaderData worldreaderRegisterData = ((WorldreaderProviderData) data).get();
              username = worldreaderRegisterData.getUsername();
              password = worldreaderRegisterData.getPassword();
            } else if (data instanceof ReadToKidsProviderData) {
              final ReadToKidsProviderData.DomainReadToKidsData readToKidsData = ((ReadToKidsProviderData) data).get();
              username = readToKidsData.getUsername();
              password = readToKidsData.getPassword();
            } else {
              throw new UnsupportedOperationException("dataprovider not supported");
            }

            final boolean isLoggedWorldreader = repository.login(username, password);
            future.set(isLoggedWorldreader);
            break;
        }
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    });

    return future;
  }

}
