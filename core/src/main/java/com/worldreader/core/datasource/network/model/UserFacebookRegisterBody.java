package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class UserFacebookRegisterBody extends BaseUserRegisterBody {

  @SerializedName("facebook_id") private String facebookToken;

  private UserFacebookRegisterBody(String facebookToken, String referrerDeviceId, String referrerUserId) {
    super(referrerDeviceId, referrerUserId);

    this.facebookToken = facebookToken;
  }

  public static UserFacebookRegisterBody create(String facebookToken, String referrerDeviceId, String referrerUserId) {
    return new UserFacebookRegisterBody(facebookToken, referrerDeviceId, referrerUserId);
  }
}
