package com.worldreader.core.analytics.event.register;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class SignUpAnalyticsEvent implements AnalyticsEvent {

  private final String username;
  private final String userId;
  private final String country;
  private final String date;
  private final String register;
  private final String referrerUserId;
  private final String referrerDeviceId;

  private SignUpAnalyticsEvent(Builder builder) {
    username = builder.username;
    userId = builder.userId;
    country = builder.country;
    date = builder.date;
    register = builder.register;
    referrerUserId = builder.referrerUserId;
    referrerDeviceId = builder.referrerDeviceId;

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

  public String getReferrerUserId() {
    return referrerUserId;
  }

  public String getReferrerDeviceId() {
    return referrerDeviceId;
  }

  public static final class Builder {
    private String username;
    private String userId;
    private String country;
    private String date;
    private String register;
    private String referrerUserId;
    private String referrerDeviceId;


    public Builder() {
    }

    public Builder withUsername(String val) {
      username = val;
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

    public Builder withReferrerUserId(String val) {
      referrerUserId = val;
      return this;
    }

    public Builder withReferrerDeviceId(String val) {
      referrerDeviceId = val;
      return this;
    }

    public SignUpAnalyticsEvent build() {
      return new SignUpAnalyticsEvent(this);
    }
  }
}
