package com.worldreader.core.datasource.model;

import com.google.gson.annotations.SerializedName;
import com.mobilejazz.vastra.strategies.timestamp.TimestampValidationStrategyDataSource;

import java.util.*;
import java.util.concurrent.*;

public class BannerEntity implements TimestampValidationStrategyDataSource {

  @SerializedName("id") private int id;
  @SerializedName("name") private String name;
  @SerializedName("start") private Date start;
  @SerializedName("end") private Date end;
  @SerializedName("image") private String image;
  @SerializedName("type") private String type;
  @SerializedName("books") private List<BookEntity> bookEntities;

  private Date lastUpdate;

  public BannerEntity() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getStart() {
    return start;
  }

  public void setStart(Date start) {
    this.start = start;
  }

  public Date getEnd() {
    return end;
  }

  public void setEnd(Date end) {
    this.end = end;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public List<BookEntity> getBookEntities() {
    return bookEntities;
  }

  public void setBookEntities(List<BookEntity> bookEntities) {
    this.bookEntities = bookEntities;
  }

  @Override public Date lastUpdate() {
    return lastUpdate;
  }

  @Override public long expiryTime() {
    return TimeUnit.HOURS.toMillis(24);
  }

}
