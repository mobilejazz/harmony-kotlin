package com.worldreader.core.datasource.storage.datasource.score;

import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.model.user.score.UserScoreEntity;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.spec.score.UserScoreStorageSpecification;

public interface UserScoreStorageDataSource
    extends Repository.Storage<UserScoreEntity, UserScoreStorageSpecification> {

  void removeAll(UserScoreStorageSpecification specification, final Callback<Void> callback);

  void getTotalUserScore(final String userId, final Callback<Integer> callback);

  void getTotalUserScoreUnSynched(final String userId, final Callback<Integer> callback);

}
