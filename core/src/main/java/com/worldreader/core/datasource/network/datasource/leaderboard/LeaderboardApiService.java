package com.worldreader.core.datasource.network.datasource.leaderboard;

import com.worldreader.core.datasource.network.model.LeaderboardStatsNetwork;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface LeaderboardApiService {

  @GET("/leaderboard") void getGlobalLeaderboardStats(@Query("page") int offset,
      Callback<LeaderboardStatsNetwork> callback);

  @GET("/leaderboard/weekly") void getWeeklyLeaderboardStats(@Query("page") int offset,
      Callback<LeaderboardStatsNetwork> callback);

  @GET("/leaderboard/monthly") void getMonthlyLeaderboardStats(@Query("page") int offset,
      Callback<LeaderboardStatsNetwork> callback);

}
