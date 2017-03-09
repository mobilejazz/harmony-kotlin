package com.worldreader.core.datasource.spec.milestones;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;

public class PutUserMilestonesStorageSpec extends UserMilestoneStorageSpecification {

  public PutUserMilestonesStorageSpec() {
  }

  public PutUserMilestonesStorageSpec(UserStorageSpecification.UserTarget userTarget) {
    super(userTarget);
  }

  @Override public Query toQuery() {
    return null;
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }
}
