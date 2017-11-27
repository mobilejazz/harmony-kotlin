package com.worldreader.core.datasource.network.general.retrofit.manager;

import com.google.gson.Gson;
import com.worldreader.core.datasource.network.datasource.geolocation.GeocodingService;
import com.worldreader.core.datasource.network.general.retrofit.AbstractRetrofitApiManager;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.*;

public class GeocodingApiManager extends AbstractRetrofitApiManager {

  private final HttpUrl endpoint;
  private final OkHttpClient okHttpClient;
  private final Executor callbackExecutor;
  private final Gson gson;

  public GeocodingApiManager(HttpUrl endpoint, OkHttpClient okHttpClient, Executor callbackExecutor, Gson gson) {
    this.okHttpClient = okHttpClient;
    this.endpoint = endpoint;
    this.callbackExecutor = callbackExecutor;
    this.gson = gson;
  }

  @Override protected Retrofit createRetrofit() {
    return new Builder(endpoint).setOkHttpClient(okHttpClient)
        .setCallbackExecutor(callbackExecutor)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .create();
  }

  public GeocodingService geocodingService() {
    return createService(GeocodingService.class);
  }
}
