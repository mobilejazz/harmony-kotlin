package com.worldreader.core.datasource.spec.userbooks;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBooksTable;

public class GetAllUserBooksLikedStorageSpec extends UserBookStorageSpecification {

  public GetAllUserBooksLikedStorageSpec() {
    super();
  }

  @Override public Query toQuery() {
    return Query.builder()
        .table(UserBooksTable.TABLE)
        .where(
            UserBooksTable.COLUMN_LIKED + " = ? AND " + UserBooksTable.COLUMN_USER_ID + " LIKE ?")
        .whereArgs(1, getUserId())
        .build();
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }

}
