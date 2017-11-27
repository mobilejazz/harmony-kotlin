package com.worldreader.core.datasource;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.GeolocationInfo;
import com.worldreader.core.datasource.network.datasource.geolocation.GeolocationNetworkDataSource;
import com.worldreader.core.datasource.network.model.GeolocationInfoEntity;
import com.worldreader.core.datasource.storage.datasource.geolocation.GeolocationStorageDataSource;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;
import com.worldreader.core.domain.repository.GeolocationRepository;

import javax.inject.Inject;
import java.util.concurrent.*;

public final class GeolocationDataSource implements GeolocationRepository {

  private final GeolocationNetworkDataSource network;
  private final GeolocationStorageDataSource storage;
  private final Mapper<GeolocationInfoEntity, GeolocationInfo> mapper;

  @Inject
  public GeolocationDataSource(GeolocationNetworkDataSource network,
      GeolocationStorageDataSource storage,
      Mapper<GeolocationInfoEntity, GeolocationInfo> mapper) {
    this.network = network;
    this.storage = storage;
    this.mapper = mapper;
  }

  @Override public ListenableFuture<Boolean> update(final double latitude, final double longitude) {
    if (!storage.isValid()) {
      try {
        final Optional<GeolocationInfoEntity> input = network.getGeocodeInformation(latitude, longitude).get();
        if (input.isPresent()) {
          storage.persist(input.get());
        }
        return Futures.immediateFuture(input.isPresent());
      } catch (InterruptedException | ExecutionException e) {
        return Futures.immediateFailedFuture(e);
      }
    } else {
      return Futures.immediateFuture(true);
    }
  }

  @Override public ListenableFuture<Optional<GeolocationInfo>> get() {
    try {
      return Futures.immediateFuture(Optional.of(mapper.transform(storage.obtains())));
    } catch (InvalidCacheException e) {
      return Futures.immediateFuture(Optional.<GeolocationInfo>absent());
    }
  }

}
