package com.worldreader.core.datasource.spec.userbooks;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBooksTable;

public class RemoveUserBookStorageSpec extends UserBookStorageSpecification {

  public RemoveUserBookStorageSpec(final String bookId, final String userId) {
    super(bookId, userId);
  }

  @Override public Query toQuery() {
    throw new UnsupportedOperationException("toQuery() not supported");
  }

  @Override public DeleteQuery toDeleteQuery() {
    return DeleteQuery.builder()
        .table(UserBooksTable.TABLE)
        .where(UserBooksTable.COLUMN_BOOK_ID
            + " LIKE ? AND "
            + UserBooksTable.COLUMN_USER_ID
            + " LIKE ?")
        .whereArgs(getBookId(), getUserId())
        .build();
  }
}
