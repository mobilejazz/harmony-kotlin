package com.worldreader.core.domain.model.user;

import java.util.*;

/**
 * Object that contains the information for a Worldreader Kids user
 */
public class KidsUser {

  public enum Gender {
    FEMALE(1), MALE(2);

    private final int value;

    Gender(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

  private final String name;
  private final String childName;
  private final String avatarId;
  private final Date birthDate;
  private final Gender gender;
  private final String relationship;

  private KidsUser(String name, String childName, String avatarId, Date birthDate, Gender gender, String relationship) {
    this.name = name;
    this.childName = childName;
    this.avatarId = avatarId;
    this.birthDate = birthDate;
    this.gender = gender;
    this.relationship = relationship;
  }

  public String getName() {
    return name;
  }

  public String getChildName() {
    return childName;
  }

  public String getAvatarId() {
    return avatarId;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public Gender getGender() {
    return gender;
  }

  public String getRelationship() {
    return relationship;
  }

  public static class Builder {

    private String name;
    private String childName;
    private String avatarId;
    private Date birthDate;
    private Gender gender;
    private String relationship;

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setChildName(String childName) {
      this.childName = childName;
      return this;
    }

    public String getChildName() {
      return childName;
    }

    public Builder setAvatarId(String avatarId) {
      this.avatarId = avatarId;
      return this;
    }

    public Builder setBirthDate(Date birthDate) {
      this.birthDate = birthDate;
      return this;
    }

    public Date getBirthDate() {
      return birthDate;
    }

    public Builder setGender(Gender gender) {
      this.gender = gender;
      return this;
    }

    public Gender getGender() {
      return gender;
    }

    public Builder setRelationship(String relationship) {
      this.relationship = relationship;
      return this;
    }

    public KidsUser build() {
      return new KidsUser(name, childName, avatarId, birthDate, gender, relationship);
    }
  }
}
