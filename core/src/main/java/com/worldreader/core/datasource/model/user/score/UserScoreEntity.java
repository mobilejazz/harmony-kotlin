package com.worldreader.core.datasource.model.user.score;

import com.worldreader.core.datasource.repository.model.RepositoryModel;

import java.util.*;

public class UserScoreEntity extends RepositoryModel {

  private final String userId;
  private final String bookId;
  private final int scoreId;
  private final int score;
  private final int pages;
  private final boolean sync;
  private final Date createdAt;
  private final Date updatedAt;

  public UserScoreEntity(final String userId, final String bookId, final int scoreId, final int score, final int pages, final boolean sync,
      final Date createdAt, final Date updatedAt) {
    this.userId = userId;
    this.bookId = bookId;
    this.scoreId = scoreId;
    this.score = score;
    this.pages = pages;
    this.sync = sync;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getUserId() {
    return userId;
  }

  public String getBookId() {
    return bookId;
  }

  public int getScoreId() {
    return scoreId;
  }

  public int getScore() {
    return score;
  }

  public int getPages() {
    return pages;
  }

  public boolean isSync() {
    return sync;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  @Override public String getIdentifier() {
    return userId;
  }

  public static class Builder {

    private String userId;
    private String bookId;
    private int scoreId;
    private int score;
    private int pages;
    private boolean sync;
    private Date createdAt;
    private Date updatedAt;

    public Builder setUserId(final String userId) {
      this.userId = userId;
      return this;
    }

    public Builder setBookId(final String bookId) {
      this.bookId = bookId;
      return this;
    }

    public Builder setScoreId(final int scoreId) {
      this.scoreId = scoreId;
      return this;
    }

    public Builder setScore(final int score) {
      this.score = score;
      return this;
    }

    public Builder setPages(final int pages) {
      this.pages = pages;
      return this;
    }

    public Builder setSync(final boolean sync) {
      this.sync = sync;
      return this;
    }

    public Builder setCreatedAt(final Date createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder setUpdatedAt(final Date updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public UserScoreEntity build() {
      return new UserScoreEntity(userId, bookId, scoreId, score, pages, sync, createdAt, updatedAt);
    }
  }

}
