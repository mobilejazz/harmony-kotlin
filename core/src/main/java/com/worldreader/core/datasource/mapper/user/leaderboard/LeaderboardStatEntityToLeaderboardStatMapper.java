package com.worldreader.core.datasource.mapper.user.leaderboard;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.LeaderboardStatEntity;
import com.worldreader.core.domain.model.LeaderboardStat;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class LeaderboardStatEntityToLeaderboardStatMapper
    implements Mapper<Optional<LeaderboardStatEntity>, Optional<LeaderboardStat>> {

  @Inject public LeaderboardStatEntityToLeaderboardStatMapper() {
  }

  @Override public Optional<LeaderboardStat> transform(Optional<LeaderboardStatEntity> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final LeaderboardStatEntity raw = optional.get();
      final LeaderboardStat leaderboardStat =
          new LeaderboardStat.Builder().withUsername(raw.getUsername())
              .withRank(raw.getRank())
              .withScore(raw.getScore())
              .build();
      return Optional.of(leaderboardStat);
    }
  }
}
