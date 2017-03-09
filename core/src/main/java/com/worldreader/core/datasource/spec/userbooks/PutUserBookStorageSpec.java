package com.worldreader.core.datasource.spec.userbooks;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

public class PutUserBookStorageSpec extends UserBookStorageSpecification {

  public PutUserBookStorageSpec(final String bookId, final String userId) {
    super(bookId, userId);
  }

  @Override public Query toQuery() {
    return null;
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }
}
