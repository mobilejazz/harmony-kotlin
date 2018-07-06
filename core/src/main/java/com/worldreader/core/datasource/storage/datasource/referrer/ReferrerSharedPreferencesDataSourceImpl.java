package com.worldreader.core.datasource.storage.datasource.referrer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import com.worldreader.core.datasource.ReferrerDataSource;
import com.worldreader.core.domain.model.Referrer;
import javax.inject.Inject;
import java.util.*;

public class ReferrerSharedPreferencesDataSourceImpl implements ReferrerDataSource {

  private SharedPreferences sharedPreferences;

  private static final String KEY_REFERRER_DEVICE_ID = "referrerDeviceId";
  private static final String KEY_REFERRER_USER_ID = "referrerUserId";
  


  @Inject public ReferrerSharedPreferencesDataSourceImpl(Context c) {
    this.sharedPreferences = c.getSharedPreferences("wr-invitation-referrer", Context.MODE_PRIVATE);
  }

  @SuppressLint("ApplySharedPref") @Override public void put(Referrer referrer) {
    this.sharedPreferences.edit()
        .putString(KEY_REFERRER_DEVICE_ID, referrer.getDeviceId())
        .putString(KEY_REFERRER_USER_ID, referrer.getUserId())
        .putString(Referrer.KEY_UTM_SOURCE, (referrer.getCampaign()!=null) ? referrer.getCampaign().get(Referrer.KEY_UTM_SOURCE) : null)
        .putString(Referrer.KEY_UTM_MEDIUM, (referrer.getCampaign()!=null) ? referrer.getCampaign().get(Referrer.KEY_UTM_MEDIUM) : null)
        .putString(Referrer.KEY_UTM_TERM, (referrer.getCampaign()!=null) ? referrer.getCampaign().get(Referrer.KEY_UTM_TERM) : null)
        .putString(Referrer.KEY_UTM_CONTENT, (referrer.getCampaign()!=null) ? referrer.getCampaign().get(Referrer.KEY_UTM_CONTENT) : null)
        .putString(Referrer.KEY_UTM_CAMPAIGN, (referrer.getCampaign()!=null) ? referrer.getCampaign().get(Referrer.KEY_UTM_CAMPAIGN) : null)
        .putString(Referrer.KEY_ANID_CAMPAIGN, (referrer.getCampaign()!=null) ? referrer.getCampaign().get(Referrer.KEY_ANID_CAMPAIGN) : null)
        .putString(Referrer.REFERRER_RAW, referrer.getReferrerRaw())
        .commit();
  }

  @Override public Referrer get() {
    Map<String, String> campaign = new HashMap<>();
    campaign.put(Referrer.KEY_UTM_SOURCE,this.sharedPreferences.getString(Referrer.KEY_UTM_SOURCE, null));
    campaign.put(Referrer.KEY_UTM_MEDIUM,this.sharedPreferences.getString(Referrer.KEY_UTM_MEDIUM, null));
    campaign.put(Referrer.KEY_UTM_TERM,this.sharedPreferences.getString(Referrer.KEY_UTM_TERM, null));
    campaign.put(Referrer.KEY_UTM_CONTENT,this.sharedPreferences.getString(Referrer.KEY_UTM_CONTENT, null));
    campaign.put(Referrer.KEY_UTM_CAMPAIGN,this.sharedPreferences.getString(Referrer.KEY_UTM_CAMPAIGN, null));
    campaign.put(Referrer.KEY_ANID_CAMPAIGN,this.sharedPreferences.getString(Referrer.KEY_ANID_CAMPAIGN, null));

    return new Referrer(
        this.sharedPreferences.getString(KEY_REFERRER_DEVICE_ID, null),
        this.sharedPreferences.getString(KEY_REFERRER_USER_ID, null),campaign, this.sharedPreferences.getString(Referrer.REFERRER_RAW, null));
  }
}
