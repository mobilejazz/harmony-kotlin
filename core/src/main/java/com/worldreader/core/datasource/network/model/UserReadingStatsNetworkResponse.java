package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.*;

public class UserReadingStatsNetworkResponse {

  @SerializedName("statistics") List<Stat> stats;

  public UserReadingStatsNetworkResponse() {
  }

  public List<Stat> getStats() {
    return stats;
  }

  public void setStats(List<Stat> stats) {
    this.stats = stats;
  }

  public static class Stat {

    @SerializedName("date") String date;
    @SerializedName("value") int count;

    public Stat(String date, int readCount) {
      this.date = date;
      this.count = readCount;
    }

    public String getDate() {
      return date;
    }

    public void setDate(String date) {
      this.date = date;
    }

    public int getReadCount() {
      return count;
    }

    public void setReadCount(int readCount) {
      this.count = readCount;
    }
  }
}
