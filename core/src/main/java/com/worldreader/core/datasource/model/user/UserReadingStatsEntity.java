package com.worldreader.core.datasource.model.user;

import android.support.annotation.NonNull;
import com.google.common.base.Preconditions;
import com.worldreader.core.common.annotation.Immutable;

import java.util.*;

@Immutable public class UserReadingStatsEntity {

  private final List<Stat> stats;

  public UserReadingStatsEntity() {
    this.stats = Collections.emptyList();
  }

  public UserReadingStatsEntity(@NonNull final List<Stat> stats) {
    this.stats = Collections.unmodifiableList(Preconditions.checkNotNull(stats, "stats == null"));
  }

  public List<Stat> getStats() {
    return stats;
  }

  @Immutable public static class Stat {

    private final String date;
    private final int count;

    public Stat(@NonNull final String date, @NonNull final int count) {
      this.date = Preconditions.checkNotNull(date, "date == null");
      this.count = Preconditions.checkNotNull(count, "count == null");
    }

    public String getDate() {
      return date;
    }

    public int getCount() {
      return count;
    }
  }
}
