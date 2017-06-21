package com.worldreader.core.datasource.mapper;

import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.model.LeaderboardStatEntity;
import com.worldreader.core.datasource.model.LeaderboardStatsEntity;
import com.worldreader.core.domain.model.LeaderboardStat;
import com.worldreader.core.domain.model.LeaderboardStats;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class LeaderboardStatsEntityDataMapper implements Mapper<LeaderboardStats, LeaderboardStatsEntity> {

  private LeaderboardStatEntityDataMapper statEntityDataMapper;

  @Inject public LeaderboardStatsEntityDataMapper(LeaderboardStatEntityDataMapper dataMapper) {
    this.statEntityDataMapper = dataMapper;
  }

  @Override public LeaderboardStats transform(LeaderboardStatsEntity data) {
    List<LeaderboardStat> stats = new ArrayList<>();
    for (LeaderboardStatEntity leaderboardStatEntity : data.getLeaderboardStats()) {
      LeaderboardStat transformed = statEntityDataMapper.transform(leaderboardStatEntity);
      stats.add(transformed);
    }
    return new LeaderboardStats(stats);
  }

  @Override public List<LeaderboardStats> transform(List<LeaderboardStatsEntity> data) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  @Override public LeaderboardStatsEntity transformInverse(LeaderboardStats data) {
    List<LeaderboardStatEntity> stats = new ArrayList<>();
    for (LeaderboardStat leaderboardStat : data.getLeaderboardStats()) {
      LeaderboardStatEntity transformed = statEntityDataMapper.transformInverse(leaderboardStat);
      stats.add(transformed);
    }
    return new LeaderboardStatsEntity(stats);
  }

  @Override public List<LeaderboardStatsEntity> transformInverse(List<LeaderboardStats> data) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }
}
