package com.worldreader.core.analytics.providers.amazon.model;

import com.worldreader.core.datasource.repository.model.RepositoryModel;

public class AnalyticsInfoModel extends RepositoryModel {

  public static final AnalyticsInfoModel EMPTY = new AnalyticsInfoModel("", "", "");

  public static final String CLIENT_ID_KEY = "clientId";
  public static final String DEVICE_ID_KEY = "deviceId";
  public static final String USER_ID_KEY = "userId";

  private String clientId;
  private String deviceId;
  private String userId;

  public AnalyticsInfoModel(final String clientId, final String deviceId, final String userId) {
    this.clientId = clientId;
    this.deviceId = deviceId;
    this.userId = userId;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(final String clientId) {
    this.clientId = clientId;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(final String deviceId) {
    this.deviceId = deviceId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(final String userId) {
    this.userId = userId;
  }

  @Override public String getIdentifier() {
    return clientId + ':' + deviceId + ':' + userId;
  }
}
