package com.worldreader.core.datasource;

import com.google.gson.Gson;
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

  public enum Token {
    APPLICATION, USER
  }

  private final OAuthNetworkDataSource networkDataSource;
  private final OAuthBdDataSource bdDataSource;
  private final OAuthEntityDataMapper mapper;
  private final Gson gson;

  @Inject
  public OAuthDataSource(OAuthNetworkDataSource networkDataSource, OAuthBdDataSource bdDataSource,
      OAuthEntityDataMapper mapper, final Gson gson) {
    this.networkDataSource = networkDataSource;
    this.bdDataSource = bdDataSource;
    this.mapper = mapper;
    this.gson = gson;
  }

  @Override public synchronized OAuthResponse applicationToken() {
    final OAuthNetworkResponseEntity token = getToken(Token.APPLICATION);
    return mapper.transform(token);
  }

  @Override public synchronized OAuthResponse userToken() {
    OAuthNetworkResponseEntity token = getToken(Token.USER);
    final boolean isExpired = isTokenExpired(token);
    if (!isExpired) {
      return mapper.transform(token);
    } else {
      // Refresh the token and return it
      token = refreshToken(Token.USER);
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
    final OAuthNetworkResponseEntity userToken =
        networkDataSource.userTokenWithFacebook(facebookToken);

    if (userToken == null) {
      return false;
    } else {
      bdDataSource.persist(OAuthBdDataSourceImpl.USER_TOKEN, userToken);
      return true;
    }
  }

  @Override public boolean loginWithGoogle(String googleId, String email) {
    final OAuthNetworkResponseEntity userToken =
        networkDataSource.userTokenWithGoogle(googleId, email);

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

  private OAuthNetworkResponseEntity refreshToken(Token type) {
    final OAuthNetworkResponseEntity token = getToken(type);
    if (type == Token.USER) {
      if (token == null || token.getRefreshToken() == null) {
        throw new IllegalStateException("User token is null");
      } else {
        final OAuthNetworkResponseEntity newUserToken =
            networkDataSource.refreshToken(token.getRefreshToken());
        bdDataSource.persist(OAuthBdDataSourceImpl.USER_TOKEN, newUserToken);
        return newUserToken;
      }
    } else {
      final OAuthNetworkResponseEntity newToken =
          networkDataSource.refreshToken(token.getRefreshToken());
      bdDataSource.persist(OAuthBdDataSourceImpl.REFRESH_TOKEN, newToken);
      return newToken;
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
