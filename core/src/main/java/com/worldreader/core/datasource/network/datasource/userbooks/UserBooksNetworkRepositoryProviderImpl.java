package com.worldreader.core.datasource.network.datasource.userbooks;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.worldreader.core.application.helper.reachability.Reachability;
import com.worldreader.core.datasource.repository.NetworkRepositoryProvider;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor.Type;

public class UserBooksNetworkRepositoryProviderImpl
    implements NetworkRepositoryProvider<UserBooksNetworkDataSource> {

  private final Reachability reachability;
  private final UserBooksNetworkDataSource networkImplementation;
  private final UserBooksNetworkDataSource offlineNetworkImplementation;
  private final IsAnonymousUserInteractor interactor;

  public UserBooksNetworkRepositoryProviderImpl(Reachability reachability,
      UserBooksNetworkDataSource networkImplementation,
      UserBooksNetworkDataSource offlineNetworkImplementation,
      IsAnonymousUserInteractor interactor) {
    this.reachability = reachability;
    this.networkImplementation = networkImplementation;
    this.offlineNetworkImplementation = offlineNetworkImplementation;
    this.interactor = interactor;
  }

  @Override public UserBooksNetworkDataSource get() {
    final ListenableFuture<Type> isUserAnonymousFuture =
        interactor.execute(MoreExecutors.directExecutor());
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
      throw new RuntimeException("Can't obtain user type!");
    }
  }

  @Override public UserBooksNetworkDataSource getRealNetwork() {
    return networkImplementation;
  }

  @Override public UserBooksNetworkDataSource getFakeNetwork() {
    return offlineNetworkImplementation;
  }

}
