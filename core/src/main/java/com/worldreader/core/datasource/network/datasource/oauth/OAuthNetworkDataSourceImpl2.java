package com.worldreader.core.datasource.network.datasource.oauth;

import com.worldreader.core.common.helper.HttpStatus;
import com.worldreader.core.datasource.network.general.retrofit.services.AuthApiService2;
import com.worldreader.core.datasource.network.general.retrofit.services.OAuthApiService2;
import com.worldreader.core.datasource.network.model.OAuthFacebookBody;
import com.worldreader.core.datasource.network.model.OAuthGoogleBody;
import com.worldreader.core.datasource.network.model.OAuthNetworkBody;
import com.worldreader.core.datasource.network.model.OAuthNetworkResponseEntity;
import com.worldreader.core.error.user.LoginException;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.io.*;

public class OAuthNetworkDataSourceImpl2 implements OAuthNetworkDataSource {

  private final OAuthApiService2 oAuthApi;
  private final AuthApiService2 authApi;
  private final String clientId;
  private final String clientSecret;

  public OAuthNetworkDataSourceImpl2(OAuthApiService2 oAuthApiService, AuthApiService2 authApiService, String clientId, String clientSecret) {
    this.oAuthApi = oAuthApiService;
    this.authApi = authApiService;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  @Override public OAuthNetworkResponseEntity applicationToken() {
    final OAuthNetworkBody applicationToken = OAuthNetworkBody.createApplicationToken(clientId, clientSecret, OAuthNetworkBody.GRANT_TYPE_CLIENT);
    try {
      final Response<OAuthNetworkResponseEntity> response = oAuthApi.token(applicationToken).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        return response.body();
      } else {
        return null;
      }
    } catch (IOException e) {
      return null;
    }
  }

  @Override public OAuthNetworkResponseEntity refreshToken(final String refreshToken) {
    final OAuthNetworkBody refreshTokenBody =
        OAuthNetworkBody.createRefreshToken(clientId, clientSecret, OAuthNetworkBody.GRANT_TYPE_REFRESH_TOKEN, refreshToken);

    try {
      final Response<OAuthNetworkResponseEntity> response = oAuthApi.token(refreshTokenBody).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        return response.body();
      } else {
        final ResponseBody errorBody = response.errorBody();
        // TODO: 23/12/2016 We have to inspect what error could the server return and act accordingly
        return null;
      }
    } catch (IOException e) {
      // In this case the request could be canceled, so we should take that into account
      return null;
    }
  }

  @Override public OAuthNetworkResponseEntity userToken(final String username, final String password) throws LoginException {
    final OAuthNetworkBody userToken =
        OAuthNetworkBody.createUserToken(clientId, clientSecret, OAuthNetworkBody.GRANT_TYPE_PASSWORD, username, password);

    try {
      final Response<OAuthNetworkResponseEntity> response = oAuthApi.token(userToken).execute();
      final boolean successful = response.isSuccessful();
      final int code = response.code();
      if (successful) {
        return response.body();
      } else if (code == HttpStatus.BAD_REQUEST) {
        throw new LoginException(LoginException.Kind.INVALID_CREDENTIALS);
      } else {
        throw new LoginException(LoginException.Kind.UNKNOWN);
      }
    } catch (IOException error) {
      // In this case the request could be canceled or with timeout, so we should take that into account
      throw new LoginException(LoginException.Kind.UNKNOWN);
    }
  }

  @Override public OAuthNetworkResponseEntity userTokenWithFacebook(final String facebookToken) throws LoginException {
    final OAuthFacebookBody oAuthFacebookBody = OAuthFacebookBody.create(clientId, facebookToken);

    try {
      final Response<OAuthNetworkResponseEntity> response = authApi.userTokenWithFacebook(oAuthFacebookBody).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        return response.body();
      } else {
        final ResponseBody errorBody = response.errorBody();
        // TODO: 23/12/2016 We have to inspect what error could the server return and act accordingly
        return null;
      }
    } catch (IOException error) {
      // In this case the request could be canceled, so we should take that into account
      return null;
    }
  }

  @Override public OAuthNetworkResponseEntity userTokenWithGoogle(final String googleId, final String email) throws LoginException {
    final OAuthGoogleBody body = OAuthGoogleBody.create(clientId, null, googleId, email);

    try {
      final Response<OAuthNetworkResponseEntity> response = authApi.userTokenWithGoogle(body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        return response.body();
      } else {
        final ResponseBody errorBody = response.errorBody();
        // TODO: 23/12/2016 We have to inspect what error could the server return and act accordingly
        return null;
      }
    } catch (IOException e) {
      // In this case the request could be canceled, so we should take that into account
      return null;
    }
  }

  @Override public OAuthNetworkResponseEntity userTokenWithGoogleTokenId(final String googleTokenId) throws LoginException {
    final OAuthGoogleBody body = OAuthGoogleBody.create(clientId, googleTokenId, null, null);

    try {
      final Response<OAuthNetworkResponseEntity> response = authApi.userTokenWithGoogle(body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        return response.body();
      } else {
        final ResponseBody errorBody = response.errorBody();
        // TODO: 23/12/2016 We have to inspect what error could the server return and act accordingly
        return null;
      }
    } catch (IOException e) {
      // In this case the request could be canceled, so we should take that into account
      return null;
    }
  }

}
