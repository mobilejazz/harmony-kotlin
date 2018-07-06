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
  private final int pagesForResouce;
  private final int currentPage;
  private final CharSequence text;
  private final int tocSize;
  private final int spineSize;
  private final int spinePosition;
  private final int textSizeInChars;
  private final String country;

  public BookReadAnalyticsEvent(String id, String title, String category, String categoryId, String publisher, @Variant int variant, String version,
      int pagesForResouce, int currentPage, CharSequence text, int tocSize, int spineSize, int spinePosition, int textSizeInChars, String country) {
    this.id = id;
    this.title = title;
    this.category = category;
    this.categoryId = categoryId;
    this.publisher = publisher;
    this.variant = variant;
    this.version = version;
    this.pagesForResouce = pagesForResouce;
    this.currentPage = currentPage;
    this.text = text;
    this.tocSize = tocSize;
    this.spineSize = spineSize;
    this.spinePosition = spinePosition;
    this.textSizeInChars = textSizeInChars;
    this.country = country;
  }

  public static BookReadAnalyticsEvent of(Book book, boolean isDownloaded, int pagesForResouce, int currentPage, CharSequence text, int tocSize, int
      spineSize, int spinePosition, int textSizeInChars, String country) {
    return new Builder()
        .setId(book.getId())
        .setTitle(book.getTitle())
        .setPublisher(book.getPublisher())
        .setCategory(toCategory(book.getCategories()))
        .setCategoryId(toCategoryId(book.getCategories()))
        .setIsDownloaded(isDownloaded)
        .setVersion(book.getVersion())
        .setPagesForResouce(pagesForResouce)
        .setCurrentPage(currentPage)
        .setText(text).setTocSize(tocSize)
        .setSpineSize(spineSize)
        .setSpinePosition(spinePosition)
        .setTextSizeInChars
            (textSizeInChars).setCountry(country).create();
  }

  public static BookReadAnalyticsEvent of(Book book) {
    return of(book, false);
  }

  public static BookReadAnalyticsEvent of(Book book, boolean isDownloaded) {
    return new BookReadAnalyticsEvent.Builder()
        .setId(book.getId())
        .setTitle(book.getTitle())
        .setPublisher(book.getPublisher())
        .setCategory(toCategory(book.getCategories()))
        .setCategoryId(toCategoryId(book.getCategories()))
        .setIsDownloaded(isDownloaded)
        .setVersion(book.getVersion()).create();
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

  @BookReadAnalyticsEvent.Variant public int getVariant() {
    return variant;
  }

  public String getVersion() {
    return version;
  }

  @IntDef(value = { ONLINE_VARIANT, OFFLINE_VARIANT }) @interface Variant {

  }

  public int getPagesForResouce() {
    return pagesForResouce;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public CharSequence getText() {
    return text;
  }

  public int getTocSize() {
    return tocSize;
  }

  public int getSpineSize() {
    return spineSize;
  }

  public int getSpinePosition() {
    return spinePosition;
  }

  public int getTextSizeInChars() {
    return textSizeInChars;
  }

  public String getCountry() {
    return country;
  }

  public static class Builder {

    private String id;
    private String title;
    private String category;
    private String categoryId;
    private String brand;
    private int variant;
    private String version;
    private int pagesForResouce;
    private int currentPage;
    private CharSequence text;
    private int tocSize;
    private int spineSize;
    private int spinePosition;
    private int textSizeInChars;
    private String country;

    public Builder setBrand(String brand) {
      this.brand = brand;
      return this;
    }

    public Builder setVariant(int variant) {
      this.variant = variant;
      return this;
    }

    public Builder setPagesForResouce(int pagesForResouce) {
      this.pagesForResouce = pagesForResouce;
      return this;
    }

    public Builder setCurrentPage(int currentPage) {
      this.currentPage = currentPage;
      return this;
    }

    public Builder setText(CharSequence text) {
      this.text = text;
      return this;
    }

    public Builder setTocSize(int tocSize) {
      this.tocSize = tocSize;
      return this;
    }

    public Builder setSpineSize(int spineSize) {
      this.spineSize = spineSize;
      return this;
    }

    public Builder setSpinePosition(int spinePosition) {
      this.spinePosition = spinePosition;
      return this;
    }

    public Builder setTextSizeInChars(int textSizeInChars) {
      this.textSizeInChars = textSizeInChars;
      return this;
    }

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

    public Builder setCountry(String country) {
      this.country = country;
      return this;
    }

    public BookReadAnalyticsEvent create() {
      return new BookReadAnalyticsEvent(id, title, category, categoryId, brand, variant, version, pagesForResouce,
          currentPage, text, tocSize, spineSize, spinePosition, textSizeInChars, country);
    }

  }

}
