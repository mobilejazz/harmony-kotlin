package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;
import com.worldreader.core.common.annotation.Immutable;

@Immutable public class OAuthGoogleBody {

  @SerializedName("client_id") private final String clientId;
  @SerializedName("google_id") private final String googleId;
  @SerializedName("email") private final String email;

  public static OAuthGoogleBody create(String clientId, String googleId, String email) {
    return new OAuthGoogleBody(clientId, googleId, email);
  }

  private OAuthGoogleBody(String clientId, String googleId, String email) {
    this.clientId = clientId;
    this.googleId = googleId;
    this.email = email;
  }

  public String getClientId() {
    return clientId;
  }

  public String getGoogleId() {
    return googleId;
  }

  public String getEmail() {
    return email;
  }
}
