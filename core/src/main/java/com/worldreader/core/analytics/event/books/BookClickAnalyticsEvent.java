package com.worldreader.core.analytics.event.books;

import com.worldreader.core.analytics.event.AnalyticsEvent;
import com.worldreader.core.application.helper.analytics.ProductList;

public class BookClickAnalyticsEvent implements AnalyticsEvent {

  private final String id;
  private final String name;
  private final String category;
  private final String publisher;
  private final ProductList productList;

  private BookClickAnalyticsEvent(String id, String name, String category, String publisher, ProductList productList) {
    this.id = id;
    this.name = name;
    this.category = category;
    this.publisher = publisher;
    this.productList = productList;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getCategory() {
    return category;
  }

  public String getPublisher() {
    return publisher;
  }

  public ProductList getProductList() {
    return productList;
  }

  public static class Builder {

    private String id;
    private String name;
    private String category;
    private String publisher;
    private ProductList productList;

    public Builder setId(String id) {
      this.id = id;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setCategory(String category) {
      this.category = category;
      return this;
    }

    public Builder setPublisher(String publisher) {
      this.publisher = publisher;
      return this;
    }

    public Builder setProductList(ProductList productList) {
      this.productList = productList;
      return this;
    }

    public BookClickAnalyticsEvent create() {
      return new BookClickAnalyticsEvent(id, name, category, publisher, productList);
    }
  }

}
