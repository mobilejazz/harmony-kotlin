package com.worldreader.core.datasource.storage.mapper.milestones;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.milestones.UserMilestoneEntity;
import com.worldreader.core.datasource.storage.model.UserMilestoneDb;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class ListUserMilestoneEntityToListUserMilestoneDbMapper
    implements Mapper<Optional<List<UserMilestoneEntity>>, Optional<List<UserMilestoneDb>>> {

  private final Mapper<Optional<UserMilestoneEntity>, Optional<UserMilestoneDb>>
      toUserMilestoneDbMapper;

  @Inject public ListUserMilestoneEntityToListUserMilestoneDbMapper(
      UserMilestoneEntityToUserMilestoneDbMapper toUserMilestoneDbMapper) {
    this.toUserMilestoneDbMapper = toUserMilestoneDbMapper;
  }

  @Override public Optional<List<UserMilestoneDb>> transform(
      final Optional<List<UserMilestoneEntity>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserMilestoneEntity> raw = optional.get();
      final List<UserMilestoneDb> entities = Lists.newArrayListWithCapacity(raw.size());
      for (final UserMilestoneEntity rawUserMilestoneEntity : raw) {
        entities.add(
            toUserMilestoneDbMapper.transform(Optional.fromNullable(rawUserMilestoneEntity))
                .orNull());
      }
      return Optional.of(entities);
    }
  }
}
