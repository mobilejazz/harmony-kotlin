package com.worldreader.core.datasource.network.general.retrofit.services;

import com.worldreader.core.datasource.network.model.LeaderboardStatNetwork;
import com.worldreader.core.datasource.network.model.MilestonesNetworkBody;
import com.worldreader.core.datasource.network.model.ResetPasswordNetworkBody;
import com.worldreader.core.datasource.network.model.ResetPasswordResponse;
import com.worldreader.core.datasource.network.model.UpdateReadingStatsNetworkBody;
import com.worldreader.core.datasource.network.model.UpdateUserFavoriteCategoriesNetworkBody;
import com.worldreader.core.datasource.network.model.UserBirthdDateNetworkBody;
import com.worldreader.core.datasource.network.model.UserEmailNetworkBody;
import com.worldreader.core.datasource.network.model.UserGoalsBody;
import com.worldreader.core.datasource.network.model.UserNameNetworkBody;
import com.worldreader.core.datasource.network.model.UserNetworkResponse;
import com.worldreader.core.datasource.network.model.UserPictureNetworkBody;
import com.worldreader.core.datasource.network.model.UserPointsNetworkBody;
import com.worldreader.core.datasource.network.model.UserPointsNetworkResponse;
import com.worldreader.core.datasource.network.model.UserReadingStatNetworkBody;
import com.worldreader.core.datasource.network.model.UserReadingStatsNetworkResponse;
import com.worldreader.core.datasource.network.model.UserRegisterBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApiService2 {

  @GET("user") Call<UserNetworkResponse> user();

  @POST("user/register") Call<UserNetworkResponse> register(@Body UserRegisterBody body);

  @POST("me/reset-password") Call<ResetPasswordResponse> resetPassword(
      @Body ResetPasswordNetworkBody body);

  @POST("user/goals") Call<UserNetworkResponse> updateGoals(@Body UserGoalsBody body);

  // "user/leaderboard/{period}"
  @GET("me/leaderboard/{period}") Call<LeaderboardStatNetwork> leaderboards(
      @Path("period") String period);

  @POST("me/statistics") Call<UserReadingStatsNetworkResponse> readingStats(
      @Body UserReadingStatNetworkBody body);

  // "user/statistics-update"
  @POST("me/statistics-update") Call<Void> updateReadingStats(
      @Body UpdateReadingStatsNetworkBody body);

  @POST("user/favorite_categories") Call<UserNetworkResponse> updateFavoriteCategories(
      @Body UpdateUserFavoriteCategoriesNetworkBody body);

  @POST("me/add-points") Call<UserPointsNetworkResponse> updatePoints(
      @Body UserPointsNetworkBody body);

  @POST("user/picture") Call<Void> updateUserPicture(@Body UserPictureNetworkBody body);

  @POST("user/birthdate") Call<Void> updateBirthdate(@Body UserBirthdDateNetworkBody body);

  @POST("user/email") Call<Void> updateEmail(@Body UserEmailNetworkBody body);

  @POST("user/name") Call<Void> updateName(@Body UserNameNetworkBody body);

  @POST("user/milestone") Call<UserNetworkResponse> updateMilestones(
      @Body MilestonesNetworkBody body);
}
