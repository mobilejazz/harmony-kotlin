package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class UserRegisterBody {

  @SerializedName("userName") private String username;
  @SerializedName("password") private String password;
  @SerializedName("email") private String email;

  public static UserRegisterBody create(String username, String password, String email) {
    return new UserRegisterBody(username, password, email);
  }

  public UserRegisterBody() {
  }

  private UserRegisterBody(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
  }
}
