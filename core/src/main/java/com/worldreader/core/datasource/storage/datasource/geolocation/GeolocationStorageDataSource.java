package com.worldreader.core.datasource.storage.datasource.geolocation;

import com.worldreader.core.datasource.network.model.GeolocationInfoEntity;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;

public interface GeolocationStorageDataSource {

  GeolocationInfoEntity obtains(String key) throws InvalidCacheException;

  boolean isValid(String key);

  void persist(String key, GeolocationInfoEntity geolocationInfoEntity);


}
