package com.worldreader.core.analytics.event.datasource;

import android.content.SharedPreferences;
import com.google.common.base.Optional;
import com.worldreader.core.analytics.models.UserInfoAnalyticsModel;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;

import javax.inject.Inject;
import java.util.*;

public class AnalyticsDataSource implements Repository<UserInfoAnalyticsModel, RepositorySpecification> {

  private static final String CLIENT_ID_KEY = "clientId";
  private static final String DEVICE_ID_KEY = "deviceId";
  private static final String USER_ID_KEY = "userId";

  private final SharedPreferences sharedPreferences;

  @Inject public AnalyticsDataSource(final SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  @Override public void get(final RepositorySpecification specification, final Callback<Optional<UserInfoAnalyticsModel>> callback) {
    final String clientId = sharedPreferences.getString(CLIENT_ID_KEY, "");
    final String deviceId = sharedPreferences.getString(DEVICE_ID_KEY, "");
    final String userId = sharedPreferences.getString(USER_ID_KEY, "");

    final UserInfoAnalyticsModel analyticsInfoModel = new UserInfoAnalyticsModel(clientId, deviceId, userId);

    if (callback != null) {
      callback.onSuccess(Optional.of(analyticsInfoModel));
    }
  }

  @Override public void getAll(final RepositorySpecification specification, final Callback<Optional<List<UserInfoAnalyticsModel>>> callback) {
    throw new UnsupportedOperationException("getAll() not supported");
  }

  @Override public void put(final UserInfoAnalyticsModel analyticsInfoModel,
      final RepositorySpecification specification,
      final Callback<Optional<UserInfoAnalyticsModel>> callback) {
    final SharedPreferences.Editor edit = sharedPreferences.edit();
    edit.putString(CLIENT_ID_KEY, analyticsInfoModel.clientId);
    edit.putString(DEVICE_ID_KEY, analyticsInfoModel.deviceId);
    edit.putString(USER_ID_KEY, analyticsInfoModel.userId);
    edit.apply();

    if (callback != null) {
      callback.onSuccess(Optional.of(analyticsInfoModel));
    }
  }

  @Override public void putAll(final List<UserInfoAnalyticsModel> analyticsInfoModels, final RepositorySpecification specification,
      final Callback<Optional<List<UserInfoAnalyticsModel>>> callback) {
    throw new UnsupportedOperationException("putAll() not supported");
  }

  @Override public void remove(final UserInfoAnalyticsModel analyticsInfoModel, final RepositorySpecification specification,
      final Callback<Optional<UserInfoAnalyticsModel>> callback) {
    final SharedPreferences.Editor edit = sharedPreferences.edit();
    edit.remove(USER_ID_KEY);
    edit.remove(DEVICE_ID_KEY);
    edit.remove(CLIENT_ID_KEY);
    edit.apply();

    if (callback != null) {
      callback.onSuccess(Optional.of(analyticsInfoModel));
    }
  }

  @Override public void removeAll(final List<UserInfoAnalyticsModel> analyticsInfoModels,
      final RepositorySpecification specification,
      final Callback<Optional<List<UserInfoAnalyticsModel>>> callback) {
    throw new UnsupportedOperationException("removeAll() not supported");
  }
}
