package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class BaseUserRegisterBody {

  @SerializedName("referrer_device_id")
  String referrerDeviceId;

  @SerializedName("referrer_user_id")
  String referrerUserId;

  protected BaseUserRegisterBody(String referrerDeviceId, String referrerUserId) {
    this.referrerDeviceId = referrerDeviceId;
    this.referrerUserId = referrerUserId;
  }
}
