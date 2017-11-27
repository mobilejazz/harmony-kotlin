package com.worldreader.core.datasource.model;

public class GeolocationInfo {

  private double latitude;
  private double longitude;

  private String countryCode;

  public GeolocationInfo(double latitude, double longitude, String countryCode) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.countryCode = countryCode;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public String getCountryCode() {
    return countryCode;
  }

  @Override public String toString() {
    return "GeolocationInfo{" +
        "latitude=" + latitude +
        ", longitude=" + longitude +
        ", countryCode='" + countryCode + '\'' +
        '}';
  }
}
