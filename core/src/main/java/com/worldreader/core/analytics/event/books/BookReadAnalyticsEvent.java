package com.worldreader.core.analytics.event.books;

import android.support.annotation.IntDef;
import com.worldreader.core.analytics.event.AnalyticsEvent;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.Category;
import java.util.List;

public class BookReadAnalyticsEvent implements AnalyticsEvent {

  public static final int ONLINE_VARIANT = 0;
  public static final int OFFLINE_VARIANT = 1;

  private final String id;
  private final String title;
  private final String category;
  private final String categoryId;
  private final String publisher;
  private final int variant;
  private final String version;

  public BookReadAnalyticsEvent(String id, String title, String category, String categoryId, String publisher, @Variant int variant, String version) {
    this.id = id;
    this.title = title;
    this.category = category;
    this.categoryId = categoryId;
    this.publisher = publisher;
    this.variant = variant;
    this.version = version;
  }

  public static BookStartReadingAnalyticsEvent of(Book book, boolean isDownloaded) {
    return new BookStartReadingAnalyticsEvent.Builder()
        .setId(book.getId())
        .setTitle(book.getTitle())
        .setPublisher(book.getPublisher())
        .setCategory(toCategory(book.getCategories()))
        .setCategoryId(toCategoryId(book.getCategories()))
        .setIsDownloaded(isDownloaded)
        .setVersion(book.getVersion())
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

  public String getCategory() {
    return category;
  }

  public String getCategoryId() {
    return categoryId;
  }

  public String getPublisher() {
    return publisher;
  }

  @BookStartReadingAnalyticsEvent.Variant public int getVariant() {
    return variant;
  }

  public String getVersion() {
    return version;
  }

  @IntDef(value = { ONLINE_VARIANT, OFFLINE_VARIANT }) @interface Variant {

  }

  public static class Builder {

    private String id;
    private String title;
    private String category;
    private String categoryId;
    private String brand;
    private int variant;
    private String version;

    public Builder setId(String id) {
      this.id = id;
      return this;
    }

    public Builder setTitle(String name) {
      this.title = name;
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

    public Builder setPublisher(String brand) {
      this.brand = brand;
      return this;
    }

    public Builder setIsDownloaded(boolean isDownloaded) {
      this.variant = isDownloaded ? OFFLINE_VARIANT : ONLINE_VARIANT;
      return this;
    }

    public Builder setVersion(String version) {
      this.version = version;
      return this;
    }

    public BookReadAnalyticsEvent create() {
      return new BookReadAnalyticsEvent(id, title, category, categoryId, brand, variant, version);
    }
  }
}
