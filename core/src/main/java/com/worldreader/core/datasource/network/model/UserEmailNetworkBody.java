package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class UserEmailNetworkBody {

  @SerializedName("email") private String email;

  // Gson
  public UserEmailNetworkBody() {
  }

  public UserEmailNetworkBody(final String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

}
