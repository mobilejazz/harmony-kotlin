package com.worldreader.core.datasource.network.mapper;

import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.model.LeaderboardStatEntity;
import com.worldreader.core.datasource.model.LeaderboardStatsEntity;
import com.worldreader.core.datasource.network.model.LeaderboardStatNetwork;
import com.worldreader.core.datasource.network.model.LeaderboardStatsNetwork;

import javax.inject.Inject;
import java.util.*;

public class LeaderboardStatsNetworkDataMapper
    implements Mapper<LeaderboardStatsEntity, LeaderboardStatsNetwork> {

  @Inject public LeaderboardStatsNetworkDataMapper() {
  }

  @Override public LeaderboardStatsEntity transform(LeaderboardStatsNetwork data) {
    List<LeaderboardStatEntity> stats = new ArrayList<>();
    for (LeaderboardStatNetwork leaderboardStatNetwork : data.getLeaderboardStats()) {
      stats.add(new LeaderboardStatEntity(leaderboardStatNetwork.getUsername(),
          leaderboardStatNetwork.getRank(), leaderboardStatNetwork.getScore()));
    }
    return new LeaderboardStatsEntity(stats);
  }

  @Override public List<LeaderboardStatsEntity> transform(List<LeaderboardStatsNetwork> data) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override public LeaderboardStatsNetwork transformInverse(LeaderboardStatsEntity data) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public List<LeaderboardStatsNetwork> transformInverse(List<LeaderboardStatsEntity> data) {
    throw new UnsupportedOperationException("Not implemented!");
  }

}
