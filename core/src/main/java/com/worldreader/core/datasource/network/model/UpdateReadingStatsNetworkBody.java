package com.worldreader.core.datasource.network.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.worldreader.core.common.annotation.Immutable;

import java.util.*;

@Immutable public class UpdateReadingStatsNetworkBody {

  @SerializedName("statistics") private final List<NetworkStat> networkStats;

  public static UpdateReadingStatsNetworkBody create(final String bookId, final int pagesRead, final Date date) {
    List<NetworkStat> stats = Lists.newArrayList(new NetworkStat(date, bookId, pagesRead, 0)); // By default points are specified by pages
    return new UpdateReadingStatsNetworkBody(stats);
  }

  public UpdateReadingStatsNetworkBody(List<NetworkStat> networkStats) {
    this.networkStats = Preconditions.checkNotNull(networkStats, "networkStats == null");
  }

  public List<NetworkStat> getNetworkStats() {
    return networkStats;
  }

  @Immutable public static class NetworkStat {

    @SerializedName("date") public Date date;
    @SerializedName("book") private String book;
    @SerializedName("pages") int pages;
    @SerializedName("points") int point;

    public NetworkStat(Date date, String book, int pages, int point) {
      this.date = date;
      this.book = book;
      this.pages = pages;
      this.point = point;
    }

    public Date getDate() {
      return date;
    }

    public void setDate(Date date) {
      this.date = date;
    }

    public String getBook() {
      return book;
    }

    public void setBook(String book) {
      this.book = book;
    }

  }

}
