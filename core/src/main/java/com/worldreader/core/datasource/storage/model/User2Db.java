package com.worldreader.core.datasource.storage.model;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.util.*;

import static com.worldreader.core.datasource.storage.datasource.cache.manager.table.UsersTable.*;

@StorIOSQLiteType(table = TABLE) public class User2Db {

  @StorIOSQLiteColumn(name = COLUMN_ID, key = true) public String id;
  @StorIOSQLiteColumn(name = COLUMN_PROFILE_ID) public int profileId;
  @StorIOSQLiteColumn(name = COLUMN_READS_TO_KIDS_ID) public String readToKidsId;
  @StorIOSQLiteColumn(name = COLUMN_USERNAME) public String userName;
  @StorIOSQLiteColumn(name = COLUMN_NAME) public String name;
  @StorIOSQLiteColumn(name = COLUMN_EMAIL) public String email;
  @StorIOSQLiteColumn(name = COLUMN_EMAIL_CONFIRMED) public boolean emailConfirmed;
  @StorIOSQLiteColumn(name = COLUMN_PAGES_PER_DAY) public int pagesPerDay;
  @StorIOSQLiteColumn(name = COLUMN_LOCALE) public String locale;
  @StorIOSQLiteColumn(name = COLUMN_FONT_SIZE) public int fontSize;
  @StorIOSQLiteColumn(name = COLUMN_GENDER) public int gender;
  @StorIOSQLiteColumn(name = COLUMN_AGE) public int age;
  @StorIOSQLiteColumn(name = COLUMN_BIRTHDATE) public Long birthDate;
  @StorIOSQLiteColumn(name = COLUMN_CHILDREN_COUNT) public int childrenCount;
  @StorIOSQLiteColumn(name = COLUMN_MIN_CHILD_AGE) public int minChildAge;
  @StorIOSQLiteColumn(name = COLUMN_MAX_CHILD_AGE) public int maxChildAge;
  @StorIOSQLiteColumn(name = COLUMN_PICTURE) public String picture;
  @StorIOSQLiteColumn(name = COLUMN_CREATED_AT) public long createdAt;
  @StorIOSQLiteColumn(name = COLUMN_UPDATED_AT) public long updatedAt;
  @StorIOSQLiteColumn(name = COLUMN_MILESTONES) public String milestones;
  @StorIOSQLiteColumn(name = COLUMN_FAVORITE_CATEGORIES) public String favoriteCategories;
  @StorIOSQLiteColumn(name = COLUMN_LOCAL_LIBRARY) public String localLibrary;
  @StorIOSQLiteColumn(name = COLUMN_CHILD_NAME) public String childName;
  @StorIOSQLiteColumn(name = COLUMN_AVATAR_ID) public String avatarId;
  @StorIOSQLiteColumn(name = COLUMN_RELATIONSHIP) public String relationship;

  public User2Db() {
  }

  public User2Db(final String id, final int profileId, final String readToKidsId,
      final String userName, final String name, final String email, final boolean emailConfirmed,
      final int pagesPerDay, final String locale, final int fontSize, final int gender,
      final int age, final Long birthDate, final int childrenCount, final int minChildAge,
      final int maxChildAge, final String picture, final long createdAt, final long updatedAt,
      final String milestones, final String favoriteCategories, final String localLibrary,
      final String childName, final String avatarId, final String relationship) {
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
    this.childName = childName;
    this.avatarId = avatarId;
    this.relationship = relationship;
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public int getProfileId() {
    return profileId;
  }

  public void setProfileId(final int profileId) {
    this.profileId = profileId;
  }

  public String getReadToKidsId() {
    return readToKidsId;
  }

  public void setReadToKidsId(final String readToKidsId) {
    this.readToKidsId = readToKidsId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(final String userName) {
    this.userName = userName;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public boolean isEmailConfirmed() {
    return emailConfirmed;
  }

  public void setEmailConfirmed(final boolean emailConfirmed) {
    this.emailConfirmed = emailConfirmed;
  }

  public int getPagesPerDay() {
    return pagesPerDay;
  }

  public void setPagesPerDay(final int pagesPerDay) {
    this.pagesPerDay = pagesPerDay;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(final String locale) {
    this.locale = locale;
  }

  public int getFontSize() {
    return fontSize;
  }

  public void setFontSize(final int fontSize) {
    this.fontSize = fontSize;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(final int gender) {
    this.gender = gender;
  }

  public int getAge() {
    return age;
  }

  public void setAge(final int age) {
    this.age = age;
  }

  public Long getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(final Long birthDate) {
    this.birthDate = birthDate;
  }

  public int getChildrenCount() {
    return childrenCount;
  }

  public void setChildrenCount(final int childrenCount) {
    this.childrenCount = childrenCount;
  }

  public int getMinChildAge() {
    return minChildAge;
  }

  public void setMinChildAge(final int minChildAge) {
    this.minChildAge = minChildAge;
  }

  public int getMaxChildAge() {
    return maxChildAge;
  }

  public void setMaxChildAge(final int maxChildAge) {
    this.maxChildAge = maxChildAge;
  }

  public void setPicture(final String picture) {
    this.picture = picture;
  }

  public String getPicture() {
    return picture;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(final long createdAt) {
    this.createdAt = createdAt;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(final long updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getMilestones() {
    return milestones;
  }

  public void setMilestones(final String milestones) {
    this.milestones = milestones;
  }

  public String getFavoriteCategories() {
    return favoriteCategories;
  }

  public void setFavoriteCategories(final String favoriteCategories) {
    this.favoriteCategories = favoriteCategories;
  }

  public String getLocalLibrary() {
    return localLibrary;
  }

  public void setLocalLibrary(final String localLibrary) {
    this.localLibrary = localLibrary;
  }

  public String getChildName() {
    return childName;
  }

  public void setChildName(String childName) {
    this.childName = childName;
  }

  public String getAvatarId() {
    return avatarId;
  }

  public void setAvatarId(String avatarId) {
    this.avatarId = avatarId;
  }

  public String getRelationship() {
    return relationship;
  }

  public void setRelationship(String relationship) {
    this.relationship = relationship;
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
    private Long birthDate;
    private int childrenCount;
    private int minChildAge;
    private int maxChildAge;
    private String picture;
    private long createdAt;
    private long updatedAt;
    private String milestones;
    private String favoriteCategories;
    private String localLibrary;
    private String childName;
    private String avatarId;
    private String relationship;

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
      if (birthDate == null) {
        return this;
      }

      this.birthDate = birthDate.getTime();
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
      if (createdAt == null) {
        return this;
      }

      this.createdAt = createdAt.getTime();
      return this;
    }

    public Builder setUpdatedAt(Date updatedAt) {
      if (updatedAt == null) {
        return this;
      }

      this.updatedAt = updatedAt.getTime();
      return this;
    }

    public Builder setMilestones(String milestones) {
      this.milestones = milestones;
      return this;
    }

    public Builder setFavoriteCategories(String favoriteCategories) {
      this.favoriteCategories = favoriteCategories;
      return this;
    }

    public Builder setPicture(String picture) {
      this.picture = picture;
      return this;
    }

    public Builder setLocalLibrary(String localLibrary) {
      this.localLibrary = localLibrary;
      return this;
    }

    public Builder setChildName(String childName) {
      this.childName = childName;
      return this;
    }

    public Builder setAvatarId(String avatarId) {
      this.avatarId = avatarId;
      return this;
    }

    public Builder setRelationship(String relationship) {
      this.relationship = relationship;
      return this;
    }

    public User2Db build() {
      return new User2Db(id, profileId, readToKidsId, userName, name, email, emailConfirmed,
          pagesPerDay, locale, fontSize, gender, age, birthDate, childrenCount, minChildAge,
          maxChildAge, picture, createdAt, updatedAt, milestones, favoriteCategories, localLibrary, childName, avatarId, relationship);
    }
  }
}
