package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class LeaderboardStatNetwork {

  @SerializedName("user_name") private String username;
  @SerializedName("rank") private int rank;
  @SerializedName("score") private int score;

  public LeaderboardStatNetwork(String username, int rank, int score) {
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
