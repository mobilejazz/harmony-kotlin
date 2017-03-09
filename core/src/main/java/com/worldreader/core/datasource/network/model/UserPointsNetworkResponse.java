package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;
import com.worldreader.core.common.annotation.Immutable;

@Immutable public class UserPointsNetworkResponse {

  @SerializedName("score") private int score;

  public UserPointsNetworkResponse() {
  }

  public int getScore() {
    return score;
  }
}
