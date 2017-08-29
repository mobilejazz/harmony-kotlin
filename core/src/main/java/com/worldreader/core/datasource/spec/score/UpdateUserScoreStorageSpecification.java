package com.worldreader.core.datasource.spec.score;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

public class UpdateUserScoreStorageSpecification extends UserScoreStorageSpecification {

  private final boolean shouldResetUserScore;

  public UpdateUserScoreStorageSpecification(final boolean shouldResetUserScore) {
    this.shouldResetUserScore = shouldResetUserScore;
  }

  public boolean shouldResetUserScore() {
    return shouldResetUserScore;
  }

  // nothing to be implemented
  @Override public Query toQuery() {
    return null;
  }

  // nothing to be implemented
  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }
}
