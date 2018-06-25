package com.worldreader.core.datasource.storage.datasource.score;

import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.model.user.score.UserScoreEntity;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.spec.score.UserScoreStorageSpecification;

import java.util.*;

public interface UserScoreStorageDataSource
    extends Repository.Storage<UserScoreEntity, UserScoreStorageSpecification> {

  void removeAll(UserScoreStorageSpecification specification, final Callback<Void> callback);

  void getTotalUserScore(final String userId, final Callback<Integer> callback);

  void getTotalUserScoreUnSynced(final String userId, final Callback<Integer> callback);

  void getBookPagesUserScore(String userId, Callback<List<UserScoreEntity>> callback);
}