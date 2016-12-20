package com.worldreader.core.domain.model;

public class LeaderboardStat {

  private String username;
  private int rank;
  private int score;

  public LeaderboardStat(String username, int rank, int score) {
    this.username = username;
    this.rank = rank;
    this.score = score;
  }

  public String getUsername() {
    return username;
  }

  public int getRank() {
    return rank;
  }

  public int getScore() {
    return score;
  }
}

