package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class UserGoalsBody {

  @SerializedName("pagesPerDay") private final int pagesPerDay;
  @SerializedName("minChildAge") private final int minChildAge;
  @SerializedName("maxChildAge") private final int maxChildAge;

  public UserGoalsBody(int pagesPerDay, int minChildAge, int maxChildAge) {
    this.pagesPerDay = pagesPerDay;
    this.minChildAge = minChildAge;
    this.maxChildAge = maxChildAge;
  }

  public int getPagesPerDay() {
    return pagesPerDay;
  }

  public int getMinChildAge() {
    return minChildAge;
  }

  public int getMaxChildAge() {
    return maxChildAge;
  }

}
