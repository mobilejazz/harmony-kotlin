package com.worldreader.core.datasource.network.model;

public class BaseNetworkRegisterData {

  private String referrerDeviceId;
  private String referrerUserId;

  public BaseNetworkRegisterData(String referrerDeviceId, String referrerUserId) {
    this.referrerDeviceId = referrerDeviceId;
    this.referrerUserId = referrerUserId;
  }

  public String getReferrerDeviceId() {
    return referrerDeviceId;
  }

  public String getReferrerUserId() {
    return referrerUserId;
  }
}
