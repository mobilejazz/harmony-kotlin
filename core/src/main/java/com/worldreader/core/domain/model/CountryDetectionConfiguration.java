package com.worldreader.core.domain.model;

public class CountryDetectionConfiguration {

  private final boolean automatic;
  private final String forcedCountry;

  private CountryDetectionConfiguration() {
    this.automatic = true;
    this.forcedCountry = null;
  }

  private CountryDetectionConfiguration(String forcedCountry) {
    this.automatic = false;
    this.forcedCountry = forcedCountry;
  }

  public static CountryDetectionConfiguration automatic() {
    return new CountryDetectionConfiguration();
  }

  public static CountryDetectionConfiguration forceCountry(String iso2CountryCode) {
    if (iso2CountryCode.length() != 2) throw new IllegalArgumentException("Wrong iso2Country code");

    return new CountryDetectionConfiguration(iso2CountryCode);
  }

  public boolean isAutomatic() {
    return automatic;
  }

  public String getForcedCountry() {
    return forcedCountry;
  }

  @Override public String toString() {
    return "CountryDetectionConfiguration{" +
        "automatic=" + automatic +
        ", forcedCountry='" + forcedCountry + '\'' +
        '}';
  }
}
