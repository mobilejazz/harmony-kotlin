package com.worldreader.core.datasource.network.mapper.user;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.UserReadingStatsEntity;
import com.worldreader.core.datasource.network.model.UserReadingStatsNetworkResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class UserReadingStatsNetworkResponseToUserReadingStatsEntityMapper
    implements Mapper<Optional<UserReadingStatsNetworkResponse>, Optional<UserReadingStatsEntity>> {

  private final StatNetworkResponseToStatEntityMapper toStatEntityMapper;

  @Inject public UserReadingStatsNetworkResponseToUserReadingStatsEntityMapper() {
    this.toStatEntityMapper = new StatNetworkResponseToStatEntityMapper();
  }

  @Override public Optional<UserReadingStatsEntity> transform(
      final Optional<UserReadingStatsNetworkResponse> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final UserReadingStatsNetworkResponse raw = optional.get();
      final List<UserReadingStatsNetworkResponse.Stat> rawStats = raw.getStats();
      final List<UserReadingStatsEntity.Stat> transformedStats =
          new ArrayList<>(rawStats != null ? rawStats.size() : 0);
      if (rawStats == null) {
        return Optional.of(new UserReadingStatsEntity());
      } else {
        for (final UserReadingStatsNetworkResponse.Stat rawStat : rawStats) {
          final UserReadingStatsEntity.Stat stat = toStatEntityMapper.transform(rawStat);
          transformedStats.add(stat);
        }
        final UserReadingStatsEntity entity = new UserReadingStatsEntity(transformedStats);
        return Optional.of(entity);
      }
    }
  }

  private static class StatNetworkResponseToStatEntityMapper
      implements Mapper<UserReadingStatsNetworkResponse.Stat, UserReadingStatsEntity.Stat> {

    @Override
    public UserReadingStatsEntity.Stat transform(final UserReadingStatsNetworkResponse.Stat raw) {
      return new UserReadingStatsEntity.Stat(raw.getDate(), raw.getReadCount());
    }
  }

}
