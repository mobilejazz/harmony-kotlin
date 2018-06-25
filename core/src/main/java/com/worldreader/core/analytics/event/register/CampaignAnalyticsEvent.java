package com.worldreader.core.analytics.event.register;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class CampaignAnalyticsEvent implements AnalyticsEvent {

  private final String utmSource;
  private final String utmMedium;
  private final String utmTerm;
  private final String utmContent;
  private final String utmCampaign;
  private final String anId;
  private final String referrerDeviceId;
  private final String referrerUserId;

  public CampaignAnalyticsEvent(Builder builder){
    this.utmSource = builder.utmSource;
    this.utmMedium = builder.utmMedium;
    this.utmTerm = builder.utmTerm;
    this.utmContent = builder.utmContent;
    this.utmCampaign = builder.utmCampaign;
    this.anId = builder.anId;
    this.referrerDeviceId = builder.referrerDeviceId;
    this.referrerUserId = builder.referrerUserId;
  }

  public String getUtmSource() {
    return utmSource;
  }

  public String getUtmMedium() {
    return utmMedium;
  }

  public String getUtmTerm() {
    return utmTerm;
  }

  public String getUtmContent() {
    return utmContent;
  }

  public String getUtmCampaign() {
    return utmCampaign;
  }

  public String getAnId() {
    return anId;
  }

  public String getReferrerDeviceId() {
    return referrerDeviceId;
  }

  public String getReferrerUserId() {
    return referrerUserId;
  }

  public static final class Builder {
    private String utmSource;
    private String utmMedium;
    private String utmTerm;
    private String utmContent;
    private String utmCampaign;
    private String anId;
    private String referrerDeviceId;
    private String referrerUserId;


    public Builder() {
    }

    public Builder withUtmSource(String val) {
      this.utmSource = val;
      return this;
    }

    public Builder withUtmMedium(String val) {
      this.utmMedium = val;
      return this;
    }

    public Builder withUtmTerm(String val) {
      this.utmTerm = val;
      return this;
    }

    public Builder withUtmContent(String val) {
      this.utmContent = val;
      return this;
    }

    public Builder withUtmCampaign(String val) {
      this.utmCampaign = val;
      return this;
    }

    public Builder withAnId(String val) {
      this.anId = val;
      return this;
    }

    public Builder withReferrerDeviceId(String val) {
      this.referrerDeviceId = val;
      return this;
    }

    public Builder withReferrerUserId(String val) {
      this.referrerUserId = val;
      return this;
    }

    public CampaignAnalyticsEvent build() {
      return new CampaignAnalyticsEvent(this);
    }
  }
}
