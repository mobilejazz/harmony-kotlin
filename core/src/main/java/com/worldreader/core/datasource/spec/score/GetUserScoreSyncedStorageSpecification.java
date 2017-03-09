package com.worldreader.core.datasource.spec.score;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserScoreTable;

public class GetUserScoreSyncedStorageSpecification extends UserScoreStorageSpecification {

  private final String userId;

  public GetUserScoreSyncedStorageSpecification(final String userId) {
    this.userId = userId;
  }

  @Override public Query toQuery() {
    return Query.builder()
        .table(UserScoreTable.TABLE)
        .where(UserScoreTable.COLUMN_USER_ID
            + " LIKE ? AND "
            + UserScoreTable.COLUMN_SYNCHRONIZED
            + " = ?")
        .whereArgs(userId, 1)
        .build();
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }
}
