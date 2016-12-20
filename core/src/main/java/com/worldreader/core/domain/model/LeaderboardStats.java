package com.worldreader.core.domain.model;

import java.util.*;

// Inmutable
public class LeaderboardStats {

  private List<LeaderboardStat> leaderboardStats;

  public LeaderboardStats() {
    leaderboardStats = new ArrayList<>();
  }

  public LeaderboardStats(List<LeaderboardStat> stats) {
    if (stats == null) {
      throw new IllegalArgumentException("stats == null");
    }

    this.leaderboardStats = stats;
  }

  public List<LeaderboardStat> getLeaderboardStats() {
    return leaderboardStats;
  }

}
