package com.worldreader.core.datasource;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.mapper.LeaderboardStatsEntityDataMapper;
import com.worldreader.core.datasource.model.LeaderboardStatsEntity;
import com.worldreader.core.datasource.network.datasource.leaderboard.LeaderboardNetworkDataSource;
import com.worldreader.core.domain.model.LeaderboardStats;
import com.worldreader.core.domain.repository.LeaderboardRepository;

import javax.inject.Inject;

public class LeaderboardDataSource implements LeaderboardRepository {

  private LeaderboardNetworkDataSource dataSource;
  private LeaderboardStatsEntityDataMapper dataMapper;

  @Inject public LeaderboardDataSource(LeaderboardNetworkDataSource dataSource,
      LeaderboardStatsEntityDataMapper dataMapper) {
    this.dataSource = dataSource;
    this.dataMapper = dataMapper;
  }

  @Override
  public void getLeaderboardStats(LeaderboardRepository.LeaderboardPeriod period, int offset,
      CompletionCallback<LeaderboardStats> callback) {

    switch (period) {
      case GLOBAL:
        getGlobalLeaderboardStats(offset, callback);
        break;
      case WEEKLY:
        getWeeklyLeaderboardStats(offset, callback);
        break;
      case MONTHLY:
        getMonthlyLeaderboardStats(offset, callback);
        break;
    }
  }

  private void getGlobalLeaderboardStats(int offset,
      final CompletionCallback<LeaderboardStats> callback) {
    dataSource.getGlobalLeaderboardStats(offset, new CompletionCallback<LeaderboardStatsEntity>() {
      @Override public void onSuccess(LeaderboardStatsEntity leaderboardStatsEntity) {
        LeaderboardStats transformed = dataMapper.transform(leaderboardStatsEntity);
        if (callback != null) {
          callback.onSuccess(transformed);
        }
      }

      @Override public void onError(ErrorCore errorCore) {
        if (callback != null) {
          callback.onError(errorCore);
        }
      }
    });
  }

  private void getWeeklyLeaderboardStats(int offset,
      final CompletionCallback<LeaderboardStats> callback) {
    dataSource.getWeeklyLeaderboardStats(offset, new CompletionCallback<LeaderboardStatsEntity>() {
      @Override public void onSuccess(LeaderboardStatsEntity leaderboardStatsEntity) {
        LeaderboardStats transformed = dataMapper.transform(leaderboardStatsEntity);
        if (callback != null) {
          callback.onSuccess(transformed);
        }
      }

      @Override public void onError(ErrorCore errorCore) {
        if (callback != null) {
          callback.onError(errorCore);
        }
      }
    });
  }

  private void getMonthlyLeaderboardStats(int offset,
      final CompletionCallback<LeaderboardStats> callback) {
    dataSource.getMonthlyLeaderboardStats(offset, new CompletionCallback<LeaderboardStatsEntity>() {
      @Override public void onSuccess(LeaderboardStatsEntity leaderboardStatsEntity) {
        LeaderboardStats transformed = dataMapper.transform(leaderboardStatsEntity);
        if (callback != null) {
          callback.onSuccess(transformed);
        }
      }

      @Override public void onError(ErrorCore errorCore) {
        if (callback != null) {
          callback.onError(errorCore);
        }
      }
    });
  }

}
