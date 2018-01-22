package com.worldreader.core.datasource.storage.datasource.country;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import com.worldreader.core.datasource.CountryDetectionConfigurationDataSource;
import com.worldreader.core.domain.model.CountryDetectionConfiguration;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CountryDetectionConfigurationSharedPrefsDataSourceImpl implements CountryDetectionConfigurationDataSource {

  private SharedPreferences sharedPreferences;

  private static final String KEY_COUNTRY_DETECTION_FORCED_COUNTRY = "countryDetectionForcedCountry";

  @Inject public CountryDetectionConfigurationSharedPrefsDataSourceImpl(Context context) {
    this.sharedPreferences = context.getSharedPreferences("CountryDetectionConfiguration", Context.MODE_PRIVATE);
  }

  @Override public CountryDetectionConfiguration get() {
    String forcedCountry = sharedPreferences.getString(KEY_COUNTRY_DETECTION_FORCED_COUNTRY, null);

    return forcedCountry != null
           ? CountryDetectionConfiguration.forceCountry(forcedCountry)
           : CountryDetectionConfiguration.automatic();
  }

  @SuppressLint("ApplySharedPref") @Override public void put(CountryDetectionConfiguration countryDetectionConfiguration) {
    SharedPreferences.Editor editor = this.sharedPreferences.edit();
    if (countryDetectionConfiguration.isAutomatic()) {
      editor.remove(KEY_COUNTRY_DETECTION_FORCED_COUNTRY);
    } else {
      editor.putString(KEY_COUNTRY_DETECTION_FORCED_COUNTRY, countryDetectionConfiguration.getForcedCountry());
    }
    editor.commit();
  }

}
