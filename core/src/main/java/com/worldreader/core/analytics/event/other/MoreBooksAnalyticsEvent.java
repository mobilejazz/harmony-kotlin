package com.worldreader.core.analytics.event.other;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class MoreBooksAnalyticsEvent implements AnalyticsEvent {

  private final String shelveId;
  private final String shelveTitle;
  private final String categoryTitle;

  public String getShelveId() {
    return shelveId;
  }

  public String getShelveTitle() {
    return shelveTitle;
  }

  public String getCategoryTitle() {
    return categoryTitle;
  }

  public String getCategoryId() {
    return categoryId;
  }

  private final String categoryId;

  public MoreBooksAnalyticsEvent(Builder builder) {
    shelveId = builder.shelveId;
    shelveTitle = builder.shelveTitle;
    categoryTitle = builder.categoryTitle;
    categoryId = builder.categoryId;
  }

  public static final class Builder {

    private String shelveId;
    private String shelveTitle;
    private String categoryTitle;
    private String categoryId;

    public Builder() {
    }

    public Builder withShelveId(String val) {
      shelveId = val;
      return this;
    }

    public Builder withShelveTitle(String val) {
      shelveTitle = val;
      return this;
    }

    public Builder withCategoryTitle(String val) {
      categoryTitle = val;
      return this;
    }

    public Builder withCategoryId(String val) {
      categoryId = val;
      return this;
    }

    public MoreBooksAnalyticsEvent build() {
      return new MoreBooksAnalyticsEvent(this);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(MoreBooksAnalyticsEvent copy) {
    Builder builder = new Builder();
    builder.shelveId = copy.getShelveId();
    builder.shelveTitle = copy.getShelveTitle();
    builder.categoryTitle = copy.getCategoryTitle();
    builder.categoryId = copy.getCategoryId();
    return builder;
  }
}
