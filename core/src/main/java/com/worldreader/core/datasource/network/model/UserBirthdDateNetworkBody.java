package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.*;

public class UserBirthdDateNetworkBody {

  @SerializedName("birthDate") private Date birthDate;

  // Gson
  public UserBirthdDateNetworkBody() {
  }

  public UserBirthdDateNetworkBody(final Date birthDate) {
    this.birthDate = birthDate;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setPicture(final Date birthDate) {
    this.birthDate = birthDate;
  }

}
