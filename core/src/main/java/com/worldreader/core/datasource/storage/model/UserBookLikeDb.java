package com.worldreader.core.datasource.storage.model;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBookLikesTable;

import java.util.Date;

@StorIOSQLiteType(table = UserBookLikesTable.TABLE) public class UserBookLikeDb {

  @StorIOSQLiteColumn(name = UserBookLikesTable.COLUMN_USER_ID, key = true) public String userId;
  @StorIOSQLiteColumn(name = UserBookLikesTable.COLUMN_BOOK_ID, key = true) public String bookId;
  @StorIOSQLiteColumn(name = UserBookLikesTable.COLUMN_LIKED) public boolean liked;
  @StorIOSQLiteColumn(name = UserBookLikesTable.COLUMN_SYNCHRONIZED) public boolean sync;
  @StorIOSQLiteColumn(name = UserBookLikesTable.COLUMN_LIKED_AT) public long likedAt;

  public UserBookLikeDb(final String userId, final String bookId, final boolean liked, final boolean sync, final long likedAt) {
    this.userId = userId;
    this.bookId = bookId;
    this.liked = liked;
    this.sync = sync;
    this.likedAt = likedAt;
  }

  public UserBookLikeDb() {
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(final String userId) {
    this.userId = userId;
  }

  public String getBookId() {
    return bookId;
  }

  public void setBookId(final String bookId) {
    this.bookId = bookId;
  }

  public boolean isLiked() {
    return liked;
  }

  public void setLiked(final boolean liked) {
    this.liked = liked;
  }

  public boolean isSync() {
    return sync;
  }

  public void setSync(final boolean sync) {
    this.sync = sync;
  }

  public Date getLikedAt() {
    return new Date(likedAt);
  }

  public void setLikedAt(final Date likedAt) {
    this.likedAt = likedAt.getTime();
  }

  public static final class Builder {

    public String userId;
    public String bookId;
    public boolean liked;
    public boolean sync;
    public Date likedAt;

    public Builder() {
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

    public UserBookLikeDb build() {
      UserBookLikeDb userBookLikeDb = new UserBookLikeDb();
      userBookLikeDb.setUserId(userId);
      userBookLikeDb.setBookId(bookId);
      userBookLikeDb.setLiked(liked);
      userBookLikeDb.setSync(sync);
      userBookLikeDb.setLikedAt(likedAt);
      return userBookLikeDb;
    }
  }
}
