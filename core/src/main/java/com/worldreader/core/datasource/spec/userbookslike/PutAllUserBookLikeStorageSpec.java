package com.worldreader.core.datasource.spec.userbookslike;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;

public class PutAllUserBookLikeStorageSpec extends UserBookLikeStorageSpec {

  public PutAllUserBookLikeStorageSpec(UserStorageSpecification.UserTarget target) {
    super(target);
  }

  @Override public Query toQuery() {
    return null;
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }
}
