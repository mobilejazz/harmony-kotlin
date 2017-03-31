package com.worldreader.core.datasource.spec.userbookslike;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

public class PutUserBookLikeStorageSpec extends UserBookLikeStorageSpec {

  public PutUserBookLikeStorageSpec(final String bookId, final String userId) {
    super(bookId, userId);
  }

  @Override public Query toQuery() {
    return null;
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }
}
