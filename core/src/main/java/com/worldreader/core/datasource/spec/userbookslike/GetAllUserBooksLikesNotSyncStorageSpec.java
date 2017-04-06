package com.worldreader.core.datasource.spec.userbookslike;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBookLikesTable;

public class GetAllUserBooksLikesNotSyncStorageSpec extends UserBookLikeStorageSpec {

  public GetAllUserBooksLikesNotSyncStorageSpec(final UserStorageSpecification.UserTarget target) {
    super(target);
  }

  @Override public Query toQuery() {
    return Query.builder()
        .table(UserBookLikesTable.TABLE)
        .where(UserBookLikesTable.COLUMN_USER_ID + " LIKE ? AND " + UserBookLikesTable.COLUMN_SYNCHRONIZED + " = 0")
        .whereArgs(getUserId())
        .build();
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }
}
