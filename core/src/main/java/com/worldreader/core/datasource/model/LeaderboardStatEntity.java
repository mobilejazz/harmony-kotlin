package com.worldreader.core.datasource.model;

public class LeaderboardStatEntity {

  private String username;
  private int rank;
  private int score;

  public LeaderboardStatEntity(String username, int rank, int score) {
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
