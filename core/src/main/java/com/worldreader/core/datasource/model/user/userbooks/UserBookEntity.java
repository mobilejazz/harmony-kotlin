package com.worldreader.core.datasource.model.user.userbooks;

import com.worldreader.core.common.annotation.Immutable;
import com.worldreader.core.datasource.repository.model.RepositoryModel;

import java.util.*;

@Immutable public class UserBookEntity extends RepositoryModel {

  private int id;
  private String userId;
  private String bookId;
  private boolean inMyBooks;
  private String bookmark;
  private List<String> collectionIds;
  private boolean finished;
  private Date saveOfflineAt;
  private int rating;
  private boolean liked;
  private boolean isSynchronized;
  private Date createdAt;
  private Date updatedAt;

  private UserBookEntity(int id, String userId, String bookId, boolean inMyBooks, String bookmark,
      boolean finished, Date saveOfflineAt, int rating, boolean liked, final boolean isSynchronized,
      Date createdAt, List<String> collectionIds, Date updatedAt) {
    this.id = id;
    this.userId = userId;
    this.bookId = bookId;
    this.inMyBooks = inMyBooks;
    this.bookmark = bookmark;
    this.finished = finished;
    this.saveOfflineAt = saveOfflineAt;
    this.rating = rating;
    this.liked = liked;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.collectionIds = collectionIds;
    this.isSynchronized = isSynchronized;
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

  public List<String> getCollectionIds() {
    if (collectionIds == null) {
      return new ArrayList<>();
    }

    return collectionIds;
  }

  public boolean isSynchronized() {
    return isSynchronized;
  }

  public void setUpdatedAt(final Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override public String getIdentifier() {
    return bookId;
  }

  public static UserBookEntity copy(UserBookEntity toCopy, boolean isLikedBook) {
    return new Builder().setId(toCopy.getId())
        .setUserId(toCopy.getUserId())
        .setBookId(toCopy.getBookId())
        .setInMyBooks(toCopy.isInMyBooks())
        .setCollectionIds(toCopy.getCollectionIds())
        .setBookmark(toCopy.getBookmark())
        .setFinished(toCopy.isFinished())
        .setSaveOfflineAt(toCopy.getSaveOfflineAt() == null ? null : toCopy.getSaveOfflineAt())
        .setRating(toCopy.getRating())
        .setLiked(isLikedBook)
        .setCreatedAt(toCopy.getCreatedAt() == null ? null : toCopy.getCreatedAt())
        .setUpdatedAt(toCopy.getUpdatedAt() == null ? null : toCopy.getUpdatedAt())
        .setSynchronized(toCopy.isSynchronized())
        .build();
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

    public Builder() {
    }

    public Builder(UserBookEntity userBookEntity) {
      this.id = userBookEntity.getId();
      this.userId = userBookEntity.getUserId();
      this.bookId = userBookEntity.getBookId();
      this.collectionIds = userBookEntity.getCollectionIds();
      this.inMyBooks = userBookEntity.isInMyBooks();
      this.bookmark = userBookEntity.getBookmark();
      this.finished = userBookEntity.isFinished();
      this.saveOfflineAt = userBookEntity.getSaveOfflineAt();
      this.rating = userBookEntity.getRating();
      this.liked = userBookEntity.isLiked();
      this.createdAt = userBookEntity.getCreatedAt();
      this.updatedAt = userBookEntity.getUpdatedAt();
      this.isSynchronized = userBookEntity.isSynchronized();
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

    public Builder setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder setUpdatedAt(Date updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public Builder setCollectionIds(List<String> collectionIds) {
      this.collectionIds = collectionIds;
      return this;
    }

    public Builder setSynchronized(boolean isSynchronized) {
      this.isSynchronized = isSynchronized;
      return this;
    }

    public UserBookEntity build() {
      return new UserBookEntity(id, userId, bookId, inMyBooks, bookmark, finished, saveOfflineAt,
          rating, liked, isSynchronized, createdAt, collectionIds, updatedAt);
    }
  }

}
