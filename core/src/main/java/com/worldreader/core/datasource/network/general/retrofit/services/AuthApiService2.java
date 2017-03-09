package com.worldreader.core.datasource.network.general.retrofit.services;

import com.worldreader.core.datasource.network.model.OAuthFacebookBody;
import com.worldreader.core.datasource.network.model.OAuthGoogleBody;
import com.worldreader.core.datasource.network.model.OAuthNetworkResponseEntity;
import com.worldreader.core.datasource.network.model.UserFacebookRegisterBody;
import com.worldreader.core.datasource.network.model.UserGoogleRegisterBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService2 {

  @POST("facebook") Call<Void> registerWithFacebook(@Body UserFacebookRegisterBody body);

  @POST("facebook/login") Call<OAuthNetworkResponseEntity> userTokenWithFacebook(
      @Body OAuthFacebookBody body);

  @POST("google") Call<Void> registerWithGoogle(@Body UserGoogleRegisterBody body);

  @POST("google/login") Call<OAuthNetworkResponseEntity> userTokenWithGoogle(
      @Body OAuthGoogleBody body);

}
