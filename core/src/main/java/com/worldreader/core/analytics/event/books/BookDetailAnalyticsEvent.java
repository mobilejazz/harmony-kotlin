package com.worldreader.core.analytics.event.books;

import com.worldreader.core.analytics.event.AnalyticsEvent;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.Category;
import java.util.List;

public class BookDetailAnalyticsEvent implements AnalyticsEvent {

  private final String id;
  private final String title;
  private final String version;
  private final String category;
  private final String categoryId;
  private final String publisher;
  private final String author;

  private BookDetailAnalyticsEvent(String id, String title, String version, String category, String categoryId, String publisher, String author) {
    this.id = id;
    this.title = title;
    this.version = version;
    this.category = category;
    this.categoryId = categoryId;
    this.publisher = publisher;
    this.author = author;
  }

  public static BookDetailAnalyticsEvent of(Book book) {
    return new Builder()
        .setId(book.getId())
        .setTitle(book.getTitle())
        .setVersion(book.getVersion())
        .setCategory(toCategory(book.getCategories()))
        .setCategoryId(toCategoryId(book.getCategories()))
        .setPublisher(book.getPublisher())
        .setAuthor(book.getAuthor())
        .create();
  }

  private static String toCategory(List<Category> categories) {
    if (categories == null || categories.isEmpty()) {
      return "";
    }

    return categories.get(0).getTitle();
  }

  private static String toCategoryId(List<Category> categories) {
    if (categories == null || categories.isEmpty()) {
      return "";
    }

    return String.valueOf(categories.get(0).getId());
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getVersion() {
    return version;
  }

  public String getCategory() {
    return category;
  }

  public String getCategoryId() {
    return categoryId;
  }

  public String getPublisher() {
    return publisher;
  }

  public String getAuthor() {
    return author;
  }

  public static class Builder {

    private String id;
    private String title;
    private String version;
    private String category;
    private String categoryId;
    private String publisher;
    private String author;

    public Builder setId(String id) {
      this.id = id;
      return this;
    }

    public Builder setTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder setVersion(String version) {
      this.version = version;
      return this;
    }

    public Builder setCategory(String category) {
      this.category = category;
      return this;
    }

    public Builder setCategoryId(String categoryId) {
      this.categoryId = categoryId;
      return this;
    }

    public Builder setPublisher(String publisher) {
      this.publisher = publisher;
      return this;
    }

    public Builder setAuthor(String author) {
      this.author = author;
      return this;
    }

    public BookDetailAnalyticsEvent create() {
      return new BookDetailAnalyticsEvent(id, title, version, category, categoryId, publisher, author);
    }
  }
}
