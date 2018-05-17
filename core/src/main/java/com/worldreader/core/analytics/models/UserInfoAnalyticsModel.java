package com.worldreader.core.analytics.models;

import com.worldreader.core.datasource.repository.model.RepositoryModel;

public class UserInfoAnalyticsModel extends RepositoryModel {

  public static final UserInfoAnalyticsModel EMPTY = new UserInfoAnalyticsModel("", "", "");

  public String clientId; // Compatibility with WRK (from Web to our app), we don't need in other apps

  public String deviceId;
  public String userId;

  public UserInfoAnalyticsModel(final String clientId, final String deviceId, final String userId) {
    this.clientId = clientId;
    this.deviceId = deviceId;
    this.userId = userId;
  }

  @Override public String getIdentifier() {
    return clientId + ':' + deviceId + ':' + userId;
  }
}
