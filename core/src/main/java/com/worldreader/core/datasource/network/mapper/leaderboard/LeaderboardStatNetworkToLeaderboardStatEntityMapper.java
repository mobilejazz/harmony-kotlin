package com.worldreader.core.datasource.network.mapper.leaderboard;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.LeaderboardStatEntity;
import com.worldreader.core.datasource.network.model.LeaderboardStatNetwork;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class LeaderboardStatNetworkToLeaderboardStatEntityMapper
    implements Mapper<Optional<LeaderboardStatNetwork>, Optional<LeaderboardStatEntity>> {

  @Inject public LeaderboardStatNetworkToLeaderboardStatEntityMapper() {
  }

  @Override
  public Optional<LeaderboardStatEntity> transform(Optional<LeaderboardStatNetwork> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final LeaderboardStatNetwork raw = optional.get();
      final LeaderboardStatEntity entity =
          new LeaderboardStatEntity.Builder().withUsername(raw.getUsername())
              .withRank(raw.getRank())
              .withScore(raw.getScore())
              .build();
      return Optional.of(entity);
    }
  }
}
