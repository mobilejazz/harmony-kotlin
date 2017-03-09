package com.worldreader.core.datasource.network.datasource.user;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.worldreader.core.application.helper.reachability.Reachability;
import com.worldreader.core.datasource.repository.NetworkRepositoryProvider;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor.Type;

public class UserNetworkRepositoryProviderImpl
    implements NetworkRepositoryProvider<UserNetworkDataSource2> {

  private final Reachability reachability;
  private final UserNetworkDataSource2 networkImplementation;
  private final UserNetworkDataSource2 offlineNetworkImplementation;
  private final IsAnonymousUserInteractor interactor;

  public UserNetworkRepositoryProviderImpl(final Reachability reachability,
      final UserNetworkDataSource2 networkImplementation,
      final UserNetworkDataSource2 offlineNetworkImplementation,
      final IsAnonymousUserInteractor interactor) {
    this.reachability = reachability;
    this.networkImplementation = networkImplementation;
    this.offlineNetworkImplementation = offlineNetworkImplementation;
    this.interactor = interactor;
  }

  @Override public UserNetworkDataSource2 get() {
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

  @Override public UserNetworkDataSource2 getRealNetwork() {
    return networkImplementation;
  }

  @Override public UserNetworkDataSource2 getFakeNetwork() {
    return offlineNetworkImplementation;
  }

}
