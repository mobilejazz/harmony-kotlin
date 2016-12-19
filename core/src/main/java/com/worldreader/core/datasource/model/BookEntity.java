package com.worldreader.core.datasource.model;

import com.google.gson.annotations.SerializedName;
import com.mobilejazz.vastra.strategies.timestamp.TimestampValidationStrategyDataSource;

import java.util.*;
import java.util.concurrent.*;

public class BookEntity implements TimestampValidationStrategyDataSource {

  @SerializedName("id") private String id;
  @SerializedName("version") private String version;
  @SerializedName("language") private String language;
  @SerializedName("categories") private List<Integer> categories;
  @SerializedName("categoryNames") private HashMap<String, String> categoryNames;
  @SerializedName("title") private String title;
  @SerializedName("author") private String author;
  @SerializedName("publisher") private String publisher;
  @SerializedName("rights") private String rights;
  @SerializedName("published") private String published;
  @SerializedName("description") private String description;
  @SerializedName("score") private double score;
  @SerializedName("ratings") private double ratings;
  @SerializedName("cover") private String cover;
  @SerializedName("content") private String content;
  @SerializedName("size") private double size;
  @SerializedName("opens") private double opens;
  @SerializedName("offline") private boolean avalableToOfflineMode;
  private Date lastUpdate;

  public BookEntity() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public List<Integer> getCategories() {
    return categories;
  }

  public void setCategories(List<Integer> categories) {
    this.categories = categories;
  }

  public HashMap<String, String> getCategoryNames() {
    return categoryNames;
  }

  public void setCategoryNames(HashMap<String, String> categoryNames) {
    this.categoryNames = categoryNames;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public String getRights() {
    return rights;
  }

  public void setRights(String rights) {
    this.rights = rights;
  }

  public String getPublished() {
    return published;
  }

  public void setPublished(String published) {
    this.published = published;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }

  public double getRatings() {
    return ratings;
  }

  public void setRatings(double ratings) {
    this.ratings = ratings;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public double getSize() {
    return size;
  }

  public void setSize(double size) {
    this.size = size;
  }

  public double getOpens() {
    return opens;
  }

  public void setOpens(double opens) {
    this.opens = opens;
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

  public void setAvalableToOfflineMode(boolean available) {
    this.avalableToOfflineMode = available;
  }

  public boolean isAvalableToOfflineMode() {
    return avalableToOfflineMode;
  }

  public static BookEntity EMPTY = new BookEntity() {{
    setId("");
    setAuthor("");
    setCategories(new ArrayList<Integer>());
    setCategoryNames(new HashMap<String, String>());
    setContent("");
    setCover("");
    setDescription("");
    setLanguage("");
    setPublished("");
    setPublisher("");
    setOpens(0);
    setRatings(0);
    setRights("");
    setSize(0);
    setTitle("");
    setVersion("");
    setScore(0);
    setLastUpdate(new Date());
  }};
}
