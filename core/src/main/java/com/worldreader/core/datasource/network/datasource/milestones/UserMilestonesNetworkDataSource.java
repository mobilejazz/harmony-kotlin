package com.worldreader.core.datasource.network.datasource.milestones;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.model.user.milestones.UserMilestoneEntity;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;

import java.util.*;

public interface UserMilestonesNetworkDataSource
    extends Repository.Network<UserMilestoneEntity, RepositorySpecification> {

  void updateMilestone(UserMilestoneEntity entity, Callback<Optional<UserEntity2>> callback);

  void updateMilestones(List<UserMilestoneEntity> entities,
      Callback<Optional<UserEntity2>> callback);
}
