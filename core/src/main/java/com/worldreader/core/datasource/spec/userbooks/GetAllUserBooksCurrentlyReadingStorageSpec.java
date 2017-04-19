package com.worldreader.core.datasource.spec.userbooks;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBooksTable;

public class GetAllUserBooksCurrentlyReadingStorageSpec extends UserBookStorageSpecification {

  @Override public Query toQuery() {
    return Query.builder()
        .table(UserBooksTable.TABLE)
        .where(UserBooksTable.COLUMN_MARK_IN_MY_BOOKS + " = ? ")
        .whereArgs(1)
        .orderBy("datetime(" + UserBooksTable.COLUMN_UPDATED_AT + ") DESC")
        .build();
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }

}