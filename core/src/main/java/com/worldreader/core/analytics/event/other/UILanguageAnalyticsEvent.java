package com.worldreader.core.analytics.event.other;

import android.support.annotation.NonNull;
import com.worldreader.core.analytics.event.AnalyticsEvent;

public class UILanguageAnalyticsEvent implements AnalyticsEvent {

  private final String langCode;
  private final String lang;
  private final String country;


  public static UILanguageAnalyticsEvent of(@NonNull String langCode, @NonNull String lang, @NonNull String country) {
    return new UILanguageAnalyticsEvent(langCode, lang, country);
  }

  private UILanguageAnalyticsEvent(String langCode, String lang, String country) {
    this.langCode = langCode;
    this.lang = lang;
    this.country = country;

  }

  public String getLang() {
    return lang;
  }

  public String getCountry() {
    return country;
  }

  public String getLangCode() {
    return langCode;
  }


}
