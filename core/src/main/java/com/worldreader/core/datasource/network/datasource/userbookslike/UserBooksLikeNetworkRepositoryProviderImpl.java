package com.worldreader.core.datasource.network.datasource.userbookslike;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.worldreader.core.application.helper.reachability.Reachability;
import com.worldreader.core.datasource.repository.NetworkRepositoryProvider;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor.Type;

public class UserBooksLikeNetworkRepositoryProviderImpl implements NetworkRepositoryProvider<UserBooksLikeNetworkDataSource> {

  private final Reachability reachability;
  private final UserBooksLikeNetworkDataSource networkImplementation;
  private final UserBooksLikeNetworkDataSource offlineNetworkImplementation;
  private final IsAnonymousUserInteractor interactor;

  public UserBooksLikeNetworkRepositoryProviderImpl(Reachability reachability, UserBooksLikeNetworkDataSource networkImplementation,
      UserBooksLikeNetworkDataSource offlineNetworkImplementation, IsAnonymousUserInteractor interactor) {
    this.reachability = reachability;
    this.networkImplementation = networkImplementation;
    this.offlineNetworkImplementation = offlineNetworkImplementation;
    this.interactor = interactor;
  }

  @Override public UserBooksLikeNetworkDataSource get() {
    final ListenableFuture<Type> isUserAnonymousFuture = interactor.execute(MoreExecutors.directExecutor());
    try {
      final Type type = isUserAnonymousFuture.get();
      switch (type) {
        case ANONYMOUS:
          return offlineNetworkImplementation;
        case REGISTERED:
        case NONE:
        default:
          final boolean networkAvailable = reachability.isReachable();
          return networkAvailable ? networkImplementation : offlineNetworkImplementation;
      }
    } catch (Exception e) {
      throw new RuntimeException("Can't obtain userbooklike type!");
    }
  }

  @Override public UserBooksLikeNetworkDataSource getRealNetwork() {
    return networkImplementation;
  }

  @Override public UserBooksLikeNetworkDataSource getFakeNetwork() {
    return offlineNetworkImplementation;
  }

}
