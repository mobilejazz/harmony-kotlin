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
  private final String collectionName;
  private final String collectionId;
  private final String shelveTitle;
  private final String shelveId;
  private final String referringScreen;
  private final String referringMeta;

  private BookDetailAnalyticsEvent(String id, String title, String version, String category, String categoryId, String publisher, String author,
      String collectionName, String collectionId, String shelveTitle, String shelveId, String referringScreen, String referringMeta) {
    this.id = id;
    this.title = title;
    this.version = version;
    this.category = category;
    this.categoryId = categoryId;
    this.publisher = publisher;
    this.author = author;
    this.collectionName = collectionName;
    this.collectionId = collectionId;
    this.shelveTitle = shelveTitle;
    this.shelveId = shelveId;
    this.referringScreen = referringScreen;
    this.referringMeta = referringMeta;
  }

  /**
   *     if (collection != null) {
   attrs.put(PinpointMobileAnalyticsConstants.COLLECTION_ID_ATTRIBUTE, String.valueOf(collection.getId()));
   attrs.put(PinpointMobileAnalyticsConstants.COLLECTION_TITLE_ATTRIBUTE, collection.getName());
   }

   if (shelve != null) {
   attrs.put(PinpointMobileAnalyticsConstants.SHELVE_ATTRIBUTE, String.valueOf(shelve.getType()));
   attrs.put(PinpointMobileAnalyticsConstants.SHELVE_TITLE_ATTRIBUTE, shelve.getTitle());
   }

   attrs.put(PinpointMobileAnalyticsConstants.BOOK_TITLE_ATTRIBUTE, book.getTitle());
   attrs.put(PinpointMobileAnalyticsConstants.BOOK_ID_ATTRIBUTE, book.getId());
   attrs.put(PinpointMobileAnalyticsConstants.BOOK_VERSION_ATTRIBUTE, PinpointMobileAnalyticsConstants.getBookVersionIntValue(book.getVersion()));
   attrs.put(PinpointMobileAnalyticsConstants.REFERRING_SCREEN, referringScreen);

   if (meta != null) {
   attrs.put(PinpointMobileAnalyticsConstants.REFERRING_META, meta.toString());
   }

   analytics.sendEvent(new GenericAnalyticsEvent(PinpointMobileAnalyticsConstants.BOOK_DETAILS_EVENT, attrs));

   *
   */


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

  public static String toCategory(List<Category> categories) {
    if (categories == null || categories.isEmpty()) {
      return "";
    }

    return categories.get(0).getTitle();
  }

  public static String toCategoryId(List<Category> categories) {
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

  public String getCollectionName() {
    return collectionName;
  }

  public String getCollectionId() {
    return collectionId;
  }

  public String getShelveTitle() {
    return shelveTitle;
  }

  public String getShelveId() {
    return shelveId;
  }

  public String getReferringScreen() {
    return referringScreen;
  }

  public String getReferringMeta() {
    return referringMeta;
  }

  public static class Builder {

    private String id;
    private String title;
    private String version;
    private String category;
    private String categoryId;
    private String publisher;
    private String author;
    private String collectionName;
    private String collectionId;
    private String shelveTitle;
    private String shelveId;
    private String referringScreen;
    private String referringMeta;

    public Builder() {
    }

    public Builder setCollectionName(String collectionName) {
      this.collectionName = collectionName;
      return this;
    }

    public Builder setCollectionId(String collectionId) {
      this.collectionId = collectionId;
      return this;
    }

    public Builder setShelveTitle(String shelveTitle) {
      this.shelveTitle = shelveTitle;
      return this;
    }

    public Builder setShelveId(String shelveId) {
      this.shelveId = shelveId;
      return this;
    }

    public Builder setReferringScreen(String referringScreen) {
      this.referringScreen = referringScreen;
      return this;
    }

    public Builder setReferringMeta(String referringMeta) {
      this.referringMeta = referringMeta;
      return this;
    }

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

    public BookDetailAnalyticsEvent create(){
      return new BookDetailAnalyticsEvent(id, title, version, category, categoryId, publisher, author, collectionName, collectionId,
      shelveTitle,
      shelveId,
      referringScreen,
      referringMeta);
    }
  }
}
