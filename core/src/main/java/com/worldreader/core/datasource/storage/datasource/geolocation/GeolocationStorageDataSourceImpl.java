package com.worldreader.core.datasource.storage.datasource.geolocation;

import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.mobilejazz.vastra.ValidationService;
import com.worldreader.core.datasource.network.model.GeolocationInfoEntity;
import com.worldreader.core.datasource.storage.datasource.cache.CacheBddDataSource;
import com.worldreader.core.datasource.storage.datasource.cache.manager.entity.CacheObject;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;

import javax.inject.Inject;
import java.util.concurrent.atomic.*;

public final class GeolocationStorageDataSourceImpl implements GeolocationStorageDataSource {

  private static final String CACHE_KEY = "GeolocationInfo";

  private final CacheBddDataSource cacheBddDataSource;
  private final Gson gson;
  private final ValidationService validationService;

  private final AtomicReference<GeolocationInfoEntity> geolocationInfoEntityMemCache;

  @Inject
  public GeolocationStorageDataSourceImpl(CacheBddDataSource cacheBddDataSource, Gson gson, ValidationService validationService) {
    this.cacheBddDataSource = cacheBddDataSource;
    this.gson = gson;
    this.validationService = validationService;
    this.geolocationInfoEntityMemCache = new AtomicReference<>();

    // Warming mem cache
    refreshMemCacheFromDb();
  }

  private synchronized void refreshMemCacheFromDb() {
    final CacheObject cacheObject = getCacheObjectFromDb();
    if (cacheObject != null) {
      geolocationInfoEntityMemCache.set(getEntity(cacheObject));
    }
  }

  private @Nullable CacheObject getCacheObjectFromDb() {
    return cacheBddDataSource.get(CACHE_KEY);
  }

  private GeolocationInfoEntity getEntity(CacheObject cacheObject) {
    return gson.fromJson(cacheObject.getValue(), GeolocationInfoEntity.class);
  }

  // Not using cache validation to obtain the data, if the data exist will be returned
  @Override public GeolocationInfoEntity obtains() throws InvalidCacheException {
    final GeolocationInfoEntity geolocationInfoEntity = geolocationInfoEntityMemCache.get();
    if (geolocationInfoEntity == null) {
      throw new InvalidCacheException();
    }
    return geolocationInfoEntity;
  }

  @Override
  public boolean isValid() {
    CacheObject cacheObject = getCacheObjectFromDb();

    return cacheObject != null && validationService.isValid(getEntity(cacheObject));
  }

  @Override public void persist(GeolocationInfoEntity geolocationInfoEntity) {
    String json = gson.toJson(geolocationInfoEntity);
    CacheObject cacheObject = CacheObject.newCacheObject(CACHE_KEY, json, System.currentTimeMillis());
    cacheBddDataSource.persist(cacheObject);

    // Refreshing mem cache
    refreshMemCacheFromDb();
  }
}
