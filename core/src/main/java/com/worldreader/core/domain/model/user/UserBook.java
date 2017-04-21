package com.worldreader.core.domain.model.user;

import com.worldreader.core.common.annotation.Immutable;
import com.worldreader.core.datasource.repository.model.RepositoryModel;

import java.util.*;

@Immutable public class UserBook extends RepositoryModel {

  private int id;
  private String userId;
  private String bookId;
  private List<String> collectionIds;
  private boolean inMyBooks;
  private String bookmark;
  private boolean finished;
  private Date saveOfflineAt;
  private int rating;
  private boolean liked;
  private boolean isSynchronized;
  private Date createdAt;
  private Date updatedAt;
  private Date openedAt;

  private UserBook(int id, String userId, String bookId, boolean inMyBooks, String bookmark,
      boolean finished, Date saveOfflineAt, int rating, boolean liked, final boolean isSynchronized,
      List<String> collectionIds, Date createdAt, Date updatedAt, Date openedAt) {
    this.id = id;
    this.userId = userId;
    this.bookId = bookId;
    this.inMyBooks = inMyBooks;
    this.bookmark = bookmark;
    this.finished = finished;
    this.saveOfflineAt = saveOfflineAt;
    this.rating = rating;
    this.liked = liked;
    this.isSynchronized = isSynchronized;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.openedAt = openedAt;
    this.collectionIds = collectionIds;
  }

  public int getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public String getBookId() {
    return bookId;
  }

  public boolean isInMyBooks() {
    return inMyBooks;
  }

  public String getBookmark() {
    return bookmark;
  }

  public boolean isFinished() {
    return finished;
  }

  public Date getSaveOfflineAt() {
    return saveOfflineAt;
  }

  public int getRating() {
    return rating;
  }

  public boolean isLiked() {
    return liked;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public Date getOpenedAt() {
    return openedAt;
  }

  public List<String> getCollectionIds() {
    return collectionIds;
  }

  public boolean isSynchronized() {
    return isSynchronized;
  }

  @Override public String getIdentifier() {
    return bookId;
  }

  public static final class Builder {

    private int id;
    private String userId;
    private String bookId;
    private List<String> collectionIds;
    private boolean inMyBooks;
    private String bookmark;
    private boolean finished;
    private Date saveOfflineAt;
    private int rating;
    private boolean liked;
    private boolean isSynchronized;
    private Date createdAt;
    private Date updatedAt;
    private Date openedAt;

    public Builder() {
    }

    public Builder(UserBook userBook) {
      this.id = userBook.getId();
      this.userId = userBook.getUserId();
      this.bookId = userBook.getBookId();
      this.collectionIds = userBook.getCollectionIds();
      this.inMyBooks = userBook.isInMyBooks();
      this.bookmark = userBook.getBookmark();
      this.finished = userBook.isFinished();
      this.saveOfflineAt = userBook.getSaveOfflineAt();
      this.rating = userBook.getRating();
      this.liked = userBook.isLiked();
      this.isSynchronized = userBook.isSynchronized();
      this.createdAt = userBook.getCreatedAt();
      this.updatedAt = userBook.getUpdatedAt();
      this.openedAt = userBook.getOpenedAt();
    }

    public Builder setId(int id) {
      this.id = id;
      return this;
    }

    public Builder setUserId(String userId) {
      this.userId = userId;
      return this;
    }

    public Builder setBookId(String bookId) {
      this.bookId = bookId;
      return this;
    }

    public Builder setInMyBooks(boolean inMyBooks) {
      this.inMyBooks = inMyBooks;
      return this;
    }

    public Builder setBookmark(String bookmark) {
      this.bookmark = bookmark;
      return this;
    }

    public Builder setFinished(boolean finished) {
      this.finished = finished;
      return this;
    }

    public Builder setSaveOfflineAt(Date saveOfflineAt) {
      this.saveOfflineAt = saveOfflineAt;
      return this;
    }

    public Builder setRating(int rating) {
      this.rating = rating;
      return this;
    }

    public Builder setLiked(boolean liked) {
      this.liked = liked;
      return this;
    }

    public Builder setCollectionIds(List<String> collectionIds) {
      this.collectionIds = collectionIds;
      return this;
    }

    public Builder setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder setUpdatedAt(Date updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public Builder setOpenedAt(Date openedAt) {
      this.openedAt = openedAt;
      return this;
    }

    public Builder setSynchronized(boolean isSynchronized) {
      this.isSynchronized = isSynchronized;
      return this;
    }

    public UserBook build() {
      return new UserBook(id, userId, bookId, inMyBooks, bookmark, finished, saveOfflineAt, rating,
          liked, isSynchronized, collectionIds, createdAt, updatedAt, openedAt);
    }
  }

}
