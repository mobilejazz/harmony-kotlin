package com.worldreader.core.datasource.storage.mapper.user;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.datasource.storage.model.User2Db;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserEntity2ToUser2DbMapper
    implements Mapper<Optional<UserEntity2>, Optional<User2Db>> {

  private final Gson gson;

  @Inject public UserEntity2ToUser2DbMapper(Gson gson) {
    this.gson = gson;
  }

  @Override public Optional<User2Db> transform(final Optional<UserEntity2> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final UserEntity2 raw = optional.get();
      final User2Db user2Db = new User2Db.Builder().setId(raw.getId())
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
          .setBirthDate(raw.getBirthDate())
          .setChildrenCount(raw.getChildrenCount())
          .setMinChildAge(raw.getMinChildAge())
          .setMaxChildAge(raw.getMaxChildAge())
          .setCreatedAt(raw.getCreatedAt())
          .setUpdatedAt(raw.getUpdatedAt())
          .setPicture(raw.getPicture())
          .setMilestones(gson.toJson(raw.getMilestones()))
          .setFavoriteCategories(gson.toJson(raw.getFavoriteCategories()))
          .setLocalLibrary(raw.getLocalLibrary())
          .setChildName(raw.getChildName())
          .setAvatarId(raw.getAvatarId())
          .setRelationship(raw.getRelationship())
          .build();
      return Optional.of(user2Db);
    }
  }

}
