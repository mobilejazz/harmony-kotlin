package com.worldreader.core.datasource.network.datasource.geolocation;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.datasource.network.model.GeolocationInfoEntity;

public interface GeolocationNetworkDataSource {

  ListenableFuture<Optional<GeolocationInfoEntity>> getGeocodeInformation(double latitude, double longitude);
}
