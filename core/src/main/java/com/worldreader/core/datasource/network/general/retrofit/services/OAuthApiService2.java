package com.worldreader.core.datasource.network.general.retrofit.services;

import com.worldreader.core.datasource.network.model.OAuthNetworkBody;
import com.worldreader.core.datasource.network.model.OAuthNetworkResponseEntity;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OAuthApiService2 {

  @POST("oauth2/token") Call<OAuthNetworkResponseEntity> token(@Body OAuthNetworkBody body);

}
