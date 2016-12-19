package com.worldreader.core.datasource.storage.datasource.banner;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mobilejazz.vastra.ValidationService;
import com.worldreader.core.datasource.model.BannerEntity;
import com.worldreader.core.datasource.storage.datasource.cache.CacheBddDataSource;
import com.worldreader.core.datasource.storage.datasource.cache.manager.entity.CacheObject;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;

import javax.inject.Inject;
import java.util.*;

public class BannerBdDataSourceImp implements BannerBdDataSource {

  private final CacheBddDataSource cacheBddDataSource;
  private final Gson gson;
  private final ValidationService validationService;

  @Inject public BannerBdDataSourceImp(CacheBddDataSource cacheBddDataSource, Gson gson,
      ValidationService validationService) {
    this.cacheBddDataSource = cacheBddDataSource;
    this.gson = gson;
    this.validationService = validationService;
  }

  @Override public List<BannerEntity> obtains(String key) throws InvalidCacheException {
    CacheObject cacheObject = cacheBddDataSource.get(key);

    if (cacheObject == null) {
      throw new InvalidCacheException();
    }

    List<BannerEntity> bannerEntities = getBannerEntities(cacheObject);

    for (BannerEntity bannerEntity : bannerEntities) {
      if (!validationService.isValid(bannerEntity)) {
        throw new InvalidCacheException();
      }
    }

    return bannerEntities;
  }

  @Override public void persist(String key, List<BannerEntity> banners) {
    addLastUpdateAttributeToBanners(banners);

    String json = gson.toJson(banners);
    CacheObject cacheObject = CacheObject.newCacheObject(key, json, System.currentTimeMillis());
    cacheBddDataSource.persist(cacheObject);
  }

  //region Private methods
  private List<BannerEntity> getBannerEntities(CacheObject cacheObject) {
    return gson.fromJson(cacheObject.getValue(), new TypeToken<List<BannerEntity>>() {
    }.getType());
  }

  private void addLastUpdateAttributeToBanners(List<BannerEntity> banners) {
    for (BannerEntity banner : banners) {
      banner.setLastUpdate(new Date());
    }
  }
  //endregion
}