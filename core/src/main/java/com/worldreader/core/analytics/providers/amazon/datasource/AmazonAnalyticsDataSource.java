package com.worldreader.core.analytics.providers.amazon.datasource;

import android.content.SharedPreferences;
import com.google.common.base.Optional;
import com.worldreader.core.analytics.providers.amazon.model.AnalyticsInfoModel;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import java.util.List;
import javax.inject.Inject;

// TODO: 06/04/2017 @jose Move sharedpreference to a storage repository
public class AmazonAnalyticsDataSource implements Repository<AnalyticsInfoModel, RepositorySpecification> {

  private final SharedPreferences sharedPreferences;

  @Inject public AmazonAnalyticsDataSource(final SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  @Override public void get(final RepositorySpecification specification,
      final Callback<Optional<AnalyticsInfoModel>> callback) {
    final String clientId = sharedPreferences.getString(AnalyticsInfoModel.CLIENT_ID_KEY, "");
    final String deviceId = sharedPreferences.getString(AnalyticsInfoModel.DEVICE_ID_KEY, "");
    final String userId = sharedPreferences.getString(AnalyticsInfoModel.USER_ID_KEY, "");

    final AnalyticsInfoModel analyticsInfoModel =
        new AnalyticsInfoModel(clientId, deviceId, userId);

    if (callback != null) {
      callback.onSuccess(Optional.of(analyticsInfoModel));
    }
  }

  @Override public void getAll(final RepositorySpecification specification, final Callback<Optional<List<AnalyticsInfoModel>>> callback) {
    throw new UnsupportedOperationException("getAll() not supported");
  }

  @Override public void put(final AnalyticsInfoModel analyticsInfoModel,
      final RepositorySpecification specification,
      final Callback<Optional<AnalyticsInfoModel>> callback) {
    final SharedPreferences.Editor edit = sharedPreferences.edit();
    edit.putString(AnalyticsInfoModel.CLIENT_ID_KEY, analyticsInfoModel.getClientId());
    edit.putString(AnalyticsInfoModel.DEVICE_ID_KEY, analyticsInfoModel.getDeviceId());
    edit.putString(AnalyticsInfoModel.USER_ID_KEY, analyticsInfoModel.getUserId());
    edit.apply();

    if (callback != null) {
      callback.onSuccess(Optional.of(analyticsInfoModel));
    }
  }

  @Override public void putAll(final List<AnalyticsInfoModel> analyticsInfoModels, final RepositorySpecification specification,
      final Callback<Optional<List<AnalyticsInfoModel>>> callback) {
    throw new UnsupportedOperationException("putAll() not supported");
  }

  @Override public void remove(final AnalyticsInfoModel analyticsInfoModel, final RepositorySpecification specification,
      final Callback<Optional<AnalyticsInfoModel>> callback) {
    final SharedPreferences.Editor edit = sharedPreferences.edit();
    edit.remove(AnalyticsInfoModel.USER_ID_KEY);
    edit.remove(AnalyticsInfoModel.DEVICE_ID_KEY);
    edit.remove(AnalyticsInfoModel.CLIENT_ID_KEY);
    edit.apply();

    if (callback != null) {
      callback.onSuccess(Optional.of(analyticsInfoModel));
    }
  }

  @Override public void removeAll(final List<AnalyticsInfoModel> analyticsInfoModels,
      final RepositorySpecification specification,
      final Callback<Optional<List<AnalyticsInfoModel>>> callback) {
    throw new UnsupportedOperationException("removeAll() not supported");
  }
}
