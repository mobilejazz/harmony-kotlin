package com.worldreader.core.datasource.spec.score;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;

public abstract class UserScoreStorageSpecification extends RepositorySpecification {

  public static final UserScoreStorageSpecification NONE = new UserScoreStorageSpecification() {
    @Override public Query toQuery() {
      return null;
    }

    @Override public DeleteQuery toDeleteQuery() {
      return null;
    }
  };

  public abstract Query toQuery();

  public abstract DeleteQuery toDeleteQuery();

  @Override public String getIdentifier() {
    return this.getClass().getSimpleName();
  }

}
