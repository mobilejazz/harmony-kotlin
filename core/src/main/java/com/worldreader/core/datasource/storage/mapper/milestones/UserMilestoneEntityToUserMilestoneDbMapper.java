package com.worldreader.core.datasource.storage.mapper.milestones;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.milestones.UserMilestoneEntity;
import com.worldreader.core.datasource.storage.model.UserMilestoneDb;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class UserMilestoneEntityToUserMilestoneDbMapper
    implements Mapper<Optional<UserMilestoneEntity>, Optional<UserMilestoneDb>> {

  private final Gson gson;

  @Inject public UserMilestoneEntityToUserMilestoneDbMapper(final Gson gson) {
    this.gson = gson;
  }

  @Override
  public Optional<UserMilestoneDb> transform(final Optional<UserMilestoneEntity> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final UserMilestoneEntity raw = optional.get();
      final UserMilestoneDb entity = new UserMilestoneDb.Builder().withUserId(raw.getUserId())
          .withMilestoneId(raw.getMilestoneId())
          .withScore(raw.getScore())
          .withSync(raw.isSync())
          .withState(raw.getState())
          .withCreatedAt(getIsoDate(raw.getCreatedAt()))
          .withUpdatedAt(getIsoDate(raw.getUpdatedAt()))
          .build();
      return Optional.of(entity);
    }
  }

  private String getIsoDate(final Date createdAt) {
    Date date;
    if (createdAt == null) {
      date = new Date();
    } else {
      date = createdAt;
    }
    return gson.toJson(date).replaceAll("\"", "");
  }
}
