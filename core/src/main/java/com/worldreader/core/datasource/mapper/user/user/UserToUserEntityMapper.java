package com.worldreader.core.datasource.mapper.user.user;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.domain.model.user.User2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserToUserEntityMapper
    implements Mapper<Optional<User2>, Optional<UserEntity2>> {

  @Inject public UserToUserEntityMapper() {
  }

  @Override public Optional<UserEntity2> transform(Optional<User2> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final User2 raw = optional.get();
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
          .setLocalLibrary(raw.getLocalLibrary())
          .setChildName(raw.getChildName())
          .setAvatarId(raw.getAvatarId())
          .setRelationship(raw.getRelationship())
          .setChildBirthDate(raw.getChildBirthDate())
          .setChildGender(raw.getChildGender())
          .build();
      return Optional.of(userEntity);
    }
  }

}
