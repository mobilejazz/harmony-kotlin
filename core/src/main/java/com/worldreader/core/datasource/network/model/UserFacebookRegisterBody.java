package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class UserFacebookRegisterBody {

  @SerializedName("facebook_id") private String facebookToken;

  private UserFacebookRegisterBody(String facebookToken) {
    this.facebookToken = facebookToken;
  }

  public static UserFacebookRegisterBody create(String facebookToken) {
    return new UserFacebookRegisterBody(facebookToken);
  }

  public String getFacebookToken() {
    return facebookToken;
  }
}
