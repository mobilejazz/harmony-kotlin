package com.worldreader.core.domain.model.user;

import com.worldreader.core.common.annotation.Immutable;
import com.worldreader.core.datasource.repository.model.RepositoryModel;

import java.util.*;

@Immutable public final class User2 extends RepositoryModel {

  private final String id;
  private final int profileId;
  private final String readToKidsId;
  private final String userName;
  private final String name;
  private final String email;
  private final boolean emailConfirmed;
  private final int pagesPerDay;
  private final String locale;
  private final int fontSize;
  private final int gender;
  private final int age;
  private final Date birthDate;
  private final int childrenCount;
  private final int minChildAge;
  private final int maxChildAge;
  private final String picture;
  private final Date createdAt;
  private final Date updatedAt;
  private final List<Integer> milestones;
  private final List<String> favoriteCategories;
  private final String localLibrary;

  private User2(String id, int profileId, String readToKidsId, String userName, String name,
      String email, boolean emailConfirmed, int pagesPerDay, String locale, int fontSize,
      int gender, int age, Date birthDate, int childrenCount, int minChildAge, int maxChildAge,
      String picture, Date createdAt, Date updatedAt, List<Integer> milestones,
      List<String> favoriteCategories, String localLibrary) {
    this.id = id;
    this.profileId = profileId;
    this.readToKidsId = readToKidsId;
    this.userName = userName;
    this.name = name;
    this.email = email;
    this.emailConfirmed = emailConfirmed;
    this.pagesPerDay = pagesPerDay;
    this.locale = locale;
    this.fontSize = fontSize;
    this.gender = gender;
    this.age = age;
    this.birthDate = birthDate;
    this.childrenCount = childrenCount;
    this.minChildAge = minChildAge;
    this.maxChildAge = maxChildAge;
    this.picture = picture;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.milestones = milestones;
    this.favoriteCategories = favoriteCategories;
    this.localLibrary = localLibrary;
  }

  public String getId() {
    return id;
  }

  public int getProfileId() {
    return profileId;
  }

  public String getReadToKidsId() {
    return readToKidsId;
  }

  public String getUserName() {
    return userName;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public boolean isEmailConfirmed() {
    return emailConfirmed;
  }

  public int getPagesPerDay() {
    return pagesPerDay;
  }

  public String getLocale() {
    return locale;
  }

  public int getFontSize() {
    return fontSize;
  }

  public int getGender() {
    return gender;
  }

  public int getAge() {
    return age;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public int getChildrenCount() {
    return childrenCount;
  }

  public int getMinChildAge() {
    return minChildAge;
  }

  public int getMaxChildAge() {
    return maxChildAge;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public List<Integer> getMilestones() {
    return milestones;
  }

  public List<String> getFavoriteCategories() {
    return favoriteCategories;
  }

  public String getPicture() {
    return picture;
  }

  @Override public String getIdentifier() {
    return id;
  }

  public String getLocalLibrary() {
    return localLibrary;
  }

  public static final class Builder {

    private String id;
    private int profileId;
    private String readToKidsId;
    private String userName;
    private String name;
    private String email;
    private boolean emailConfirmed;
    private int pagesPerDay;
    private String locale;
    private int fontSize;
    private int gender;
    private int age;
    private Date birthDate;
    private int childrenCount;
    private int minChildAge;
    private int maxChildAge;
    private String picture;
    private Date createdAt;
    private Date updatedAt;
    private List<Integer> milestones;
    private List<String> favoriteCategories;
    private String localLibrary;

    public Builder(final User2 raw) {
      setId(raw.getId());
      setProfileId(raw.getProfileId());
      setReadToKidsId(raw.getReadToKidsId());
      setUserName(raw.getUserName());
      setName(raw.getName());
      setEmail(raw.getEmail());
      setEmailConfirmed(raw.isEmailConfirmed());
      setPagesPerDay(raw.getPagesPerDay());
      setLocale(raw.getLocale());
      setFontSize(raw.getFontSize());
      setGender(raw.getGender());
      setAge(raw.getAge());
      setBirthDate(raw.getBirthDate());
      setChildrenCount(raw.getChildrenCount());
      setMinChildAge(raw.getMinChildAge());
      setMaxChildAge(raw.getMaxChildAge());
      setCreatedAt(raw.getCreatedAt());
      setUpdatedAt(raw.getUpdatedAt());
      setFavoriteCategories(raw.getFavoriteCategories());
      setPicture(raw.getPicture());
      setLocalLibrary(raw.getLocalLibrary());
    }

    public Builder() {
    }

    public Builder setId(String id) {
      this.id = id;
      return this;
    }

    public Builder setProfileId(int profileId) {
      this.profileId = profileId;
      return this;
    }

    public Builder setReadToKidsId(String readToKidsId) {
      this.readToKidsId = readToKidsId;
      return this;
    }

    public Builder setUserName(String userName) {
      this.userName = userName;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setEmail(String email) {
      this.email = email;
      return this;
    }

    public Builder setEmailConfirmed(boolean emailConfirmed) {
      this.emailConfirmed = emailConfirmed;
      return this;
    }

    public Builder setPagesPerDay(int pagesPerDay) {
      this.pagesPerDay = pagesPerDay;
      return this;
    }

    public Builder setLocale(String locale) {
      this.locale = locale;
      return this;
    }

    public Builder setFontSize(int fontSize) {
      this.fontSize = fontSize;
      return this;
    }

    public Builder setGender(int gender) {
      this.gender = gender;
      return this;
    }

    public Builder setAge(int age) {
      this.age = age;
      return this;
    }

    public Builder setBirthDate(Date birthDate) {
      this.birthDate = birthDate;
      return this;
    }

    public Builder setChildrenCount(int childrenCount) {
      this.childrenCount = childrenCount;
      return this;
    }

    public Builder setMinChildAge(int minChildAge) {
      this.minChildAge = minChildAge;
      return this;
    }

    public Builder setMaxChildAge(int maxChildAge) {
      this.maxChildAge = maxChildAge;
      return this;
    }

    public Builder setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder setUpdatedAt(Date updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public Builder setMilestones(List<Integer> milestones) {
      this.milestones = milestones;
      return this;
    }

    public Builder setFavoriteCategories(List<String> favoriteCategories) {
      this.favoriteCategories = favoriteCategories;
      return this;
    }

    public Builder setPicture(final String picture) {
      this.picture = picture;
      return this;
    }

    public Builder setLocalLibrary(final String localLibrary) {
      this.localLibrary = localLibrary;
      return this;
    }

    public User2 build() {
      return new User2(id, profileId, readToKidsId, userName, name, email, emailConfirmed,
          pagesPerDay, locale, fontSize, gender, age, birthDate, childrenCount, minChildAge,
          maxChildAge, picture, createdAt, updatedAt, milestones, favoriteCategories, localLibrary);
    }
  }

}
