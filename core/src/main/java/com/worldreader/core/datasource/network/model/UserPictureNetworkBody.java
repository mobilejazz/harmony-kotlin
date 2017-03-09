package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class UserPictureNetworkBody {

  @SerializedName("picture") private String picture;

  // Gson
  public UserPictureNetworkBody() {
  }

  public UserPictureNetworkBody(final String picture) {
    this.picture = picture;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(final String picture) {
    this.picture = picture;
  }
}
