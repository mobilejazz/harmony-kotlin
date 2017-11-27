package com.worldreader.core.datasource.storage.datasource.userflow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.worldreader.core.datasource.model.UserFlowEntity;
import com.worldreader.core.datasource.storage.datasource.cache.CacheBddDataSource;
import com.worldreader.core.datasource.storage.datasource.cache.manager.entity.CacheObject;

import javax.inject.Inject;
import java.util.*;

public class UserFlowBdDataSourceImpl implements UserFlowBdDataSource {

  private static final String USER_FLOW_KEY = "user.flow.key";

  private final CacheBddDataSource cache;
  private final Gson gson;

  @Inject public UserFlowBdDataSourceImpl(CacheBddDataSource cache, Gson gson) {
    this.cache = cache;
    this.gson = gson;
  }

  @Override public void persist(UserFlowEntity.Type type, List<UserFlowEntity> values) {
    String json = gson.toJson(values);

    CacheObject cacheObject =
        CacheObject.newCacheObject(type.name(), json, System.currentTimeMillis());

    cache.persist(cacheObject);
  }

  @Override public List<UserFlowEntity> getUserFlow(UserFlowEntity.Type type) {
    CacheObject cacheObject = cache.get(type.name());

    if (cacheObject == null) {
      return null;
    }

    return gson.fromJson(cacheObject.getValue(), new TypeToken<List<UserFlowEntity>>() {
    }.getType());
  }
}
