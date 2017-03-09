package com.worldreader.core.datasource.spec.milestones;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserMilestonesTable;
import com.worldreader.core.domain.model.user.UserMilestone;

public class GetAllUnsynchronizedUserMilestonesStorageSpec extends GetAllUserMilestoneStorageSpec {

  public GetAllUnsynchronizedUserMilestonesStorageSpec(UserStorageSpecification.UserTarget target) {
    super(target);
  }

  @Override public Query toQuery() {
    return Query.builder()
        .table(UserMilestonesTable.TABLE)
        .where(UserMilestonesTable.COLUMN_USER_ID
            + " LIKE ? AND "
            + UserMilestonesTable.COLUMN_STATUS
            + " = "
            + UserMilestone.STATE_DONE
            + " AND "
            + UserMilestonesTable.COLUMN_SYNCHRONIZED
            + " = 0")
        .whereArgs(getUserId())
        .build();
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }
}
