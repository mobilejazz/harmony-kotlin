package com.worldreader.core.datasource.storage.mapper.milestones;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.milestones.UserMilestoneEntity;
import com.worldreader.core.datasource.storage.model.UserMilestoneDb;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class ListUserMilestoneEntityDbToListUserMilestoneEntityMapper
    implements Mapper<Optional<List<UserMilestoneDb>>, Optional<List<UserMilestoneEntity>>> {

  private final Mapper<Optional<UserMilestoneDb>, Optional<UserMilestoneEntity>>
      toUserMilestoneEntityMapper;

  @Inject public ListUserMilestoneEntityDbToListUserMilestoneEntityMapper(
      UserMilestoneDbToUserMilestoneEntityMapper toUserMilestoneEntityMapper) {
    this.toUserMilestoneEntityMapper = toUserMilestoneEntityMapper;
  }

  @Override public Optional<List<UserMilestoneEntity>> transform(
      final Optional<List<UserMilestoneDb>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserMilestoneDb> raw = optional.get();
      final List<UserMilestoneEntity> entities = Lists.newArrayListWithCapacity(raw.size());
      for (final UserMilestoneDb rawUserMilestoneDb : raw) {
        entities.add(
            toUserMilestoneEntityMapper.transform(Optional.fromNullable(rawUserMilestoneDb))
                .orNull());
      }
      return Optional.of(entities);
    }
  }

}
