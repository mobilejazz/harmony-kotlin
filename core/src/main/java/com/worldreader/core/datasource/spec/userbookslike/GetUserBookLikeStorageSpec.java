package com.worldreader.core.datasource.spec.userbookslike;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBookLikesTable;

public class GetUserBookLikeStorageSpec extends UserBookLikeStorageSpec {

  public GetUserBookLikeStorageSpec(final String bookId, final String userId) {
    super(bookId, userId);
  }

  @Override public Query toQuery() {
    return Query.builder()
        .table(UserBookLikesTable.TABLE)
        .where(UserBookLikesTable.COLUMN_BOOK_ID + " LIKE ? AND " + UserBookLikesTable.COLUMN_USER_ID + " LIKE ?")
        .whereArgs(getBookId(), getUserId())
        .build();
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }
}
