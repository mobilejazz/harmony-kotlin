package com.worldreader.core.datasource.mapper.user.milestones;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.milestones.UserMilestoneEntity;
import com.worldreader.core.domain.model.user.UserMilestone;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class ListUserMilestoneToListUserMilestoneEntityMapper
    implements Mapper<Optional<List<UserMilestone>>, Optional<List<UserMilestoneEntity>>> {

  private final Mapper<Optional<UserMilestone>, Optional<UserMilestoneEntity>>
      toUserMilestoneEntityMapper;

  @Inject public ListUserMilestoneToListUserMilestoneEntityMapper(
      UserMilestoneToUserMilestoneEntityMapper mapper) {
    this.toUserMilestoneEntityMapper = mapper;
  }

  @Override public Optional<List<UserMilestoneEntity>> transform(
      final Optional<List<UserMilestone>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserMilestone> raw = optional.get();
      final List<UserMilestoneEntity> entities = Lists.newArrayListWithCapacity(raw.size());
      for (final UserMilestone rawMilestone : raw) {
        entities.add(
            toUserMilestoneEntityMapper.transform(Optional.fromNullable(rawMilestone)).orNull());
      }
      return Optional.of(entities);
    }
  }

}
