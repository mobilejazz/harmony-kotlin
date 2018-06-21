package com.worldreader.core.analytics.event.register;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class CampaignAnalyticsEvent implements AnalyticsEvent {

  private static final String KEY_UTM_SOURCE = "utm_source";
  private static final String KEY_UTM_MEDIUM = "utm_medium";
  private static final String KEY_UTM_TERM = "utm_term";
  private static final String KEY_UTM_CONTENT = "utm_content";
  private static final String KEY_UTM_CAMPAIGN = "utm_campaign";

  private final String utmSource;
  private final String utmMedium;
  private final String utmTerm;
  private final String utmContent;
  private final String utmCampaign;

  public CampaignAnalyticsEvent(Builder builder){
    this.utmSource = builder.utmSource;
    this.utmMedium = builder.utmMedium;
    this.utmTerm = builder.utmTerm;
    this.utmContent = builder.utmContent;
    this.utmCampaign = builder.utmCampaign;
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

  public static final class Builder {
    private String utmSource;
    private String utmMedium;
    private String utmTerm;
    private String utmContent;
    private String utmCampaign;


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

    public CampaignAnalyticsEvent build() {
      return new CampaignAnalyticsEvent(this);
    }
  }
}
