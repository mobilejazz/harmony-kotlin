package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class OAuthNetworkBody {

  public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
  public static final String GRANT_TYPE_CLIENT = "client_credentials";
  public static final String GRANT_TYPE_PASSWORD = "password";

  @SerializedName("client_id") private String clientId;
  @SerializedName("client_secret") private String clientSecret;
  @SerializedName("grant_type") private String grantType;
  @SerializedName("refresh_token") private String refreshToken;
  @SerializedName("username") private String username;
  @SerializedName("password") private String password;

  public OAuthNetworkBody(String clientId, String clientSecret, String grantType,
      String refreshToken, String username, String password) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.grantType = grantType;
    this.refreshToken = refreshToken;
    this.username = username;
    this.password = password;
  }

  public static OAuthNetworkBody createApplicationToken(String clientId, String clientSecret,
      String grantType) {
    return new OAuthNetworkBody(clientId, clientSecret, grantType, null/*refresh token*/, null /*username*/,
        null/*password*/);
  }

  public static OAuthNetworkBody createRefreshToken(String clientId, String clientSecret,
      String grantType, String refreshToken) {
    return new OAuthNetworkBody(clientId, clientSecret, grantType, refreshToken, null /*username*/,
        null/*password*/);
  }

  public static OAuthNetworkBody createUserToken(String clientId, String clientSecret,
      String grantType, String username, String password) {
    return new OAuthNetworkBody(clientId, clientSecret, grantType, null/*refresh token*/, username,
        password);
  }

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public String getGrantType() {
    return grantType;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
