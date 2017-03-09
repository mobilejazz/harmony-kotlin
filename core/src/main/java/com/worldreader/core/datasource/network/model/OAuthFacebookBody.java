package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;
import com.worldreader.core.common.annotation.Immutable;

@Immutable public class OAuthFacebookBody {

  @SerializedName("client_id") private final String clientId;
  @SerializedName("facebook_id") private final String facebookToken;

  public static OAuthFacebookBody create(String clientId, String facebookToken) {
    return new OAuthFacebookBody(clientId, facebookToken);
  }

  private OAuthFacebookBody(String clientId, String facebookToken) {
    this.clientId = clientId;
    this.facebookToken = facebookToken;
  }

  public String getClientId() {
    return clientId;
  }

  public String getFacebookToken() {
    return facebookToken;
  }
}
