package com.worldreader.core.datasource.mapper.user.milestones;

import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.milestones.MilestoneEntity;
import com.worldreader.core.domain.model.user.Milestone;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class SetMilestoneEntityToMilestoneMapper
    implements Mapper<Set<MilestoneEntity>, Set<Milestone>> {

  @Inject public SetMilestoneEntityToMilestoneMapper() {
  }

  @Override public Set<Milestone> transform(final Set<MilestoneEntity> entities) {
    final Set<Milestone> milestones = new LinkedHashSet<>(entities.size());
    for (final MilestoneEntity milestoneEntity : entities) {
      final Milestone milestone =
          new Milestone(milestoneEntity.getId(), milestoneEntity.getDescription(),
              milestoneEntity.getPoints(), transformState(milestoneEntity.getState()),
              transformMetadata(milestoneEntity.getMetadata()));
      milestones.add(milestone);
    }
    return milestones;
  }

  private Milestone.Metadata transformMetadata(MilestoneEntity.Metadata metadata) {
    return new Milestone.Metadata(metadata.getValue());
  }

  private Milestone.State transformState(MilestoneEntity.State state) {
    switch (state) {
      case PENDING:
        return Milestone.State.PENDING;
      case IN_PROGRESS:
        return Milestone.State.IN_PROGRESS;
      case DONE:
        return Milestone.State.DONE;
      default:
        throw new IllegalArgumentException("Invalid state passed: " + state);
    }
  }

}
