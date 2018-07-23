package com.worldreader.core.analytics.event.categories;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class CategorySelectedAnalyticsEvent implements AnalyticsEvent {

  private Integer categoryId;
  private String categoryName;
  private Integer parentCategoryId;
  private String parentCategoryName;
  private String referringScreen;
  private String referringMeta;
  private String countryCode;

  private CategorySelectedAnalyticsEvent(Integer categoryId, String name, Integer parentCategoryId, String parentCategoryName, String referringScreen,
      String referringMeta, String countryCode) {
    this.categoryId = categoryId;
    this.categoryName = name;
    this.parentCategoryId = parentCategoryId;
    this.parentCategoryName = parentCategoryName;
    this.referringScreen = referringScreen;
    this.referringMeta = referringMeta;
    this.countryCode = countryCode;
  }

  public static CategorySelectedAnalyticsEvent of(Integer categoryId, String name) {
    return new Builder(categoryId, name).create();
  }

  public Integer getCategoryId() {
    return categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public String getParentCategoryId() {
    if(parentCategoryId == null)
      return new String("");
    return String.valueOf(parentCategoryId);
  }

  public String getParentCategoryName() {
    return parentCategoryName;
  }

  public String getReferringScreen() {
    return referringScreen;
  }

  public String getReferringMeta() {
    return referringMeta;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public static final class Builder {

    private Integer categoryId;
    private String categoryName;
    private Integer parentCategoryId;
    private String parentCategoryName;
    private String referringScreen;
    private String referringMeta;
    private String countryCode;

    public Builder(Integer cId, String cName){
      categoryId = cId;
      categoryName = cName;
      parentCategoryName = "";
      parentCategoryId = null;
      referringScreen ="";
      referringMeta="";
      countryCode ="";

    }

    public Builder parentCategoryId(Integer val) {
      parentCategoryId = val;
      return this;
    }

    public Builder parentCategoryName(String val) {
      parentCategoryName = val;
      return this;
    }

    public Builder referringScreen(String val) {
      referringScreen = val;
      return this;
    }

    public Builder referringMeta(String val) {
      referringMeta = val;
      return this;
    }

    public Builder countryCode(String val) {
      countryCode = val;
      return this;
    }

    public CategorySelectedAnalyticsEvent create() {
      return new CategorySelectedAnalyticsEvent(categoryId, categoryName, parentCategoryId, parentCategoryName, referringScreen, referringMeta, countryCode);
    }
  }
}
