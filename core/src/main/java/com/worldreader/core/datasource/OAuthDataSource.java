package com.worldreader.core.datasource;

import com.google.gson.Gson;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.datasource.mapper.OAuthEntityDataMapper;
import com.worldreader.core.datasource.network.datasource.oauth.OAuthNetworkDataSource;
import com.worldreader.core.datasource.network.model.OAuthNetworkResponseEntity;
import com.worldreader.core.datasource.storage.datasource.oauth.OAuthBdDataSource;
import com.worldreader.core.datasource.storage.datasource.oauth.OAuthBdDataSourceImpl;
import com.worldreader.core.domain.model.OAuthResponse;
import com.worldreader.core.domain.repository.OAuthRepository;

import javax.inject.Inject;
import java.util.*;

public class OAuthDataSource implements OAuthRepository {

  private static final String TAG = OAuthDataSource.class.getSimpleName();

  private final OAuthNetworkDataSource networkDataSource;
  private final OAuthBdDataSource bdDataSource;
  private final OAuthEntityDataMapper mapper;
  private final Gson gson;
  private final Logger logger;

  public enum Token {
    APPLICATION, USER
  }

  @Inject
  public OAuthDataSource(OAuthNetworkDataSource networkDataSource, OAuthBdDataSource bdDataSource, OAuthEntityDataMapper mapper, final Gson gson,
      Logger logger) {
    this.networkDataSource = networkDataSource;
    this.bdDataSource = bdDataSource;
    this.mapper = mapper;
    this.gson = gson;
    this.logger = logger;
  }

  @Override public synchronized OAuthResponse applicationToken() {
    final OAuthNetworkResponseEntity token = getToken(Token.APPLICATION);
    return mapper.transform(token);
  }

  @Override public synchronized OAuthResponse userToken() throws IllegalStateException {
    OAuthNetworkResponseEntity token = getToken(Token.USER);
    final boolean isExpired = isTokenExpired(token);
    if (!isExpired) {
      return mapper.transform(token);
    } else {
      // Refresh the token and return it
      logger.d(TAG, "Auto logout issue: token expired");
      token = refreshToken();

      return mapper.transform(token);
    }
  }

  @Override public boolean login(String username, String password) {
    final OAuthNetworkResponseEntity userToken = networkDataSource.userToken(username, password);

    if (userToken == null) {
      return false;
    } else {
      bdDataSource.persist(OAuthBdDataSourceImpl.USER_TOKEN, userToken);
      return true;
    }
  }

  @Override public boolean loginWithFacebook(String facebookToken) {
    final OAuthNetworkResponseEntity userToken = networkDataSource.userTokenWithFacebook(facebookToken);

    if (userToken == null) {
      return false;
    } else {
      bdDataSource.persist(OAuthBdDataSourceImpl.USER_TOKEN, userToken);
      return true;
    }
  }

  @Override public boolean loginWithGoogle(String googleId, String email) {
    final OAuthNetworkResponseEntity userToken = networkDataSource.userTokenWithGoogle(googleId, email);

    if (userToken == null) {
      return false;
    } else {
      bdDataSource.persist(OAuthBdDataSourceImpl.USER_TOKEN, userToken);
      return true;
    }
  }

  @Override public boolean putUserToken(final String value) {
    final OAuthNetworkResponseEntity token = gson.fromJson(value, OAuthNetworkResponseEntity.class);

    if (token == null) {
      return false;
    } else {
      bdDataSource.persist(OAuthBdDataSourceImpl.USER_TOKEN, token);
      return true;
    }
  }

  @Override public boolean removeUserToken() {
    return bdDataSource.remove(OAuthBdDataSourceImpl.USER_TOKEN);
  }

  @Override public boolean removeApplicationToken() {
    return bdDataSource.remove(OAuthBdDataSourceImpl.APPLICATION_TOKEN);
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private OAuthNetworkResponseEntity refreshToken() throws IllegalStateException {
    final OAuthNetworkResponseEntity token = getToken(Token.USER);
    logger.d(TAG, "Auto logout issue: old token = " + token);
    if (token == null || token.getRefreshToken() == null) {
      throw new IllegalStateException("User token is null");
    } else {
      final OAuthNetworkResponseEntity newUserToken = networkDataSource.refreshToken(token.getRefreshToken());
      logger.d(TAG, "Auto logout issue: new token = " + newUserToken);
      if (newUserToken == null) {
        logger.d(TAG, "Auto logout issue: returning expired token");
        return token;
      } else {
        if (newUserToken.getRefreshToken() == null) { // If the refresh token is not provided the old one should be still valid
          logger.d(TAG, "Auto logout issue: refresh token not provided, using the old one");
          newUserToken.setRefreshToken(token.getRefreshToken());
        }
      }
      bdDataSource.persist(OAuthBdDataSourceImpl.USER_TOKEN, newUserToken);
      return newUserToken;
    }
  }

  private OAuthNetworkResponseEntity getToken(Token type) {
    switch (type) {
      case APPLICATION:
        return networkDataSource.applicationToken();
      case USER:
        return bdDataSource.userToken();
      default:
        throw new IllegalArgumentException("Token type should be of Application or User token");
    }
  }

  private boolean isTokenExpired(OAuthNetworkResponseEntity token) {
    if (token == null) {
      return true;
    }

    // Get the creation date to compare
    final Date creationDate = token.getCreationDate();

    // Diff the creation date with the current date in millis to know the difference.
    final long diff = System.currentTimeMillis() - creationDate.getTime();

    // Token expired in milliseconds
    final long tokenExpiredInMillis = token.getExpiresIn() * 1000;

    // Compare the difference with the expires in
    return diff > tokenExpiredInMillis;
  }
}
