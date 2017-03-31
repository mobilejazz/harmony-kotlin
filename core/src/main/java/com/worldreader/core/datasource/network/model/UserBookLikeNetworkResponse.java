package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class UserBookLikeNetworkResponse {

  @SerializedName("id") private String id;
  @SerializedName("bookId") private String bookId;
  @SerializedName("userLevel") private int userLevel;
  @SerializedName("likedAt") private Date likedAt;

  // Gson
  public UserBookLikeNetworkResponse() {
  }

  public UserBookLikeNetworkResponse(final String id, final String bookId, final int userLevel, final Date likedAt) {
    this.id = id;
    this.bookId = bookId;
    this.userLevel = userLevel;
    this.likedAt = likedAt;
  }

  public String getId() {
    return id;
  }

  public String getBookId() {
    return bookId;
  }

  public int getUserLevel() {
    return userLevel;
  }

  public Date getLikedAt() {
    return likedAt;
  }
}
