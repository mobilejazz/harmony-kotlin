package com.worldreader.core.domain.model.user;

import android.support.annotation.NonNull;
import com.google.common.base.Preconditions;
import com.worldreader.core.common.annotation.Immutable;

import java.util.*;

@Immutable public class UserReadingStats {

  private final List<Stat> stats;

  public UserReadingStats() {
    this.stats = Collections.emptyList();
  }

  public UserReadingStats(@NonNull final List<Stat> stats) {
    this.stats = Collections.unmodifiableList(Preconditions.checkNotNull(stats, "stats == null"));
  }

  public List<Stat> getStats() {
    return stats;
  }

  @Immutable public static class Stat {

    private final String date;
    private final int count;

    public Stat(final String date, final int count) {
      this.date = date;
      this.count = count;
    }

    public String getDate() {
      return date;
    }

    public int getCount() {
      return count;
    }
  }
}
