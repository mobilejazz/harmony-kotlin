package com.worldreader.core.datasource.network.model;

import com.worldreader.core.common.annotation.Immutable;

@Immutable public class UserPointsNetworkBody {

  private final int points;

  public UserPointsNetworkBody(int points) {
    this.points = points;
  }

  public int getPoints() {
    return points;
  }

}
