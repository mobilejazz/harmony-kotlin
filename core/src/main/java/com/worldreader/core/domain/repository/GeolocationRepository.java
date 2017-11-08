package com.worldreader.core.domain.repository;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.datasource.model.GeolocationInfo;

public interface GeolocationRepository {

  /**
   * Update the user current location in the storage.
   * @param latitude
   * @param longitude
   * @return true if the location has been updated or if there was a valid location in cache, false otherwise
   */
  ListenableFuture<Boolean> update(double latitude, double longitude);

  ListenableFuture<Optional<GeolocationInfo>> get();
}
