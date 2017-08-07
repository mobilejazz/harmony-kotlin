package com.worldreader.core.datasource.network.datasource.leaderboard;

import com.worldreader.core.datasource.network.model.LeaderboardStatsNetwork;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LeaderboardApiService {

  @GET("leaderboard") Call<LeaderboardStatsNetwork> getGlobalLeaderboardStats(@Query("page") int offset);

  @GET("leaderboard/weekly") Call<LeaderboardStatsNetwork> getWeeklyLeaderboardStats(@Query("page") int offset);

  @GET("leaderboard/monthly") Call<LeaderboardStatsNetwork> getMonthlyLeaderboardStats(@Query("page") int offset);

}
