package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class UserGoogleRegisterBody extends BaseUserRegisterBody {

  @SerializedName("google_id") private String googleId;
  @SerializedName("name") private String name;
  @SerializedName("email") private String email;

  private UserGoogleRegisterBody(String googleId, String name, String email, String referrerDeviceId, String referrerUserId) {
    super(referrerDeviceId, referrerUserId);

    this.googleId = googleId;
    this.name = name;
    this.email = email;
  }

  public static UserGoogleRegisterBody create(String googleId, String name, String email, String referrerDeviceId, String referrerUserId) {
    return new UserGoogleRegisterBody(googleId, name, email, referrerDeviceId, referrerUserId);
  }
}
