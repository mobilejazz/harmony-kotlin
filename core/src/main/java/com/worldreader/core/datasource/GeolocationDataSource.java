package com.worldreader.core.datasource;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.concurrency.FluentFuture;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.GeolocationInfo;
import com.worldreader.core.datasource.network.datasource.geolocation.GeolocationNetworkDataSource;
import com.worldreader.core.datasource.network.model.GeolocationInfoEntity;
import com.worldreader.core.datasource.storage.datasource.geolocation.GeolocationStorageDataSource;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;
import com.worldreader.core.domain.repository.GeolocationRepository;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class GeolocationDataSource implements GeolocationRepository {

  private static final String CACHE_KEY = "GeolocationInfo";

  private GeolocationNetworkDataSource network;
  private GeolocationStorageDataSource storage;
  private Mapper<GeolocationInfoEntity, GeolocationInfo> mapper;

  @Inject
  public GeolocationDataSource(GeolocationNetworkDataSource network,
      GeolocationStorageDataSource storage,
      Mapper<GeolocationInfoEntity, GeolocationInfo> mapper) {
    this.network = network;
    this.storage = storage;
    this.mapper = mapper;
  }

  @Override public ListenableFuture<Boolean> update(final double latitude, final double longitude) {
    if (!storage.isValid(CACHE_KEY)) {
      return FluentFuture.from(network.getGeocodeInformation(latitude, longitude))
          .transform(new Function<Optional<GeolocationInfoEntity>, Boolean>() {
            @Nullable @Override public Boolean apply(Optional<GeolocationInfoEntity> input) {
              if (input.isPresent()) {
                storage.persist(CACHE_KEY, input.get());
                return true;
              } else {
                return false;
              }
            }
          });
    } else {
      return Futures.immediateFuture(true);
    }
  }

  @Override public ListenableFuture<Optional<GeolocationInfo>> get() {
    try {
      return Futures.immediateFuture(Optional.of(mapper.transform(storage.obtains(CACHE_KEY))));
    } catch (InvalidCacheException e) {
      return Futures.immediateFuture(Optional.<GeolocationInfo>absent());
    }
  }

}
