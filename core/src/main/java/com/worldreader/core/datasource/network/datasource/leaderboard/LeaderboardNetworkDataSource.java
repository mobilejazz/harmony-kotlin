package com.worldreader.core.datasource.network.datasource.leaderboard;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.datasource.model.LeaderboardStatsEntity;

public interface LeaderboardNetworkDataSource {

  void getGlobalLeaderboardStats(int offset, CompletionCallback<LeaderboardStatsEntity> callback);

  void getWeeklyLeaderboardStats(int offset, CompletionCallback<LeaderboardStatsEntity> callback);

  void getMonthlyLeaderboardStats(int offset, CompletionCallback<LeaderboardStatsEntity> callback);

}
