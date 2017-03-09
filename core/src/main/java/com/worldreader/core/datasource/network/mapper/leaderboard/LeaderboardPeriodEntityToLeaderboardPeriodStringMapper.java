package com.worldreader.core.datasource.network.mapper.leaderboard;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.LeaderboardPeriodEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class LeaderboardPeriodEntityToLeaderboardPeriodStringMapper
    implements Mapper<Optional<LeaderboardPeriodEntity>, Optional<String>> {

  @Inject public LeaderboardPeriodEntityToLeaderboardPeriodStringMapper() {
  }

  @Override public Optional<String> transform(Optional<LeaderboardPeriodEntity> optional) {
    if (!optional.isPresent()) {
      return Optional.of(""); // Global
    } else {
      final LeaderboardPeriodEntity raw = optional.get();
      switch (raw) {
        case GLOBAL:
          return Optional.of(""); // Global
        case MONTHLY:
          return Optional.of("monthly");
        case WEEKLY:
          return Optional.of("weekly");
        default:
          throw new IllegalArgumentException("Illegal LeaderboardPeriod passed as argument!");
      }
    }
  }

}
