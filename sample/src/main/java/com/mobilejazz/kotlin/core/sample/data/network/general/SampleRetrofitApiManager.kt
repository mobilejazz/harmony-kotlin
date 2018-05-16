package com.mobilejazz.kotlin.core.sample.data.network.general

import com.google.gson.Gson
import com.mobilejazz.kotlin.core.sample.data.network.items.ItemApiService
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.guava.GuavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor

class SampleRetrofitApiManager(
    private val endpoint: HttpUrl,
    private val okHttpClient: OkHttpClient,
    private val callbackExecutor: Executor,
    private val gson: Gson
) : RetrofitApiManager {

  private val retrofit: Retrofit by lazy {
    createRetrofit()
  }

  override fun <S> createService(serviceClass: Class<S>): S {
    return retrofit.create(serviceClass)
  }

  private fun createRetrofit(): Retrofit {
    return RetrofitApiManager.Builder(endpoint)
        .setOkHttpClient(okHttpClient)
        .setCallbackExecutor(callbackExecutor).addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(GuavaCallAdapterFactory.create())
        .create()
  }

  fun itemService(): ItemApiService = createService(ItemApiService::class.java)
}
