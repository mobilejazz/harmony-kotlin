package com.worldreader.core.domain.interactors.leaderboard;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.LeaderboardStats;

public interface GetLeaderboardInteractor {

  enum LeaderboardPeriod {
    GLOBAL,
    MONTHLY,
    WEEKLY
  }

  void execute(LeaderboardPeriod period, int offset,
      DomainCallback<LeaderboardStats, ErrorCore> callback);

}
