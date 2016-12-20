package com.worldreader.core.datasource.network.datasource.rating;

import com.worldreader.core.datasource.model.ScoreEntity;
import okhttp3.Response;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

public interface RatingApiService {

  @POST("/ratings/{id}") void rate(@Path("id") String id, @Body() ScoreEntity score,
      Callback<Response> ignored);

}
