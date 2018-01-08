package com.worldreader.core.datasource.storage.datasource.referrer;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import com.worldreader.core.application.di.qualifiers.ReferrerSharedPreferences;
import com.worldreader.core.datasource.ReferrerDataSource;
import com.worldreader.core.domain.model.Referrer;

import javax.inject.Inject;

public class ReferrerSharedPreferencesDataSourceImpl implements ReferrerDataSource {

  private SharedPreferences sharedPreferences;

  private static final String KEY_INVITATION_REFERRER = "invitationReferrer";

  @Inject public ReferrerSharedPreferencesDataSourceImpl(@ReferrerSharedPreferences SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  @SuppressLint("ApplySharedPref") @Override public void put(Referrer referrer) {
    this.sharedPreferences.edit().putString(KEY_INVITATION_REFERRER, referrer.formatAsUrlQueryValue()).commit();
  }

  @Override public Referrer get() {
    Referrer referrer = null;
    if (this.sharedPreferences.contains(KEY_INVITATION_REFERRER)) {
      try {
        referrer = Referrer.parse(this.sharedPreferences.getString(KEY_INVITATION_REFERRER, ""));
      } catch (Referrer.ReferrerParseException e) {
        // Won't happen
      }
    }
    return referrer;
  }
}
