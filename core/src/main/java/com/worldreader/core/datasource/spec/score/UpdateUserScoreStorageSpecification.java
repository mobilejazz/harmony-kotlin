package com.worldreader.core.datasource.spec.score;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

public class UpdateUserScoreStorageSpecification extends UserScoreStorageSpecification {

  private final String userId;
  private final int scoreId;

  public UpdateUserScoreStorageSpecification(final String userId, final int scoreId) {
    this.userId = userId;
    this.scoreId = scoreId;
  }

  @Override public Query toQuery() {
    return null;
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }
}
