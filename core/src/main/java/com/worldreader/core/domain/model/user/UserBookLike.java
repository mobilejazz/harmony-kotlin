package com.worldreader.core.domain.model.user;

import com.worldreader.core.datasource.repository.model.RepositoryModel;

import java.util.*;

public class UserBookLike extends RepositoryModel {

  private String userId;
  private String bookId;
  private boolean liked;
  private boolean sync;
  private Date likedAt;

  public UserBookLike(final String userId, final String bookId, final boolean liked, final boolean sync, final Date likedAt) {
    this.userId = userId;
    this.bookId = bookId;
    this.liked = liked;
    this.sync = sync;
    this.likedAt = likedAt;
  }

  public UserBookLike() {
  }

  public String getUserId() {
    return userId;
  }

  public String getBookId() {
    return bookId;
  }

  public boolean isLiked() {
    return liked;
  }

  public boolean isSync() {
    return sync;
  }

  public Date getLikedAt() {
    return likedAt;
  }

  @Override public String getIdentifier() {
    return this.getClass().getSimpleName();
  }

  public static final class Builder {

    private String id;
    private String userId;
    private String bookId;
    private int userLevel;
    private boolean liked;
    private boolean sync;
    private Date likedAt;

    public Builder() {
    }

    public Builder withId(String id) {
      this.id = id;
      return this;
    }

    public Builder withUserId(String userId) {
      this.userId = userId;
      return this;
    }

    public Builder withBookId(String bookId) {
      this.bookId = bookId;
      return this;
    }

    public Builder withUserLevel(int userLevel) {
      this.userLevel = userLevel;
      return this;
    }

    public Builder withLiked(boolean liked) {
      this.liked = liked;
      return this;
    }

    public Builder withSync(boolean sync) {
      this.sync = sync;
      return this;
    }

    public Builder withLikedAt(Date likedAt) {
      this.likedAt = likedAt;
      return this;
    }

    public UserBookLike build() {
      return new UserBookLike(userId, bookId, liked, sync, likedAt);
    }
  }

}
