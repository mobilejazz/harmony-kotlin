package com.worldreader.core.datasource.storage.mapper.milestones;

import android.text.TextUtils;
import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.milestones.UserMilestoneEntity;
import com.worldreader.core.datasource.storage.model.UserMilestoneDb;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class UserMilestoneDbToUserMilestoneEntityMapper
    implements Mapper<Optional<UserMilestoneDb>, Optional<UserMilestoneEntity>> {

  private final Gson gson;

  @Inject public UserMilestoneDbToUserMilestoneEntityMapper(final Gson gson) {
    this.gson = gson;
  }

  @Override
  public Optional<UserMilestoneEntity> transform(final Optional<UserMilestoneDb> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final UserMilestoneDb raw = optional.get();
      final UserMilestoneEntity entity =
          new UserMilestoneEntity.Builder().withUserId(raw.getUserId())
              .withMilestoneId(raw.getMilestoneId())
              .withScore(raw.getScore())
              .withSync(raw.isSync())
              .withState(raw.getState())
              .withCreatedAt(toDate(raw.getCreatedAt()))
              .withUpdatedAt(toDate(raw.getUpdatedAt()))
              .build();
      return Optional.of(entity);
    }
  }

  private Date toDate(final String createdAt) {
    if (TextUtils.isEmpty(createdAt)) {
      return null;
    }

    final String date = "\"" + createdAt + "\""; // Restore previous removed comma

    return gson.fromJson(date, Date.class);
  }

}
