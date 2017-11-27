package com.worldreader.core.datasource.model.user.userbooklikes;

import com.worldreader.core.datasource.repository.model.RepositoryModel;

import java.util.*;

public class UserBookLikeEntity extends RepositoryModel {

  private String userId;
  private String bookId;
  private boolean liked;
  private boolean sync;
  private Date likedAt;

  public UserBookLikeEntity(final String userId, final String bookId, final boolean liked, final boolean sync, final Date likedAt) {
    this.userId = userId;
    this.bookId = bookId;
    this.liked = liked;
    this.sync = sync;
    this.likedAt = likedAt;
  }

  public UserBookLikeEntity() {
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

    private String userId;
    private String bookId;
    private boolean liked;
    private boolean sync;
    private Date likedAt;

    public Builder() {
    }

    public Builder(UserBookLikeEntity entity) {
      this.userId = entity.getUserId();
      this.bookId = entity.getBookId();
      this.liked = entity.isLiked();
      this.sync = entity.isSync();
      this.likedAt = entity.getLikedAt();
    }

    public Builder withUserId(String userId) {
      this.userId = userId;
      return this;
    }

    public Builder withBookId(String bookId) {
      this.bookId = bookId;
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

    public UserBookLikeEntity build() {
      return new UserBookLikeEntity(userId, bookId, liked, sync, likedAt);
    }
  }

}
