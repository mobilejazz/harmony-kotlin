package com.worldreader.core.datasource.storage.model;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import static com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBooksTable.*;

@StorIOSQLiteType(table = TABLE) public class UserBookDb {

  @StorIOSQLiteColumn(name = COLUMN_ID) public int id;
  @StorIOSQLiteColumn(name = COLUMN_USER_ID, key = true) public String userId;
  @StorIOSQLiteColumn(name = COLUMN_BOOK_ID, key = true) public String bookId;
  @StorIOSQLiteColumn(name = COLUMN_FAVORITE) public Boolean favorite;
  @StorIOSQLiteColumn(name = COLUMN_BOOKMARK) public String bookmark;
  @StorIOSQLiteColumn(name = COLUMN_COLLECTION_IDS) public String collectionIds;
  @StorIOSQLiteColumn(name = COLUMN_FINISHED) public Boolean finished;
  @StorIOSQLiteColumn(name = COLUMN_SAVED_OFFLINE_AT) public String saveOfflineAt;
  @StorIOSQLiteColumn(name = COLUMN_RATING) public Integer rating;
  @StorIOSQLiteColumn(name = COLUMN_LIKED) public Boolean liked;
  @StorIOSQLiteColumn(name = COLUMN_SYNC) public Boolean syncronized;
  @StorIOSQLiteColumn(name = COLUMN_CREATED_AT) public String createdAt;
  @StorIOSQLiteColumn(name = COLUMN_UPDATED_AT) public String updatedAt;

  public UserBookDb(final int id, final String userId, final String bookId, final boolean favorite,
      final String bookmark, final boolean finished, final String saveOfflineAt, final int rating,
      final boolean liked, final String createdAt, final String updatedAt) {
    this.id = id;
    this.userId = userId;
    this.bookId = bookId;
    this.favorite = favorite;
    this.bookmark = bookmark;
    this.finished = finished;
    this.saveOfflineAt = saveOfflineAt;
    this.rating = rating;
    this.liked = liked;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UserBookDb() {
  }

  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
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

  public boolean isFavorite() {
    return favorite;
  }

  public void setFavorite(final boolean favorite) {
    this.favorite = favorite;
  }

  public String getBookmark() {
    return bookmark;
  }

  public void setBookmark(final String bookmark) {
    this.bookmark = bookmark;
  }

  public boolean isFinished() {
    return finished;
  }

  public void setFinished(final boolean finished) {
    this.finished = finished;
  }

  public String getSaveOfflineAt() {
    return saveOfflineAt;
  }

  public void setSaveOfflineAt(final String saveOfflineAt) {
    this.saveOfflineAt = saveOfflineAt;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(final int rating) {
    this.rating = rating;
  }

  public boolean isLiked() {
    return liked;
  }

  public void setLiked(final boolean liked) {
    this.liked = liked;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(final String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(final String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getCollectionIds() {
    return collectionIds;
  }

  public void setCollectionIds(final String collectionIds) {
    this.collectionIds = collectionIds;
  }

  public Boolean getSyncronized() {
    return syncronized;
  }

  public void setSyncronized(final Boolean syncronized) {
    this.syncronized = syncronized;
  }

  public static final class Builder {

    public int id;
    public String userId;
    public String bookId;
    public boolean favorite;
    public String bookmark;
    public String collectionIds;
    public boolean finished;
    public String saveOfflineAt;
    public int rating;
    public boolean liked;
    public boolean isSynchronized;
    public String createdAt;
    public String updatedAt;

    public Builder() {
    }

    public Builder withId(Integer id) {
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

    public Builder withFavorite(Boolean favorite) {
      this.favorite = favorite;
      return this;
    }

    public Builder withBookmark(String bookmark) {
      this.bookmark = bookmark;
      return this;
    }

    public Builder withFinished(Boolean finished) {
      this.finished = finished;
      return this;
    }

    public Builder withSaveOfflineAt(String saveOfflineAt) {
      this.saveOfflineAt = saveOfflineAt;
      return this;
    }

    public Builder withRating(Integer rating) {
      this.rating = rating;
      return this;
    }

    public Builder withLiked(Boolean liked) {
      this.liked = liked;
      return this;
    }

    public Builder withCreatedAt(String createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder withUpdatedAt(String updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public Builder withSynchronized(Boolean isSynchronized) {
      this.isSynchronized = isSynchronized;
      return this;
    }

    public Builder withCollectionIds(String collectionIds) {
      this.collectionIds = collectionIds;
      return this;
    }

    public UserBookDb build() {
      UserBookDb userBookDb = new UserBookDb();
      userBookDb.setId(id);
      userBookDb.setUserId(userId);
      userBookDb.setBookId(bookId);
      userBookDb.setFavorite(favorite);
      userBookDb.setBookmark(bookmark);
      userBookDb.setFinished(finished);
      userBookDb.setSaveOfflineAt(saveOfflineAt);
      userBookDb.setRating(rating);
      userBookDb.setCollectionIds(collectionIds);
      userBookDb.setLiked(liked);
      userBookDb.setSyncronized(isSynchronized);
      userBookDb.setCreatedAt(createdAt);
      userBookDb.setUpdatedAt(updatedAt);
      return userBookDb;
    }
  }
}
