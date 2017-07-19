package com.worldreader.core.datasource.network.datasource.leaderboard;

import android.support.annotation.NonNull;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.datasource.model.LeaderboardStatsEntity;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import com.worldreader.core.datasource.network.mapper.LeaderboardStatsNetworkDataMapper;
import com.worldreader.core.datasource.network.model.LeaderboardStatsNetwork;
import javax.inject.Inject;
import retrofit2.Call;

public class LeaderboardNetworkDataSourceImpl implements LeaderboardNetworkDataSource {

  private static final String TAG = LeaderboardNetworkDataSource.class.getSimpleName();

  private final LeaderboardApiService leaderboardApiService;
  private final LeaderboardStatsNetworkDataMapper dataMapper;
  private final ErrorAdapter<Throwable> errorAdapter;
  private final Logger logger;

  @Inject public LeaderboardNetworkDataSourceImpl(LeaderboardApiService leaderboardApiService,
      LeaderboardStatsNetworkDataMapper leaderboardStatsNetworkDataMapper, ErrorAdapter<Throwable> errorAdapter, Logger logger) {
    this.leaderboardApiService = leaderboardApiService;
    this.dataMapper = leaderboardStatsNetworkDataMapper;
    this.errorAdapter = errorAdapter;
    this.logger = logger;
  }

  @Override public void getGlobalLeaderboardStats(int offset, final CompletionCallback<LeaderboardStatsEntity> callback) {
    leaderboardApiService.getGlobalLeaderboardStats(offset).enqueue(new retrofit2.Callback<LeaderboardStatsNetwork>() {
      @Override
      public void onResponse(@NonNull final Call<LeaderboardStatsNetwork> call, @NonNull final retrofit2.Response<LeaderboardStatsNetwork> response) {
        final boolean successful = response.isSuccessful();
        if (successful) {
          if (callback != null) {
            final LeaderboardStatsNetwork body = response.body();
            LeaderboardStatsEntity transformed = dataMapper.transform(body);
            callback.onSuccess(transformed);
          }
        } else {
          if (callback != null) {
            final Retrofit2Error error = Retrofit2Error.httpError(response);
            callback.onError(errorAdapter.of(error));
          }
        }
      }

      @Override public void onFailure(@NonNull final Call<LeaderboardStatsNetwork> call, @NonNull final Throwable t) {
        if (callback != null) {
          callback.onError(errorAdapter.of(t));
        }
      }
    });
  }

  @Override public void getWeeklyLeaderboardStats(int offset, final CompletionCallback<LeaderboardStatsEntity> callback) {
    leaderboardApiService.getWeeklyLeaderboardStats(offset).enqueue(new retrofit2.Callback<LeaderboardStatsNetwork>() {
      @Override
      public void onResponse(@NonNull final Call<LeaderboardStatsNetwork> call, @NonNull final retrofit2.Response<LeaderboardStatsNetwork> response) {
        final boolean successful = response.isSuccessful();
        if (successful) {
          if (callback != null) {
            final LeaderboardStatsNetwork body = response.body();
            LeaderboardStatsEntity transformed = dataMapper.transform(body);
            callback.onSuccess(transformed);
          }
        } else {
          if (callback != null) {
            final Retrofit2Error error = Retrofit2Error.httpError(response);
            callback.onError(errorAdapter.of(error));
          }
        }
      }

      @Override public void onFailure(@NonNull final Call<LeaderboardStatsNetwork> call, @NonNull final Throwable t) {
        if (callback != null) {
          callback.onError(errorAdapter.of(t));
        }
      }
    });
  }

  @Override public void getMonthlyLeaderboardStats(int offset, final CompletionCallback<LeaderboardStatsEntity> callback) {
    leaderboardApiService.getMonthlyLeaderboardStats(offset).enqueue(new retrofit2.Callback<LeaderboardStatsNetwork>() {
      @Override
      public void onResponse(@NonNull final Call<LeaderboardStatsNetwork> call, @NonNull final retrofit2.Response<LeaderboardStatsNetwork> response) {
        final boolean successful = response.isSuccessful();
        if (successful) {
          if (callback != null) {
            final LeaderboardStatsNetwork body = response.body();
            LeaderboardStatsEntity transformed = dataMapper.transform(body);
            callback.onSuccess(transformed);
          }
        } else {
          if (callback != null) {
            final Retrofit2Error error = Retrofit2Error.httpError(response);
            callback.onError(errorAdapter.of(error));
          }
        }
      }

      @Override public void onFailure(@NonNull final Call<LeaderboardStatsNetwork> call, @NonNull final Throwable t) {
        if (callback != null) {
          callback.onError(errorAdapter.of(t));
        }
      }
    });
  }
}
