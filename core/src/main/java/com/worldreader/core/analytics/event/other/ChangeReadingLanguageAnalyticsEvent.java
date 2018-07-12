package com.worldreader.core.analytics.event.other;

import android.support.annotation.NonNull;
import com.worldreader.core.analytics.event.AnalyticsEvent;

public class ChangeReadingLanguageAnalyticsEvent implements AnalyticsEvent {

  private final String langCode;
  private final String lang;
  private final String country;

  public static ChangeReadingLanguageAnalyticsEvent of(String langCode, @NonNull String lang, @NonNull String country) {
    return new ChangeReadingLanguageAnalyticsEvent(langCode, lang, country);
  }

  private ChangeReadingLanguageAnalyticsEvent(String langCode, String lang, String country) {
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
