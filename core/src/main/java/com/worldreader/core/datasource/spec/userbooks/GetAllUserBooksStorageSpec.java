package com.worldreader.core.datasource.spec.userbooks;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBooksTable;

public class GetAllUserBooksStorageSpec extends UserBookStorageSpecification {

  @Override public Query toQuery() {
    return Query.builder().table(UserBooksTable.TABLE).build();
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }
}
