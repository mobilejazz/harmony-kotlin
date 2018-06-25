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
  private final Date childBirthDate;
  private final Gender childGender;
  private final String relationship;

  private KidsUser(String name, String childName, String avatarId, Date childBirthDate, Gender childGender, String relationship) {
    this.name = name;
    this.childName = childName;
    this.avatarId = avatarId;
    this.childBirthDate = childBirthDate;
    this.childGender = childGender;
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

  public Date getChildBirthDate() {
    return childBirthDate;
  }

  public Gender getChildGender() {
    return childGender;
  }

  public String getRelationship() {
    return relationship;
  }

  public static class Builder {

    private String name;
    private String childName;
    private String avatarId;
    private Date childBirthDate;
    private Gender childGender;
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

    public Builder setChildBirthDate(Date birthDate) {
      this.childBirthDate = birthDate;
      return this;
    }

    public Date getChildBirthDate() {
      return childBirthDate;
    }

    public Builder setChildGender(Gender childGender) {
      this.childGender = childGender;
      return this;
    }

    public Gender getChildGender() {
      return childGender;
    }

    public Builder setRelationship(String relationship) {
      this.relationship = relationship;
      return this;
    }

    public KidsUser build() {
      return new KidsUser(name, childName, avatarId, childBirthDate, childGender, relationship);
    }
  }
}
