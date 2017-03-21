package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class UserRegisterBody {

  @SerializedName("userName") private String username;
  @SerializedName("password") private String password;
  @SerializedName("email") private String email;
  @SerializedName("readToKidsId") private String activatorCode;
  @SerializedName("gender") private int gender;
  @SerializedName("age") private int age;

  public static UserRegisterBody create(String username, String password, String email) {
    return new UserRegisterBody(username, password, email);
  }

  public static UserRegisterBody create(final String username, final String password,
      final String email, final String activatorCode, final int gender, final int age) {
    return new UserRegisterBody(username, password, email, activatorCode, gender, age);
  }

  public UserRegisterBody() {
  }

  private UserRegisterBody(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
  }

  private UserRegisterBody(final String username, final String password, final String email,
      final String activatorCode, final int gender, final int age) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.activatorCode = activatorCode;
    this.gender = gender;
    this.age = age;
  }
}
