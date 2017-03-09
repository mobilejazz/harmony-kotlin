package com.worldreader.core.datasource.storage.datasource.oauth;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.worldreader.core.datasource.network.model.OAuthNetworkResponseEntity;
import com.worldreader.core.datasource.storage.datasource.cache.CacheBddDataSource;
import com.worldreader.core.datasource.storage.datasource.cache.manager.entity.CacheObject;

import javax.inject.Inject;

public class OAuthBdDataSourceImpl implements OAuthBdDataSource {

  public static final String APPLICATION_TOKEN = "application.token";
  public static final String REFRESH_TOKEN = "refresh.token";
  public static final String USER_TOKEN = "user.token";

  private CacheBddDataSource cacheDataSource;
  private Gson gson;

  @Inject public OAuthBdDataSourceImpl(CacheBddDataSource cacheDataSource, Gson gson) {
    this.cacheDataSource = cacheDataSource;
    this.gson = gson;
  }

  @Override public OAuthNetworkResponseEntity applicationToken() {
    CacheObject cacheObject = cacheDataSource.get(APPLICATION_TOKEN);
    if (cacheObject == null) {
      return null;
    } else {
      return getOAuthResponse(cacheObject);
    }
  }

  @Override public OAuthNetworkResponseEntity refreshToken() {
    CacheObject cacheObject = cacheDataSource.get(REFRESH_TOKEN);
    return getOAuthResponse(cacheObject);
  }

  @Override public OAuthNetworkResponseEntity userToken() {
    CacheObject cacheObject = cacheDataSource.get(USER_TOKEN);
    if (cacheObject == null) {
      return null;
    } else {
      return getOAuthResponse(cacheObject);
    }
  }

  @Override public boolean persist(String key, OAuthNetworkResponseEntity body) {
    String json = gson.toJson(body);
    CacheObject cache = CacheObject.newCacheObject(key, json, System.currentTimeMillis());
    cacheDataSource.persist(cache);
    return true;
  }

  @Override public boolean remove(String key) {
    cacheDataSource.delete(key);
    return true;
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private method
  ///////////////////////////////////////////////////////////////////////////

  private OAuthNetworkResponseEntity getOAuthResponse(CacheObject cacheObject) {
    return gson.fromJson(cacheObject.getValue(), new TypeToken<OAuthNetworkResponseEntity>() {
    }.getType());
  }
}
