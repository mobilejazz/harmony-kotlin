package com.worldreader.core.datasource.spec.milestones;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserMilestonesTable;

public class DeleteUnsynchronizedUserMilestonesStorageSpec
    extends UserMilestoneStorageSpecification {

  public DeleteUnsynchronizedUserMilestonesStorageSpec() {
  }

  @Override public Query toQuery() {
    return null;
  }

  @Override public DeleteQuery toDeleteQuery() {
    return DeleteQuery.builder()
        .table(UserMilestonesTable.TABLE)
        .where(UserMilestonesTable.COLUMN_USER_ID
            + " = ? AND "
            + UserMilestonesTable.COLUMN_SYNCHRONIZED
            + " = 0")
        .whereArgs(getUserId())
        .build();
  }

}
