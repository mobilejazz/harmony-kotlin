package com.worldreader.core.datasource.storage.datasource.geolocation;

import com.google.gson.Gson;
import com.mobilejazz.vastra.ValidationService;
import com.worldreader.core.datasource.network.model.GeolocationInfoEntity;
import com.worldreader.core.datasource.storage.datasource.cache.CacheBddDataSource;
import com.worldreader.core.datasource.storage.datasource.cache.manager.entity.CacheObject;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;

import javax.inject.Inject;

public class GeolocationStorageDataSourceImpl implements GeolocationStorageDataSource {

  private final CacheBddDataSource cacheBddDataSource;
  private final Gson gson;
  private final ValidationService validationService;

  @Inject
  public GeolocationStorageDataSourceImpl(CacheBddDataSource cacheBddDataSource, Gson gson, ValidationService validationService) {
    this.cacheBddDataSource = cacheBddDataSource;
    this.gson = gson;
    this.validationService = validationService;
  }

  @Override public GeolocationInfoEntity obtains(String key) throws InvalidCacheException {
    CacheObject cacheObject = cacheBddDataSource.get(key);

    if (cacheObject == null) {
      throw new InvalidCacheException();
    }

    GeolocationInfoEntity geolocationInfoEntity = getEntity(cacheObject);
    if (!validationService.isValid(geolocationInfoEntity)) {
      throw new InvalidCacheException();
    }

    return geolocationInfoEntity;
  }

  @Override
  public boolean isValid(String key) {
    CacheObject cacheObject = cacheBddDataSource.get(key);

    return cacheObject != null && validationService.isValid(getEntity(cacheObject));
  }

  private GeolocationInfoEntity getEntity(CacheObject cacheObject) {
    return gson.fromJson(cacheObject.getValue(), GeolocationInfoEntity.class);
  }

  @Override public void persist(String key, GeolocationInfoEntity geolocationInfoEntity) {
    String json = gson.toJson(geolocationInfoEntity);
    CacheObject cacheObject = CacheObject.newCacheObject(key, json, System.currentTimeMillis());
    cacheBddDataSource.persist(cacheObject);
  }
}
