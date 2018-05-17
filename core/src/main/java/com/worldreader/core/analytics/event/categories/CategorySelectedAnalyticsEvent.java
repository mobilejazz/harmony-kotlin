package com.worldreader.core.analytics.event.categories;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class CategorySelectedAnalyticsEvent implements AnalyticsEvent {

  private Integer categoryId;
  private String name;

  private CategorySelectedAnalyticsEvent(Integer categoryId, String name) {
    this.categoryId = categoryId;
    this.name = name;
  }

  public static CategorySelectedAnalyticsEvent of(Integer categoryId, String name) {
    return new CategorySelectedAnalyticsEvent(categoryId, name);
  }

  public Integer getCategoryId() {
    return categoryId;
  }

  public String getName() {
    return name;
  }
}
