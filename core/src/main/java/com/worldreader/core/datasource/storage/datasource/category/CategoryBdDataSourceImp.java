package com.worldreader.core.datasource.storage.datasource.category;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mobilejazz.vastra.ValidationService;
import com.worldreader.core.datasource.model.CategoryEntity;
import com.worldreader.core.datasource.storage.datasource.cache.CacheBddDataSource;
import com.worldreader.core.datasource.storage.datasource.cache.manager.entity.CacheObject;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;

import javax.inject.Inject;
import java.util.*;

public class CategoryBdDataSourceImp implements CategoryBdDataSource {

  private final CacheBddDataSource cacheBddDataSource;
  private final Gson gson;
  private final ValidationService validationService;

  @Inject public CategoryBdDataSourceImp(CacheBddDataSource cacheBddDataSource, Gson gson,
      ValidationService validationService) {
    this.cacheBddDataSource = cacheBddDataSource;
    this.gson = gson;
    this.validationService = validationService;
  }

  @Override public List<CategoryEntity> obtains(String key) throws InvalidCacheException {
    CacheObject cacheObject = cacheBddDataSource.get(key);

    if (cacheObject == null) {
      throw new InvalidCacheException();
    }

    List<CategoryEntity> categoryEntities = getCategoryEntities(cacheObject);
    for (CategoryEntity categoryEntity : categoryEntities) {
      if (!validationService.isValid(categoryEntity)) {
        throw new InvalidCacheException();
      }
    }

    return categoryEntities;
  }

  @Override public void persist(String key, List<CategoryEntity> categories) {
    addLastUpdateToCategories(categories);

    String json = gson.toJson(categories);
    CacheObject cacheObject = CacheObject.newCacheObject(key, json, System.currentTimeMillis());
    cacheBddDataSource.persist(cacheObject);
  }

  //region Private methods
  private List<CategoryEntity> getCategoryEntities(CacheObject cacheObject) {
    return gson.fromJson(cacheObject.getValue(), new TypeToken<List<CategoryEntity>>() {
    }.getType());
  }

  private void addLastUpdateToCategories(List<CategoryEntity> categories) {
    for (CategoryEntity category : categories) {
      category.setLastUpdate(new Date());
    }
  }
  //endregion
}
