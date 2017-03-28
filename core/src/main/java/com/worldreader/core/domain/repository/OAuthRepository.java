package com.worldreader.core.domain.repository;

import com.worldreader.core.domain.model.OAuthResponse;

public interface OAuthRepository {

  OAuthResponse applicationToken();

  OAuthResponse userToken();

  boolean login(String username, String password);

  boolean loginWithFacebook(String facebookToken);

  boolean loginWithGoogle(String googleId, String email);

  boolean putUserToken(String value);

  boolean removeUserToken();

  boolean removeApplicationToken();
}
