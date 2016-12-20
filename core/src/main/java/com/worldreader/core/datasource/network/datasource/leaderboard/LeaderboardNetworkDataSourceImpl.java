package com.worldreader.core.datasource.network.datasource.leaderboard;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.common.deprecated.error.adapter.ErrorRetrofitAdapter;
import com.worldreader.core.datasource.model.LeaderboardStatsEntity;
import com.worldreader.core.datasource.network.mapper.LeaderboardStatsNetworkDataMapper;
import com.worldreader.core.datasource.network.model.LeaderboardStatsNetwork;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;

public class LeaderboardNetworkDataSourceImpl implements LeaderboardNetworkDataSource {

  private static final String TAG = LeaderboardNetworkDataSource.class.getSimpleName();

  private LeaderboardApiService leaderboardApiService;
  private final LeaderboardStatsNetworkDataMapper dataMapper;
  private final Logger logger;
  private ErrorAdapter<RetrofitError> errorAdapter = new ErrorRetrofitAdapter();

  @Inject public LeaderboardNetworkDataSourceImpl(LeaderboardApiService leaderboardApiService,
      LeaderboardStatsNetworkDataMapper leaderboardStatsNetworkDataMapper, Logger logger) {
    this.leaderboardApiService = leaderboardApiService;
    this.dataMapper = leaderboardStatsNetworkDataMapper;
    this.logger = logger;
  }

  @Override public void getGlobalLeaderboardStats(int offset,
      final CompletionCallback<LeaderboardStatsEntity> callback) {
    leaderboardApiService.getGlobalLeaderboardStats(offset,
        new Callback<LeaderboardStatsNetwork>() {
          @Override
          public void success(LeaderboardStatsNetwork leaderboardStatsNetwork, Response response) {
            if (callback != null) {
              LeaderboardStatsEntity transformed = dataMapper.transform(leaderboardStatsNetwork);
              callback.onSuccess(transformed);
            }
          }

          @Override public void failure(RetrofitError error) {
            if (callback != null) {
              logger.e(TAG, error.toString());
              callback.onError(errorAdapter.of(error));
            }
          }
        });
  }

  @Override public void getWeeklyLeaderboardStats(int offset,
      final CompletionCallback<LeaderboardStatsEntity> callback) {
    leaderboardApiService.getWeeklyLeaderboardStats(offset,
        new Callback<LeaderboardStatsNetwork>() {
          @Override
          public void success(LeaderboardStatsNetwork leaderboardStatsNetwork, Response response) {
            if (callback != null) {
              LeaderboardStatsEntity transformed = dataMapper.transform(leaderboardStatsNetwork);
              callback.onSuccess(transformed);
            }
          }

          @Override public void failure(RetrofitError error) {
            if (callback != null) {
              logger.e(TAG, error.toString());
              callback.onError(errorAdapter.of(error));
            }
          }
        });
  }

  @Override public void getMonthlyLeaderboardStats(int offset,
      final CompletionCallback<LeaderboardStatsEntity> callback) {
    leaderboardApiService.getMonthlyLeaderboardStats(offset,
        new Callback<LeaderboardStatsNetwork>() {
          @Override
          public void success(LeaderboardStatsNetwork leaderboardStatsNetwork, Response response) {
            if (callback != null) {
              LeaderboardStatsEntity transformed = dataMapper.transform(leaderboardStatsNetwork);
              callback.onSuccess(transformed);
            }
          }

          @Override public void failure(RetrofitError error) {
            if (callback != null) {
              logger.e(TAG, error.toString());
              callback.onError(errorAdapter.of(error));
            }
          }
        });

  }
}
