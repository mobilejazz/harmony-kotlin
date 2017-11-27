package com.worldreader.core.datasource.mapper.user.user;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.UserReadingStatsEntity;
import com.worldreader.core.domain.model.user.UserReadingStats;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class UserReadingStatsEntityToUserReadingStatsMapper
    implements Mapper<Optional<UserReadingStatsEntity>, Optional<UserReadingStats>> {

  private final StatEntityToStaMapper toStatMapper;

  @Inject public UserReadingStatsEntityToUserReadingStatsMapper() {
    toStatMapper = new StatEntityToStaMapper();
  }

  @Override public Optional<UserReadingStats> transform(final Optional<UserReadingStatsEntity> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final UserReadingStatsEntity raw = optional.get();
      final List<UserReadingStatsEntity.Stat> rawStats = raw.getStats();
      if (rawStats == null) {
        return Optional.of(new UserReadingStats());
      } else {
        final List<UserReadingStats.Stat> transformedStats = new ArrayList<>(rawStats.size());
        for (final UserReadingStatsEntity.Stat rawStat : rawStats) {
          final UserReadingStats.Stat stat = toStatMapper.transform(rawStat);
          transformedStats.add(stat);
        }
        final UserReadingStats entity = new UserReadingStats(transformedStats);
        return Optional.of(entity);
      }
    }
  }

  private static class StatEntityToStaMapper implements Mapper<UserReadingStatsEntity.Stat, UserReadingStats.Stat> {

    @Override public UserReadingStats.Stat transform(final UserReadingStatsEntity.Stat raw) {
      return new UserReadingStats.Stat(raw.getDate(), raw.getCount());
    }
  }

}
