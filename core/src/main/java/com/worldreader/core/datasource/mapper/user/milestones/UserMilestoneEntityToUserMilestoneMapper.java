package com.worldreader.core.datasource.mapper.user.milestones;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.milestones.UserMilestoneEntity;
import com.worldreader.core.domain.model.user.UserMilestone;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserMilestoneEntityToUserMilestoneMapper
    implements Mapper<Optional<UserMilestoneEntity>, Optional<UserMilestone>> {

  @Inject public UserMilestoneEntityToUserMilestoneMapper() {
  }

  @Override public Optional<UserMilestone> transform(final Optional<UserMilestoneEntity> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final UserMilestoneEntity raw = optional.get();
      final UserMilestone entity = new UserMilestone.Builder().withUserId(raw.getUserId())
          .withMilestoneId(raw.getMilestoneId())
          .withScore(raw.getScore())
          .withSync(raw.isSync())
          .withState(raw.getState())
          .withCreatedAt(raw.getCreatedAt())
          .withUpdatedAt(raw.getUpdatedAt())
          .build();
      return Optional.of(entity);
    }
  }
}
