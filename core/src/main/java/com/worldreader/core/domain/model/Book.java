package com.worldreader.core.domain.model;

import java.io.*;
import java.util.*;

public class Book implements Serializable {

  private String id;
  private String version;
  private List<Category> categories;
  private String language;
  private String title;
  private String author;
  private String publisher;
  private String rights;
  private String published;
  private String description;
  private double score;
  private double ratings;
  private String cover;
  private String content;
  private double size;
  private double opens;
  private boolean isBookDownloaded;
  private boolean avalableToOfflineMode;

  public Book() {
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

  public List<Category> getCategories() {
    return categories;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
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
    return cover + "?size=480x800";
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

  public boolean isBookDownloaded() {
    return isBookDownloaded;
  }

  public void setBookDownloaded(boolean bookDownloaded) {
    isBookDownloaded = bookDownloaded;
  }

  /**
   * Return the cover url with the properly size.
   *
   * Sizes supported by the server:
   * - 60x80
   * - 120x160
   * - 240x320
   * - 480x800
   * - 720x1280
   * - 768x1280
   * - 1080x1920
   *
   * @return Cover url
   */
  public String getCoverUrlWithSize(int measuredWidth, int measuredHeight) {
    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(cover);
    urlBuilder.append("?size=");

    int height = 0, width = 0;

    if (measuredHeight < 60) {
      height = 60;
      width = 80;
    } else if (measuredHeight < 120) {
      height = 120;
      width = 160;
    } else if (measuredHeight < 240) {
      height = 240;
      width = 320;
    } else {
      height = 480;
      width = 800;
    }
    // To save bandwidth image size is limited to 480x800 maximum

    urlBuilder.append(height);
    urlBuilder.append("x");
    urlBuilder.append(width);

    return urlBuilder.toString();
  }

  public static Book createFakeBook() {
    Book book = new Book();
    book.setTitle("Fake Title");
    book.setCover("fake.cover");

    return book;
  }

  public boolean isFavorited() {
    return ratings > 10;
  }

  public void setAvailableToOfflineMode(boolean available) {
    this.avalableToOfflineMode = available;
  }

  public boolean isAvailableToOfflineMode() {
    return avalableToOfflineMode;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Book book = (Book) o;

    return id.equals(book.id);

  }

  @Override public int hashCode() {
    return id.hashCode();
  }
}
