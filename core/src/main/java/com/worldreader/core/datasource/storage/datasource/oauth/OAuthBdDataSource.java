package com.worldreader.core.datasource.storage.datasource.oauth;

import com.worldreader.core.datasource.network.model.OAuthNetworkResponseEntity;
import com.worldreader.core.error.user.OAuthTokenNotFoundException;

public interface OAuthBdDataSource {

  OAuthNetworkResponseEntity applicationToken() throws OAuthTokenNotFoundException;

  OAuthNetworkResponseEntity refreshToken();

  OAuthNetworkResponseEntity userToken();

  boolean persist(String key, OAuthNetworkResponseEntity body);

  boolean remove(String key);

}
