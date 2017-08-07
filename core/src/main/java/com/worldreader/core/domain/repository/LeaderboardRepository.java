package com.worldreader.core.domain.repository;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.domain.model.LeaderboardStats;

public interface LeaderboardRepository {

  enum LeaderboardPeriod {
    GLOBAL, MONTHLY, WEEKLY
  }

  void getLeaderboardStats(LeaderboardPeriod period, int offset, CompletionCallback<LeaderboardStats> callback);

}
