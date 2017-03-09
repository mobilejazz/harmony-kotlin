package com.worldreader.core.datasource.spec.milestones;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserMilestonesTable;

public class GetUserMilestoneStorageSpec extends UserMilestoneStorageSpecification {

  public GetUserMilestoneStorageSpec(final UserStorageSpecification.UserTarget target,
      final String milestoneId) {
    super(target);
    this.setMilestoneid(milestoneId);
  }

  public GetUserMilestoneStorageSpec(final String milestoneId, final String userId) {
    super(milestoneId, userId);
  }

  @Override public Query toQuery() {
    return Query.builder()
        .table(UserMilestonesTable.TABLE)
        .where(UserMilestonesTable.COLUMN_MILESTONE_ID
            + " LIKE ? AND "
            + UserMilestonesTable.COLUMN_USER_ID
            + " LIKE ?")
        .whereArgs(getMilestoneId(), getUserId())
        .build();
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }
}
