package com.worldreader.core.analytics.event.books;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class BookFinishedAnalyticsEvent implements AnalyticsEvent {

  private final String id;
  private final String title;
  private final String publisher;
  private final String publisherId;
  private final String authorName;
  private final String authorId;


  public BookFinishedAnalyticsEvent(String id, String title, String publisher, String publisherId, String authorName, String authorId) {
    this.id = id;
    this.title = title;
    this.publisher = publisher;
    this.publisherId = publisherId;

    this.authorName = authorName;
    this.authorId = authorId;
  }

  private BookFinishedAnalyticsEvent(Builder builder) {
    id = builder.id;
    title = builder.title;
    publisher = builder.publisher;
    publisherId = builder.publisherId;
    authorName = builder.authorName;
    authorId = builder.authorId;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getPublisher() {
    return publisher;
  }

  public String getPublisherId() {
    return publisherId;
  }

  public String getAuthorName() {
    return authorName;
  }

  public String getAuthorId() {
    return authorId;
  }

  public static final class Builder {

    private String id;
    private String title;
    private String publisher;
    private String publisherId;
    private String authorName;
    private String authorId;

    public Builder(String id, String title) {
      this.id = id;
      this.title = title;
    }

    public Builder setId(String id){
      this.id = id;
      return this;
    }

    public Builder setTitle(String title){
      this.title = title;
      return this;
    }

    public Builder setPublisherName(String publisherName){
      this.publisher = publisherName;
      return this;
    }

    public Builder setPublisherId(String id){
      this.publisherId = id;
      return this;
    }

    public Builder setAuthorName(String authorName){
      this.authorName = authorName;
      return this;
    }

    public Builder setAuthorId(String authorId){
      this.authorId = authorId;
      return this;
    }

    public BookFinishedAnalyticsEvent build() {
      return new BookFinishedAnalyticsEvent(this);
    }
  }
}
