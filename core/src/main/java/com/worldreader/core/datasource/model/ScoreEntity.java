package com.worldreader.core.datasource.model;

import com.google.gson.annotations.SerializedName;

public class ScoreEntity {

  @SerializedName("score") private int score;

  public ScoreEntity() {
  }

  public ScoreEntity(int score) {
    if (score < 0 || score > 5) {
      throw new IllegalArgumentException("Score must been between 1 to 5");
    }
    this.score = score;
  }

  public int getScore() {
    return this.score;
  }

}
