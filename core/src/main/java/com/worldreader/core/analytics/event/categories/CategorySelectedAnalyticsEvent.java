package com.worldreader.core.analytics.event.categories;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class CategorySelectedAnalyticsEvent implements AnalyticsEvent {

  private Integer categoryId;
  private String categoryName;
  private Integer parentCategoryId;
  private String parentCategoryName;
  private String referringScreen;
  private String referringMeta;


  /**
   * attrs.put(PinpointMobileAnalyticsConstants.PARENT_CATEGORY_ID_ATTRIBUTE, "");
   attrs.put(PinpointMobileAnalyticsConstants.PARENT_CATEGORY_TITLE_ATTRIBUTE, "");

   * attrs.put(PinpointMobileAnalyticsConstants.REFERRING_SCREEN, referringScreen);
   * attrs.put(PinpointMobileAnalyticsConstants.REFERRING_META, meta.toString());
   * @param categoryId
   * @param name
   * @param parentCategoryId
   * @param parentCategoryName
   * @param referringScreen
   * @param referringMeta
   */


  private CategorySelectedAnalyticsEvent(Integer categoryId, String name, Integer parentCategoryId, String parentCategoryName, String referringScreen,
      String referringMeta) {
    this.categoryId = categoryId;
    this.categoryName = name;
    this.parentCategoryId = parentCategoryId;
    this.parentCategoryName = parentCategoryName;
    this.referringScreen = referringScreen;
    this.referringMeta = referringMeta;
  }

  public static CategorySelectedAnalyticsEvent of(Integer categoryId, String name) {
    return new Builder(categoryId, name).create();
  }

  public Integer getCategoryId() {
    return categoryId;
  }

  public String getName() {
    return categoryName;
  }


  public static final class Builder {

    private Integer categoryId;
    private String categoryName;
    private Integer parentCategoryId;
    private String parentCategoryName;
    private String referringScreen;
    private String referringMeta;

    public Builder(Integer cId, String cName){
      categoryId = cId;
      categoryName = cName;
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

    public CategorySelectedAnalyticsEvent create() {
      return new CategorySelectedAnalyticsEvent(categoryId, categoryName, parentCategoryId, parentCategoryName, referringScreen, referringMeta);
    }
  }
}
