package com.worldreader.core.datasource.storage.datasource.referrer;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import com.worldreader.core.application.di.qualifiers.ReferrerSharedPreferences;
import com.worldreader.core.datasource.ReferrerDataSource;
import com.worldreader.core.domain.model.Referrer;

import javax.inject.Inject;

public class ReferrerSharedPreferencesDataSourceImpl implements ReferrerDataSource {

  private SharedPreferences sharedPreferences;

  private static final String KEY_REFERRER_DEVICE_ID = "referrerDeviceId";
  private static final String KEY_REFERRER_USER_ID = "referrerUserId";

  @Inject public ReferrerSharedPreferencesDataSourceImpl(@ReferrerSharedPreferences SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  @SuppressLint("ApplySharedPref") @Override public void put(Referrer referrer) {
    this.sharedPreferences.edit()
        .putString(KEY_REFERRER_DEVICE_ID, referrer.getDeviceId())
        .putString(KEY_REFERRER_USER_ID, referrer.getUserId())
        .commit();
  }

  @Override public Referrer get() {
    return new Referrer(
        this.sharedPreferences.getString(KEY_REFERRER_DEVICE_ID, null),
        this.sharedPreferences.getString(KEY_REFERRER_USER_ID, null)
    );
  }
}
