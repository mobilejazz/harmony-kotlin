package com.worldreader.core.domain.model;

import java.util.*;

public class BookDownloaded {

  private String bookId;
  private Date timestamp;

  private BookDownloaded(String bookId, Date timestamp) {
    this.bookId = bookId;
    this.timestamp = timestamp;
  }

  public String getBookId() {
    return bookId;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public static BookDownloaded create(String bookId, Date timestamp) {
    return new BookDownloaded(bookId, timestamp);
  }

  @Override public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final BookDownloaded that = (BookDownloaded) o;

    return bookId != null ? bookId.equals(that.bookId) : that.bookId == null;

  }

  @Override public int hashCode() {
    return bookId != null ? bookId.hashCode() : 0;
  }
}
