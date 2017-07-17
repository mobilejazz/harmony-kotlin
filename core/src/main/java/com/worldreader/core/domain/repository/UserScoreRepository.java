package com.worldreader.core.domain.repository;

import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.domain.model.user.UserScore;
import java.util.List;

public interface UserScoreRepository extends Repository<UserScore, RepositorySpecification> {

  void removeAll(RepositorySpecification specification, final Callback<Void> callback);

  void getTotalUserScore(final String userId, final Callback<Integer> callback);

  void getBookPagesUserScore(final String userId, final Callback<List<UserScore>> callback);

  void getTotalUserScoreUnsynced(final String userId, final Callback<Integer> callback);

}
