package com.worldreader.core.domain.model;

import java.util.*;

public class BookDownloaded {

  private String bookId;
  private String version;
  private Date timestamp;

  private BookDownloaded(String bookId, String version, Date timestamp) {
    this.bookId = bookId;
    this.version = version;
    this.timestamp = timestamp;
  }

  public String getBookId() {
    return bookId;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public String getVersion() {
    return version;
  }

  /**
   * In old days we where not saving the version number
   * @return
   */
  public boolean hasVersion() {
    return version != null && !version.isEmpty();
  }

  public static BookDownloaded create(String bookId, String version, Date timestamp) {
    return new BookDownloaded(bookId, version, timestamp);
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
