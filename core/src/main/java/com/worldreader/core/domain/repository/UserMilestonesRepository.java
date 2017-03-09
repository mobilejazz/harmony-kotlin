package com.worldreader.core.domain.repository;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.domain.model.user.Milestone;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.model.user.UserMilestone;

import java.util.*;

public interface UserMilestonesRepository
    extends Repository<UserMilestone, RepositorySpecification> {

  // Special method for obtaining all Milestones for creating UserMilestones
  // This case is special and is used in CreateUserMilestonesInteractor
  void getAllUserMilestones(Callback<Set<Milestone>> callback);

  void updateMilestones(final List<UserMilestone> entities,
      final Callback<Optional<User2>> callback);
}
