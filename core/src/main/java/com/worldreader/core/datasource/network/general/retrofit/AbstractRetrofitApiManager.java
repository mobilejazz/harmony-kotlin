package com.worldreader.core.datasource.network.general.retrofit;

import retrofit2.Retrofit;

public abstract class AbstractRetrofitApiManager implements RetrofitApiManager2 {

  private Retrofit retrofit;

  @Override public <S> S createService(Class<S> serviceClass) {
    if (retrofit == null) {
      retrofit = createRetrofit();
    }
    return retrofit.create(serviceClass);
  }

  protected abstract Retrofit createRetrofit();

}
