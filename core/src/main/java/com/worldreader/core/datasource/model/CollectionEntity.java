package com.worldreader.core.datasource.model;

import com.google.gson.annotations.SerializedName;
import com.mobilejazz.vastra.strategies.timestamp.TimestampValidationStrategyDataSource;

import java.util.*;
import java.util.concurrent.*;

public class CollectionEntity implements TimestampValidationStrategyDataSource {

  @SerializedName("id") private int id;
  @SerializedName("name") private String name;
  @SerializedName("start") private Date start;
  @SerializedName("end") private Date end;
  @SerializedName("books") private List<BookEntity> books;
  private Date lastUpdate;

  public CollectionEntity() {
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

  public List<BookEntity> getBooks() {
    return books;
  }

  public void setBooks(List<BookEntity> books) {
    this.books = books;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  @Override public Date lastUpdate() {
    return lastUpdate;
  }

  @Override public long expiryTime() {
    return TimeUnit.HOURS.toMillis(24);
  }
}
