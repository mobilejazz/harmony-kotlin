package com.worldreader.core.application.helper;

/**
 * A pagination object to be used to describe pagination (offset, limit) which can be used in
 * queries.
 */
public class Pagination {

  private int offset;
  private final int limit;
  private final String key;
  private boolean isFinished;

  private Pagination(Builder builder) {
    this.offset = builder.offset;
    this.limit = builder.limit;
    this.key = builder.key;
  }

  /**
   * The current page number this pagination object represents
   *
   * @return the page number
   */
  public int getPageNumber() {
    if (offset < limit || limit == 0) return 1;

    return (offset / limit) + 1;
  }

  /**
   * The offset for this pagination object. The offset determines what index (0 index) to start
   * retrieving results from.
   *
   * @return the offset
   */
  public int getOffset() {
    return offset;
  }

  /**
   * The limit for this pagination object. The limit determines the maximum amount of results to
   * return.
   *
   * @return the limit
   */
  public int getLimit() {
    return limit;
  }

  /**
   * Creates a new pagination object representing the next page
   *
   * @return new pagination object with offset shifted by offset+limit
   */
  public int getNext() {
    return offset += limit;
  }

  /**
   * Creates a new pagination object representing the previous page
   *
   * @return new pagination object with offset shifted by offset-limit
   */
  public int getPrevious() {
    if (limit >= offset) {
      return 0;
    } else {
      return offset -= limit;
    }
  }

  public boolean isFinished() {
    return isFinished;
  }

  public void setIsFinished(boolean isFinished) {
    this.isFinished = isFinished;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof Pagination)) {
      return false;
    }

    Pagination pagination = (Pagination) o;

    return (limit == pagination.limit) && (offset == pagination.offset);
  }

  public static class Builder {

    private int offset;
    private int limit;
    private String key;

    public Builder addOffset(int offset) {
      this.offset = offset;
      return this;
    }

    public Builder addLimit(int limit) {
      this.limit = limit;
      return this;
    }

    public Builder addKey(String key) {
      this.key = key;
      return this;
    }

    public Pagination build() {
      return new Pagination(this);
    }
  }
}
