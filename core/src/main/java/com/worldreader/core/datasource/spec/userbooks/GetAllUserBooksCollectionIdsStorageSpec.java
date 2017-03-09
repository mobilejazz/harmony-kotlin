package com.worldreader.core.datasource.spec.userbooks;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBooksTable;

public class GetAllUserBooksCollectionIdsStorageSpec extends UserBookStorageSpecification {

  public GetAllUserBooksCollectionIdsStorageSpec() {
  }

  public GetAllUserBooksCollectionIdsStorageSpec(final String userId) {
    super(userId);
  }

  @Override public Query toQuery() {
    return Query.builder()
        .table(UserBooksTable.TABLE)
        .where(UserBooksTable.COLUMN_COLLECTION_IDS
            + " IS NOT NULL AND "
            + UserBooksTable.COLUMN_COLLECTION_IDS
            + " != \"\"")
        .build();
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }

}
