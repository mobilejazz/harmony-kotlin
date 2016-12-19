package com.worldreader.core.datasource.helper.locale;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import javax.inject.Inject;
import java.util.*;

public class LocaleCountryCodeProvider implements CountryCodeProvider {

  private Context context;

  @Inject public LocaleCountryCodeProvider(Context context) {
    this.context = context;
  }

  @Override public String getCountryCode() {
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    String countryISO = tm.getSimCountryIso();

    // Trick to make possible work with the emulator.
    if (TextUtils.isEmpty(countryISO)) {
      countryISO = "US";
    }

    return countryISO;
  }

  @Override public String getCountryIso3Code() {
    return context.getResources().getConfiguration().locale.getISO3Country();
  }

  @Override public String getLanguageIso3Code() {
    return Locale.getDefault().getISO3Language();
  }

  @Override public String getDisplayLanguage() {
    return Locale.getDefault().getDisplayLanguage();
  }

  @Override public String getDisplayLanguage(String languageIsoCode3) {
    Locale[] availableLocales = Locale.getAvailableLocales();
    for (Locale availableLocale : availableLocales) {
      if (availableLocale.getISO3Language().equalsIgnoreCase(languageIsoCode3)) {
        return availableLocale.getDisplayLanguage();
      }
    }
    return null;
  }
}
