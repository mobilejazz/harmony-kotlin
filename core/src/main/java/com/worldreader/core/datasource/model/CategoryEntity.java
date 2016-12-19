package com.worldreader.core.datasource.model;

import com.google.gson.annotations.SerializedName;
import com.mobilejazz.vastra.strategies.timestamp.TimestampValidationStrategyDataSource;

import java.util.*;
import java.util.concurrent.*;

public class CategoryEntity implements TimestampValidationStrategyDataSource {

  @SerializedName("id") private int id;
  @SerializedName("languages") private List<String> languages;
  @SerializedName("title") private String title;
  @SerializedName("titleNol") private String titleNol;
  @SerializedName("description") private String description;
  @SerializedName("icon") private String icon;
  @SerializedName("color") private String color;
  @SerializedName("subcategories") private List<CategoryEntity> subCategoryEntities;
  private Date lastUpdate;

  public CategoryEntity() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public List<String> getLanguages() {
    return languages;
  }

  public void setLanguages(List<String> languages) {
    this.languages = languages;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitleNol() {
    return titleNol;
  }

  public void setTitleNol(String titleNol) {
    this.titleNol = titleNol;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public List<CategoryEntity> getSubCategoryEntities() {
    return subCategoryEntities;
  }

  public void setSubCategoryEntities(List<CategoryEntity> subCategoryEntities) {
    this.subCategoryEntities = subCategoryEntities;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  @Override public Date lastUpdate() {
    return lastUpdate;
  }

  @Override public long expiryTime() {
    return TimeUnit.DAYS.toMillis(30);
  }
}
