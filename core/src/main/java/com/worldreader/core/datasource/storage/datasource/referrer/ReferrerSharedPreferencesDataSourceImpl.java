package com.worldreader.core.datasource.storage.datasource.referrer;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import com.worldreader.core.application.di.qualifiers.ReferrerSharedPreferences;
import com.worldreader.core.datasource.ReferrerDataSource;
import com.worldreader.core.domain.model.Referrer;

import javax.inject.Inject;
import java.util.*;

public class ReferrerSharedPreferencesDataSourceImpl implements ReferrerDataSource {

  private SharedPreferences sharedPreferences;

  private static final String KEY_REFERRER_DEVICE_ID = "referrerDeviceId";
  private static final String KEY_REFERRER_USER_ID = "referrerUserId";
  


  @Inject public ReferrerSharedPreferencesDataSourceImpl(@ReferrerSharedPreferences SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  @SuppressLint("ApplySharedPref") @Override public void put(Referrer referrer) {
    referrer.getCampaign().keySet();
    this.sharedPreferences.edit()
        .putString(KEY_REFERRER_DEVICE_ID, referrer.getDeviceId())
        .putString(KEY_REFERRER_USER_ID, referrer.getUserId())
        .putString(Referrer.KEY_UTM_SOURCE, referrer.getCampaign().get(Referrer.KEY_UTM_SOURCE))
        .putString(Referrer.KEY_UTM_MEDIUM, referrer.getCampaign().get(Referrer.KEY_UTM_MEDIUM))
        .putString(Referrer.KEY_UTM_TERM, referrer.getCampaign().get(Referrer.KEY_UTM_TERM))
        .putString(Referrer.KEY_UTM_CONTENT, referrer.getCampaign().get(Referrer.KEY_UTM_CONTENT))
        .putString(Referrer.KEY_UTM_CAMPAIGN, referrer.getCampaign().get(Referrer.KEY_UTM_CAMPAIGN))
        .commit();
  }

  @Override public Referrer get() {
    Map<String, String> campaign = new HashMap<>();
    campaign.put(Referrer.KEY_UTM_SOURCE,this.sharedPreferences.getString(Referrer.KEY_UTM_SOURCE, null));
    campaign.put(Referrer.KEY_UTM_MEDIUM,this.sharedPreferences.getString(Referrer.KEY_UTM_MEDIUM, null));
    campaign.put(Referrer.KEY_UTM_TERM,this.sharedPreferences.getString(Referrer.KEY_UTM_TERM, null));
    campaign.put(Referrer.KEY_UTM_CONTENT,this.sharedPreferences.getString(Referrer.KEY_UTM_CONTENT, null));
    campaign.put(Referrer.KEY_UTM_CAMPAIGN,this.sharedPreferences.getString(Referrer.KEY_UTM_CAMPAIGN, null));

    return new Referrer(
        this.sharedPreferences.getString(KEY_REFERRER_DEVICE_ID, null),
        this.sharedPreferences.getString(KEY_REFERRER_USER_ID, null),campaign);
  }
}
