package com.worldreader.core.analytics.event.register;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class SignUpAnalyticsEvent implements AnalyticsEvent {

  private final String email;

  private SignUpAnalyticsEvent(String email) {
    this.email = email;
  }

  private SignUpAnalyticsEvent(Builder builder) {
    email = builder.email;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private String email;

    private Builder() {
    }

    public Builder withEmail(String val) {
      email = val;
      return this;
    }

    public SignUpAnalyticsEvent build() {
      return new SignUpAnalyticsEvent(this);
    }
  }
}
