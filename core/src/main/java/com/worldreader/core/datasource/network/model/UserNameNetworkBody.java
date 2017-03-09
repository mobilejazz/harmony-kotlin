package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class UserNameNetworkBody {

  @SerializedName("name") private String name;

  // Gson
  public UserNameNetworkBody() {
  }

  public UserNameNetworkBody(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }
}
