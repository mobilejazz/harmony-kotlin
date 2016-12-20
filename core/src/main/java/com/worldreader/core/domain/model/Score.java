package com.worldreader.core.domain.model;

public enum Score {

  ONE_STAR(1),

  TWO_STARS(2),

  THREE_STARS(3),

  FOUR_STARS(4),

  FIVE_STARS(5);

  private int score;

  Score(int score) {
    this.score = score;
  }

  public int getScore() {
    return score;
  }

}
