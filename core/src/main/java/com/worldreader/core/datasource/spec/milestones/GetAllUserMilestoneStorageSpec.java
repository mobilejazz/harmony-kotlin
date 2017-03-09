package com.worldreader.core.datasource.spec.milestones;

import android.support.annotation.Nullable;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserMilestonesTable;
import com.worldreader.core.domain.model.Level;
import com.worldreader.core.domain.model.user.Milestone;
import org.javatuples.Pair;

public class GetAllUserMilestoneStorageSpec extends UserMilestoneStorageSpecification {

  @Nullable private Integer score;

  public static Builder builder() {
    return Builder.builder();
  }

  public GetAllUserMilestoneStorageSpec() {
  }

  public GetAllUserMilestoneStorageSpec(UserStorageSpecification.UserTarget target) {
    super(target);
  }

  public void setScore(@Nullable Integer score) {
    this.score = score;
  }

  @Override public Query toQuery() {
    final Query.CompleteBuilder builder = Query.builder().table(UserMilestonesTable.TABLE);

    if (score != null) {
      final Level.Name level = Level.Name.getLevelFromScore(score);
      final Pair<Integer, Integer> minMaxMilestonePair = obtainMinMaxMilestoneIdPair(level);
      final Integer minMilestoneId = minMaxMilestonePair.getValue0();
      final Integer maxMilestoneId = minMaxMilestonePair.getValue1();
      builder.where(UserMilestonesTable.COLUMN_MILESTONE_ID
          + " >= ? AND "
          + UserMilestonesTable.COLUMN_MILESTONE_ID
          + " <= ? ORDER BY "
          + UserMilestonesTable.COLUMN_MILESTONE_ID
          + " ASC").whereArgs(minMilestoneId, maxMilestoneId);
    }

    return builder.build();
  }

  private Pair<Integer, Integer> obtainMinMaxMilestoneIdPair(final Level.Name level) {
    switch (level) {
      case VISITOR:
      default:
        return Pair.with(Milestone.ID.VISITOR_SET_YOUR_GOALS, Milestone.ID.VISITOR_LIKE_BOOK);
      case AVID_READER:
        return Pair.with(Milestone.ID.AVID_READER_USE_TTS,
            Milestone.ID.AVID_READER_ACHIEVE_DAILY_GOAL);
      case VORACIOUS_READER:
        return Pair.with(Milestone.ID.VORACIOUS_READER_ACHIEVE_DAILY_GOAL,
            Milestone.ID.VORACIOUS_READER_READ_ON_3_CONTINUOUS_DAYS);
      case CRITICAL_REASONER:
        return Pair.with(Milestone.ID.CRITICAL_REASONER_ACHIEVE_DAILY_GOAL,
            Milestone.ID.CRITICAL_REASONER_LIKE_BOOKS);
      case POTENTIAL_MASTER:
        return Pair.with(Milestone.ID.POTENTIAL_MASTER_ACHIEVE_DAILY_GOAL,
            Milestone.ID.POTENTIAL_MASTER_COMPLETE_BOOKS);
      case MASTER:
        return Pair.with(Milestone.ID.MASTER_SHARE_BADGE, Milestone.ID.MASTER_COMPLETE_BOOKS);
    }
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }

  public static class Builder {

    private UserStorageSpecification.UserTarget target;
    private Integer score;

    public static Builder builder() {
      return new Builder();
    }

    private Builder() {
    }

    public Builder withTarget(UserStorageSpecification.UserTarget target) {
      this.target = target;
      return this;
    }

    public Builder withScore(Integer score) {
      this.score = score;
      return this;
    }

    public GetAllUserMilestoneStorageSpec build() {
      final GetAllUserMilestoneStorageSpec spec = new GetAllUserMilestoneStorageSpec(target);
      spec.setScore(score);
      return spec;
    }
  }
}
