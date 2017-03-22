package com.worldreader.core.datasource.network.general.retrofit.manager;

import com.google.gson.Gson;
import com.worldreader.core.datasource.network.datasource.dictionary.DictionaryApiService2;
import com.worldreader.core.datasource.network.general.retrofit.AbstractRetrofitApiManager;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.Executor;

public class DictionaryApiManager extends AbstractRetrofitApiManager {

  private final HttpUrl endpoint;
  private final OkHttpClient okHttpClient;
  private final Executor callbackExecutor;
  private final Gson gson;

  public DictionaryApiManager(final HttpUrl endpoint, final OkHttpClient okHttpClient, final Executor callbackExecutor, final Gson gson) {
    this.endpoint = endpoint;
    this.okHttpClient = okHttpClient;
    this.callbackExecutor = callbackExecutor;
    this.gson = gson;
  }

  @Override protected Retrofit createRetrofit() {
    return new Builder(endpoint).setOkHttpClient(okHttpClient)
        .setCallbackExecutor(callbackExecutor)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .create();
  }

  public DictionaryApiService2 dictionaryApiService() {
    return createService(DictionaryApiService2.class);
  }

}
