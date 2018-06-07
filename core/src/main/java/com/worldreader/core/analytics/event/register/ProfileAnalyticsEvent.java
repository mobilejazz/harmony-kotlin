package com.worldreader.core.analytics.event.register;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class ProfileAnalyticsEvent implements AnalyticsEvent {

  private final String username;
  private final String userId;
  private final String country;
  private final String date;
  private final String register;
  private final String language;

  private ProfileAnalyticsEvent(Builder builder) {
    username = builder.username;
    userId = builder.userId;
    country = builder.country;
    date = builder.date;
    register = builder.register;
    language = builder.language;

  }

  public static Builder builder() {
    return new Builder();
  }

  public String getUsername() {
    return username;
  }

  public String getUserId() {
    return userId;
  }

  public String getCountry() {
    return country;
  }

  public String getDate() {
    return date;
  }

  public String getRegister() {
    return register;
  }

  public String getLanguage() {
    return language;
  }

  public static final class Builder {
    private String username;
    private String userId;
    private String country;
    private String date;
    private String register;
    public String language;

    private Builder() {
    }

    public Builder withUsername(String user) {
      username = user;
      return this;
    }

    public Builder withUserId(String val) {
      userId = val;
      return this;
    }

    public Builder withCountry(String val) {
      country = val;
      return this;
    }

    public Builder withDate(String val) {
      date = val;
      return this;
    }

    public Builder withRegister(String val) {
      register = val;
      return this;
    }

    public Builder withLanguage(String val) {
      language = val;
      return this;
    }

    public ProfileAnalyticsEvent build() {
      return new ProfileAnalyticsEvent(this);
    }
  }
}
