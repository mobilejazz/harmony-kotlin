package com.worldreader.core.datasource.network.model;

import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

import java.util.*;

public class UserBookNetworkBody {

  @SerializedName("id") private int id;
  @SerializedName("userId") private String userId;
  @SerializedName("bookId") private String bookId;
  @SerializedName("collectionIds") private String collectionIds;
  @SerializedName("favorite") private boolean favorite;
  @SerializedName("bookmark") private String bookmark;
  @SerializedName("finished") private boolean finished;
  @SerializedName("savedOfflineAt") private Date savedOfflineAt;
  @SerializedName("rating") private int rating;
  @SerializedName("liked") private boolean liked;
  @SerializedName("createdAt") private Date createdAt;
  @SerializedName("updatedAt") private Date updatedAt;

  public static UserBookNetworkBody finishBook(@NonNull String bookId) {
    return new Builder().setBookId(bookId).setFinished(true).build();
  }

  public static UserBookNetworkBody unfinishBook(@NonNull String bookId) {
    return new Builder().setBookId(bookId).setFinished(false).build();
  }

  public static UserBookNetworkBody likeBook(@NonNull String bookId) {
    return new UserBookNetworkBody.Builder().setBookId(bookId).setLiked(true).build();
  }

  public static UserBookNetworkBody unlikeBook(@NonNull String bookId) {
    return new UserBookNetworkBody.Builder().setBookId(bookId).setLiked(false).build();
  }

  public static UserBookNetworkBody updateCollectionIds(@NonNull String bookId,
      @NonNull List<String> collectionIds) {
    return new UserBookNetworkBody.Builder().setBookId(bookId)
        .setCollectionIds(mapCollectionIds(collectionIds))
        .build();
  }

  public static String mapCollectionIds(List<String> collectionIds) {
    if (collectionIds.size() == 0) {
      return null;
    } else {
      StringBuilder builder = new StringBuilder();
      Set<String> collectionIdsSet = new HashSet<>(collectionIds);
      List<String> notDuplicatedItemCollection = new ArrayList<>(collectionIdsSet);

      for (int position = 0; position < notDuplicatedItemCollection.size(); position++) {
        builder.append(notDuplicatedItemCollection.get(position));

        if (position < notDuplicatedItemCollection.size() - 1) {
          builder.append(",");
        }
      }
      return builder.toString();
    }
  }

  public UserBookNetworkBody() {
  }

  private UserBookNetworkBody(int id, String userId, String bookId, boolean favorite,
      String bookmark, boolean finished, Date savedOfflineAt, int rating, boolean liked,
      String collectionIds, Date createdAt, Date updatedAt) {
    this.id = id;
    this.userId = userId;
    this.bookId = bookId;
    this.favorite = favorite;
    this.bookmark = bookmark;
    this.finished = finished;
    this.savedOfflineAt = savedOfflineAt;
    this.rating = rating;
    this.liked = liked;
    this.collectionIds = collectionIds;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
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

  public boolean isFavorite() {
    return favorite;
  }

  public String getBookmark() {
    return bookmark;
  }

  public boolean isFinished() {
    return finished;
  }

  public Date getSavedOfflineAt() {
    return savedOfflineAt;
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

  public String getCollectionIds() {
    return collectionIds;
  }

  public static final class Builder {

    private int id;
    private String userId;
    private String bookId;
    private String collectionIds;
    private boolean favorite;
    private String bookmark;
    private boolean finished;
    private Date savedOfflineAt;
    private int rating;
    private boolean liked;
    private Date createdAt;
    private Date updatedAt;

    public Builder() {
    }

    public static Builder anUserBooksBody() {
      return new Builder();
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

    public Builder setFavorite(boolean favorite) {
      this.favorite = favorite;
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

    public Builder setSavedOfflineAt(Date savedOfflineAt) {
      this.savedOfflineAt = savedOfflineAt;
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

    public Builder setCollectionIds(String collectionIds) {
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

    public UserBookNetworkBody build() {
      return new UserBookNetworkBody(id, userId, bookId, favorite, bookmark, finished,
          savedOfflineAt, rating, liked, collectionIds, createdAt, updatedAt);
    }
  }
}
