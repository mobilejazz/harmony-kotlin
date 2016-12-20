package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.*;

public class LeaderboardStatsNetwork {

  @SerializedName("leaderboard") private List<LeaderboardStatNetwork> leaderboardStats;

  public LeaderboardStatsNetwork() {
    leaderboardStats = new ArrayList<>();
  }

  public LeaderboardStatsNetwork(List<LeaderboardStatNetwork> stats) {
    if (stats == null) {
      throw new IllegalArgumentException("stats == null");
    }
    this.leaderboardStats = stats;
  }

  public List<LeaderboardStatNetwork> getLeaderboardStats() {
    return leaderboardStats;
  }

  public void setLeaderboardStats(List<LeaderboardStatNetwork> leaderboardStats) {
    this.leaderboardStats = leaderboardStats;
  }
}
