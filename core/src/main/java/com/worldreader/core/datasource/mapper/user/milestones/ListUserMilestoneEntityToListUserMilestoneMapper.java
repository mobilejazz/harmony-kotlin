package com.worldreader.core.datasource.mapper.user.milestones;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.milestones.UserMilestoneEntity;
import com.worldreader.core.domain.model.user.UserMilestone;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class ListUserMilestoneEntityToListUserMilestoneMapper
    implements Mapper<Optional<List<UserMilestoneEntity>>, Optional<List<UserMilestone>>> {

  private final Mapper<Optional<UserMilestoneEntity>, Optional<UserMilestone>>
      toUserMilestoneMapper;

  @Inject public ListUserMilestoneEntityToListUserMilestoneMapper(
      UserMilestoneEntityToUserMilestoneMapper mapper) {
    toUserMilestoneMapper = mapper;
  }

  @Override public Optional<List<UserMilestone>> transform(
      final Optional<List<UserMilestoneEntity>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserMilestoneEntity> raw = optional.get();
      final List<UserMilestone> entities = Lists.newArrayListWithCapacity(raw.size());
      for (final UserMilestoneEntity rawMilestone : raw) {
        entities.add(toUserMilestoneMapper.transform(Optional.fromNullable(rawMilestone)).orNull());
      }
      return Optional.of(entities);
    }
  }

}
