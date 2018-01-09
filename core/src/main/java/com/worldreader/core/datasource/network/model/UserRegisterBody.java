package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class UserRegisterBody extends BaseUserRegisterBody {

  @SerializedName("userName") private String username;
  @SerializedName("password") private String password;
  @SerializedName("email") private String email;
  @SerializedName("readToKidsId") private String activatorCode;
  @SerializedName("gender") private int gender;
  @SerializedName("age") private int age;

  public static UserRegisterBody create(String username, String password, String email, String referrerDeviceId, String referrerUserId) {
    return new UserRegisterBody(username, password, email, referrerDeviceId, referrerUserId);
  }

  public static UserRegisterBody create(final String username, final String password,
      final String email, final String activatorCode, final int gender, final int age, String referrerDeviceId, String referrerUserId) {
    return new UserRegisterBody(username, password, email, activatorCode, gender, age, referrerDeviceId, referrerUserId);
  }

  private UserRegisterBody(String username, String password, String email, String referrerDeviceId, String referrerUserId) {
    super(referrerDeviceId, referrerUserId);

    this.username = username;
    this.password = password;
    this.email = email;
  }

  private UserRegisterBody(final String username, final String password, final String email,
      final String activatorCode, final int gender, final int age, String referrerDeviceId, String referrerUserId) {
    super(referrerDeviceId, referrerUserId);

    this.username = username;
    this.password = password;
    this.email = email;
    this.activatorCode = activatorCode;
    this.gender = gender;
    this.age = age;
  }
}
