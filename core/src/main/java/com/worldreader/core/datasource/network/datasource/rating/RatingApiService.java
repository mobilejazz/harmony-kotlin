package com.worldreader.core.datasource.network.datasource.rating;

import com.worldreader.core.datasource.model.ScoreEntity;
import com.worldreader.core.datasource.network.general.retrofit.annotations.JSON;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RatingApiService {

  @POST("ratings/{id}") @JSON Call<Void> rate(@Path("id") String id, @Body() ScoreEntity score);

}
