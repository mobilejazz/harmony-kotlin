package com.worldreader.core.datasource.network.mapper.user;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.datasource.network.model.UserNetworkResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserNetworkResponseToUserEntityMapper
    implements Mapper<Optional<UserNetworkResponse>, Optional<UserEntity2>> {

  @Inject public UserNetworkResponseToUserEntityMapper() {
  }

  @Override public Optional<UserEntity2> transform(Optional<UserNetworkResponse> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final UserNetworkResponse raw = optional.get();
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
          .setBirthDate(raw.getBirthDate())
          .setChildrenCount(raw.getChildrenCount())
          .setMinChildAge(raw.getMinChildAge())
          .setMaxChildAge(raw.getMaxChildAge())
          .setCreatedAt(raw.getCreatedAt())
          .setUpdatedAt(raw.getUpdatedAt())
          .setMilestones(raw.getMilestones())
          .setFavoriteCategories(raw.getFavoriteCategories())
          .setPicture(raw.getPicture())
          .build();
      return Optional.of(userEntity);

    }
  }

}
