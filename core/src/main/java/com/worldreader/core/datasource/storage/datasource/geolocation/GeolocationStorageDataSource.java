package com.worldreader.core.datasource.storage.datasource.geolocation;

import com.worldreader.core.datasource.network.model.GeolocationInfoEntity;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;

public interface GeolocationStorageDataSource {

  GeolocationInfoEntity obtains() throws InvalidCacheException;

  boolean isValid();

  void persist(GeolocationInfoEntity geolocationInfoEntity);

}
