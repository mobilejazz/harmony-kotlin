package com.worldreader.core.datasource.storage.mapper.user;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.datasource.storage.model.User2Db;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class User2DbToUserEntity2Mapper
    implements Mapper<Optional<User2Db>, Optional<UserEntity2>> {

  private final Gson gson;

  @Inject public User2DbToUserEntity2Mapper(Gson gson) {
    this.gson = gson;
  }

  @Override public Optional<UserEntity2> transform(final Optional<User2Db> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final User2Db raw = optional.get();
      final UserEntity2 userEntity = new UserEntity2.Builder().setId(raw.getId())
          .setProfileId(raw.getProfileId())
          .setReadToKidsId(raw.getReadToKidsId())
          .setUserName(raw.getUserName())
          .setName(raw.getName())
          .setEmail(raw.getEmail())
          .setEmailConfirmed(raw.isEmailConfirmed())
          .setPagesPerDay(raw.getPagesPerDay())
          .setLocale(raw.getLocale())
          .setFontSize(raw.getFontSize())
          .setGender(raw.getGender())
          .setAge(raw.getAge())
          .setBirthDate(raw.getBirthDate() != null ? new Date(raw.getBirthDate()) : null)
          .setChildrenCount(raw.getChildrenCount())
          .setMinChildAge(raw.getMinChildAge())
          .setMaxChildAge(raw.getMaxChildAge())
          .setPicture(raw.getPicture())
          .setCreatedAt(new Date(raw.getCreatedAt()))
          .setUpdatedAt(new Date(raw.getUpdatedAt()))
          .setMilestones((List<Integer>) gson.fromJson(raw.getMilestones(),
              new TypeToken<ArrayList<Integer>>() {
              }.getType()))
          .setFavoriteCategories((List<String>) gson.fromJson(raw.getFavoriteCategories(),
              new TypeToken<ArrayList<String>>() {
              }.getType()))
          .setLocalLibrary(raw.getLocalLibrary())
          .setChildName(raw.getChildName())
          .setAvatarId(raw.getAvatarId())
          .setRelationship(raw.getRelationship())
          .setChildBirthDate(raw.getChildBirthDate()!= null ? new Date(raw.getBirthDate()) : null)
          .setChildGender(raw.getChildGender())
          .build();
      return Optional.of(userEntity);
    }
  }

}
