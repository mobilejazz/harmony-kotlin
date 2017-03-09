package com.worldreader.core.datasource.mapper.user.leaderboard;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.LeaderboardPeriodEntity;
import com.worldreader.core.domain.model.user.LeaderboardPeriod;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class LeaderboardPeriodToLeaderboardPeriodEntityMapper
    implements Mapper<Optional<LeaderboardPeriod>, Optional<LeaderboardPeriodEntity>> {

  @Inject public LeaderboardPeriodToLeaderboardPeriodEntityMapper() {
  }

  @Override
  public Optional<LeaderboardPeriodEntity> transform(Optional<LeaderboardPeriod> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final LeaderboardPeriod raw = optional.get();
      switch (raw) {
        case GLOBAL:
          return Optional.of(LeaderboardPeriodEntity.GLOBAL);
        case MONTHLY:
          return Optional.of(LeaderboardPeriodEntity.MONTHLY);
        case WEEKLY:
          return Optional.of(LeaderboardPeriodEntity.WEEKLY);
        default:
          throw new IllegalArgumentException("Can't process properly this value!");
      }
    }
  }

}
