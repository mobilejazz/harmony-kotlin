package com.worldreader.core.datasource.storage.datasource.collection;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mobilejazz.vastra.ValidationService;
import com.worldreader.core.datasource.model.CollectionEntity;
import com.worldreader.core.datasource.storage.datasource.cache.CacheBddDataSource;
import com.worldreader.core.datasource.storage.datasource.cache.manager.entity.CacheObject;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;

import javax.inject.Inject;
import java.util.*;

public class CollectionBdDataSourceImp implements CollectionBdDataSource {

  private final CacheBddDataSource cacheBddDataSource;
  private final Gson gson;
  private final ValidationService validationService;

  @Inject public CollectionBdDataSourceImp(CacheBddDataSource cacheBddDataSource, Gson gson,
      ValidationService validationService) {
    this.cacheBddDataSource = cacheBddDataSource;
    this.gson = gson;
    this.validationService = validationService;
  }

  @Override public List<CollectionEntity> obtains(String key) throws InvalidCacheException {
    CacheObject cache = getCache(key);

    if (cache == null) {
      throw new InvalidCacheException();
    }

    List<CollectionEntity> collectionsEntity = getCollectionsEntity(cache);
    for (CollectionEntity collectionEntity : collectionsEntity) {
      if (!validationService.isValid(collectionEntity)) {
        throw new InvalidCacheException();
      }
    }

    return collectionsEntity;
  }

  @Override public CollectionEntity obtain(String key) throws InvalidCacheException {
    CacheObject cache = getCache(key);

    if (cache == null) {
      throw new InvalidCacheException();
    }

    CollectionEntity collectionEntity = getCollectionEntity(cache);

    if (!validationService.isValid(collectionEntity)) {
      throw new InvalidCacheException();
    }

    return collectionEntity;
  }

  @Override public void persist(String key, List<CollectionEntity> collectionEntities) {
    addLastUpdateToCollections(collectionEntities);
    String json = gson.toJson(collectionEntities);
    saveCache(key, json);
  }

  @Override public void persist(String key, CollectionEntity collectionEntity) {
    addLastUpdateToCollections(Arrays.asList(collectionEntity));
    String json = gson.toJson(collectionEntity);
    saveCache(key, json);
  }

  //region Private methods
  private CacheObject getCache(String key) {
    return cacheBddDataSource.get(key);
  }

  private void saveCache(String key, String json) {
    CacheObject cache = CacheObject.newCacheObject(key, json, System.currentTimeMillis());
    cacheBddDataSource.persist(cache);
  }

  private CollectionEntity getCollectionEntity(CacheObject cacheObject) {
    return gson.fromJson(cacheObject.getValue(), new TypeToken<CollectionEntity>() {
    }.getType());
  }

  private List<CollectionEntity> getCollectionsEntity(CacheObject cacheObject) {
    return gson.fromJson(cacheObject.getValue(), new TypeToken<List<CollectionEntity>>() {
    }.getType());
  }

  private void addLastUpdateToCollections(List<CollectionEntity> collectionEntities) {
    for (CollectionEntity collectionEntity : collectionEntities) {
      collectionEntity.setLastUpdate(new Date());
    }
  }
  //endregion
}
