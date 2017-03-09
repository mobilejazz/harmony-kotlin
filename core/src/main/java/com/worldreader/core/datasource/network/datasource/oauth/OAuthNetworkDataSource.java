package com.worldreader.core.datasource.network.datasource.oauth;

import com.worldreader.core.datasource.network.model.OAuthNetworkResponseEntity;
import com.worldreader.core.error.user.LoginException;

public interface OAuthNetworkDataSource {

  OAuthNetworkResponseEntity applicationToken();

  OAuthNetworkResponseEntity refreshToken(String refreshToken);

  OAuthNetworkResponseEntity userToken(String username, String password) throws LoginException;

  OAuthNetworkResponseEntity userTokenWithFacebook(String facebookToken) throws LoginException;

  OAuthNetworkResponseEntity userTokenWithGoogle(String googleId, String email)
      throws LoginException;
}
