package com.worldreader.core.domain.model;

import com.worldreader.core.common.annotation.Immutable;
import com.worldreader.core.datasource.repository.model.RepositoryModel;

@Immutable public class LeaderboardStat extends RepositoryModel {

  private final String username;
  private final int rank;
  private final int score;

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

  @Override public String getIdentifier() {
    return username;
  }

  public static final class Builder {

    private String username;
    private int rank;
    private int score;

    public Builder() {
    }

    public Builder withUsername(String username) {
      this.username = username;
      return this;
    }

    public Builder withRank(int rank) {
      this.rank = rank;
      return this;
    }

    public Builder withScore(int score) {
      this.score = score;
      return this;
    }

    public LeaderboardStat build() {
      return new LeaderboardStat(username, rank, score);
    }
  }
}

