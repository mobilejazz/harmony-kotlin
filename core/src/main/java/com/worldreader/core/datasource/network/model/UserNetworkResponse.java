package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;
import com.worldreader.core.datasource.repository.model.RepositoryModel;

import java.util.*;

public class UserNetworkResponse extends RepositoryModel {

  @SerializedName("id") private String id;
  @SerializedName("profileId") private int profileId;
  @SerializedName("readToKidsId") private String readToKidsId;
  @SerializedName("userName") private String userName;
  @SerializedName("name") private String name;
  @SerializedName("email") private String email;
  @SerializedName("emailConfirmed") private boolean emailConfirmed;
  @SerializedName("score") private int score;
  @SerializedName("pagesPerDay") private int pagesPerDay;
  @SerializedName("locale") private String locale;
  @SerializedName("fontSize") private int fontSize;
  @SerializedName("gender") private int gender;
  @SerializedName("age") private int age;
  @SerializedName("birthDate") private Date birthDate;
  @SerializedName("childrenCount") private int childrenCount;
  @SerializedName("minChildAge") private int minChildAge;
  @SerializedName("maxChildAge") private int maxChildAge;
  @SerializedName("picture") private String picture;
  @SerializedName("createdAt") private Date createdAt;
  @SerializedName("updatedAt") private Date updatedAt;
  @SerializedName("milestones") private List<Integer> milestones;
  @SerializedName("favoriteCategories") private List<String> favoriteCategories;

  public UserNetworkResponse() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getProfileId() {
    return profileId;
  }

  public void setProfileId(int profileId) {
    this.profileId = profileId;
  }

  public String getReadToKidsId() {
    return readToKidsId;
  }

  public void setReadToKidsId(String readToKidsId) {
    this.readToKidsId = readToKidsId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isEmailConfirmed() {
    return emailConfirmed;
  }

  public void setEmailConfirmed(boolean emailConfirmed) {
    this.emailConfirmed = emailConfirmed;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getPagesPerDay() {
    return pagesPerDay;
  }

  public void setPagesPerDay(int pagesPerDay) {
    this.pagesPerDay = pagesPerDay;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public int getFontSize() {
    return fontSize;
  }

  public void setFontSize(int fontSize) {
    this.fontSize = fontSize;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public int getChildrenCount() {
    return childrenCount;
  }

  public void setChildrenCount(int childrenCount) {
    this.childrenCount = childrenCount;
  }

  public int getMinChildAge() {
    return minChildAge;
  }

  public void setMinChildAge(int minChildAge) {
    this.minChildAge = minChildAge;
  }

  public int getMaxChildAge() {
    return maxChildAge;
  }

  public void setMaxChildAge(int maxChildAge) {
    this.maxChildAge = maxChildAge;
  }

  public String getPicture() {
    return picture;
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

  public List<Integer> getMilestones() {
    return milestones;
  }

  public void setMilestones(List<Integer> milestones) {
    this.milestones = milestones;
  }

  public List<String> getFavoriteCategories() {
    return favoriteCategories;
  }

  public void setFavoriteCategories(List<String> favoriteCategories) {
    this.favoriteCategories = favoriteCategories;
  }

  @Override public String getIdentifier() {
    return id;
  }
}
