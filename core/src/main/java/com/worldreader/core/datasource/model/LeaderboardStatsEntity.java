package com.worldreader.core.datasource.model;

import java.util.*;

public class LeaderboardStatsEntity {

  private List<LeaderboardStatEntity> leaderboardStats;

  public LeaderboardStatsEntity() {
    leaderboardStats = new ArrayList<>();
  }

  public LeaderboardStatsEntity(List<LeaderboardStatEntity> stats) {
    if (stats == null) {
      throw new IllegalArgumentException("stats == null");
    }

    this.leaderboardStats = stats;
  }

  public List<LeaderboardStatEntity> getLeaderboardStats() {
    return leaderboardStats;
  }

}
