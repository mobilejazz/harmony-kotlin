package com.worldreader.core.datasource.network.quality;

public enum ImageResourceQuality {
  LOW("_low"),
  MEDIUM("_medium"),
  ORIGINAL("");

  private String urlQualifier;

  ImageResourceQuality(String urlQualifier) {
    this.urlQualifier = urlQualifier;
  }

  public String getUrlQualifier() {
    return urlQualifier;
  }
}
