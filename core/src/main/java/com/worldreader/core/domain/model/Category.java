package com.worldreader.core.domain.model;

import java.io.*;
import java.util.*;

public class Category implements Serializable {

  private int id;
  private String title;
  private String color;
  private String icon;
  private List<Category> subCategories;

  private int iconRes;

  public static final int QUICK_READS = 7;
  public static final int CRITICS_PICKS = 68;
  public static final int LOVE = 6;
  public static final int CHILDREN = 16;
  public static final int YOUNG_ADULT = 105;
  public static final int INSPIRE = 60;
  public static final int LEARN = 4;
  public static final int HEALTH = 5;
  public static final int WORSHIP = 2;
  public static final int THRILLER = 8;
  public static final int SPORTS = 80;
  public static final int CAREER = 25;
  public static final int FANTASY = 89;
  public static final int POETRY = 86;
  public static final int MORE_BOOKS = 94;

  public Category() {
  }

  public Category(int id, String title) {
    this(id, title, null);
  }

  public Category(int id, String title, String icon) {
    this.id = id;
    this.title = title;
    this.icon = icon;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public List<Category> getSubCategories() {
    return subCategories;
  }

  /**
   * Get all subcategories that have this category as ancestor
   * @return
   */
  public List<Category> getAllSubcategoryLevels() {
    return this.getAllSubcategoryLevels(this.subCategories);
  }

  private List<Category> getAllSubcategoryLevels(List<Category> categories) {
    List<Category> allSubcategories = new ArrayList<>();
    for (Category category : categories) {
      allSubcategories.add(category);
      allSubcategories.addAll(getAllSubcategoryLevels(category.getSubCategories()));
    }

    return allSubcategories;
  }

  public void setSubCategories(List<Category> subCategories) {
    this.subCategories = subCategories;
  }

  public int getIconRes() {
    return iconRes;
  }

  public void setIconRes(int iconRes) {
    this.iconRes = iconRes;
  }

  @Override public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Category category = (Category) o;
    return id == category.id;
  }

  @Override public int hashCode() {
    return id;
  }
}
