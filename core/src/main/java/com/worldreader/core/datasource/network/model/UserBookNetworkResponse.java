package com.worldreader.core.datasource.network.model;

import android.text.TextUtils;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

import java.util.*;

public class UserBookNetworkResponse {

  @SerializedName("id") private int id;
  @SerializedName("userId") private String userId;
  @SerializedName("bookId") private String bookId;
  @SerializedName("favorite") private boolean favorite;
  @SerializedName("bookmark") private String bookmark;
  @SerializedName("finished") private boolean finished;
  @SerializedName("saveOfflineAt") private Date saveOfflineAt;
  @SerializedName("rating") private int rating;
  @SerializedName("liked") private boolean liked;
  @SerializedName("collectionIds") private String collectionIds;
  @SerializedName("createdAt") private Date createdAt;
  @SerializedName("updatedAt") private Date updatedAt;

  public UserBookNetworkResponse() {
  }

  private UserBookNetworkResponse(int id, String userId, String bookId, boolean favorite,
      String bookmark, boolean finished, Date saveOfflineAt, int rating, boolean liked,
      String collectionIds, Date createdAt, Date updatedAt) {
    this.id = id;
    this.userId = userId;
    this.bookId = bookId;
    this.favorite = favorite;
    this.bookmark = bookmark;
    this.finished = finished;
    this.saveOfflineAt = saveOfflineAt;
    this.rating = rating;
    this.liked = liked;
    this.collectionIds = collectionIds;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getBookId() {
    return bookId;
  }

  public void setBookId(String bookId) {
    this.bookId = bookId;
  }

  public boolean isFavorite() {
    return favorite;
  }

  public void setFavorite(boolean favorite) {
    this.favorite = favorite;
  }

  public String getBookmark() {
    return bookmark;
  }

  public void setBookmark(String bookmark) {
    this.bookmark = bookmark;
  }

  public boolean isFinished() {
    return finished;
  }

  public void setFinished(boolean finished) {
    this.finished = finished;
  }

  public Date getSaveOfflineAt() {
    return saveOfflineAt;
  }

  public void setSaveOfflineAt(Date saveOfflineAt) {
    this.saveOfflineAt = saveOfflineAt;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public boolean isLiked() {
    return liked;
  }

  public void setLiked(boolean liked) {
    this.liked = liked;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getCollectionIds() {
    return collectionIds;
  }

  public void setCollectionIds(final String collectionIds) {
    this.collectionIds = collectionIds;
  }

  public static List<String> mapCollectionIds(String collectionIds) {
    if (!TextUtils.isEmpty(collectionIds)) {
      final String withOutSpaces = collectionIds.replaceAll("\\s+", "");
      final String[] split = withOutSpaces.split(",");
      return Lists.newArrayList(split);
    } else {
      return Collections.emptyList();
    }
  }
}
