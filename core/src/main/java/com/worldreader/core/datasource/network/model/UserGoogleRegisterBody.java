package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class UserGoogleRegisterBody {

  @SerializedName("google_id") private String googleId;
  @SerializedName("name") private String name;
  @SerializedName("email") private String email;

  private UserGoogleRegisterBody(String googleId, String name, String email) {
    this.googleId = googleId;
    this.name = name;
    this.email = email;
  }

  public static UserGoogleRegisterBody create(String googleId, String name, String email) {
    return new UserGoogleRegisterBody(googleId, name, email);
  }

  public String getGoogleId() {
    return googleId;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }
}
