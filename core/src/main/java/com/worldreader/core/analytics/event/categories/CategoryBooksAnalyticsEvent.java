package com.worldreader.core.analytics.event.categories;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class CategoryBooksAnalyticsEvent implements AnalyticsEvent {

  private String shelveId;
  private String shelveName;
  private String categoryScreen;

  public String getShelveId() {
    return shelveId;
  }

  public String getShelveName() {
    return shelveName;
  }

  public String getCategoryScreen() {
    return categoryScreen;
  }

  private CategoryBooksAnalyticsEvent(String shelveId, String shelveName, String categoryScreen) {
    this.shelveId = shelveId;
    this.shelveName = shelveName;
    this.categoryScreen = categoryScreen;
  }

  private CategoryBooksAnalyticsEvent(Builder builder) {
    shelveId = builder.shelveId;
    shelveName = builder.shelveName;
    categoryScreen = builder.categoryScreen;
  }


  public static final class Builder {

    private String shelveId;
    private String shelveName;
    private String categoryScreen;

    public Builder() {
    }

    public Builder shelveId(String val) {
      shelveId = val;
      return this;
    }

    public Builder shelveName(String val) {
      shelveName = val;
      return this;
    }

    public Builder categoryScreen(String val) {
      categoryScreen = val;
      return this;
    }

    public CategoryBooksAnalyticsEvent create() {
      return new CategoryBooksAnalyticsEvent(this);
    }
  }
}
