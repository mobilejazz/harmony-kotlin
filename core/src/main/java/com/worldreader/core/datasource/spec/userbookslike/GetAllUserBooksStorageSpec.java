package com.worldreader.core.datasource.spec.userbookslike;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

public class GetAllUserBooksStorageSpec extends UserBookLikeStorageSpec {

  @Override public Query toQuery() {
    return null;
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }
}
