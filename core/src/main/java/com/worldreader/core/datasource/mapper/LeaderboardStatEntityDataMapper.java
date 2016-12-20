package com.worldreader.core.datasource.mapper;

import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.model.LeaderboardStatEntity;
import com.worldreader.core.domain.model.LeaderboardStat;

import javax.inject.Inject;
import java.util.*;

public class LeaderboardStatEntityDataMapper
    implements Mapper<LeaderboardStat, LeaderboardStatEntity> {

  @Inject public LeaderboardStatEntityDataMapper() {
  }

  @Override public LeaderboardStat transform(LeaderboardStatEntity data) {
    return new LeaderboardStat(data.getUsername(), data.getRank(), data.getScore());
  }

  @Override public List<LeaderboardStat> transform(List<LeaderboardStatEntity> data) {
    throw new IllegalArgumentException("Not supported yet!");
  }

  @Override public LeaderboardStatEntity transformInverse(LeaderboardStat data) {
    return new LeaderboardStatEntity(data.getUsername(), data.getRank(), data.getScore());
  }

  @Override public List<LeaderboardStatEntity> transformInverse(List<LeaderboardStat> data) {
    throw new IllegalArgumentException("Not supported yet!");
  }

}
