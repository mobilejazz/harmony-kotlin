package com.mobilejazz.sample.di.general

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mobilejazz.sample.core.data.network.HackerNewsItemService
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.guava.GuavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(
    subcomponents = [(NetworkingComponent::class)],
    includes = [(NetworkConverterModule::class), (NetworkRetrofitApiServices::class)]
)
class NetworkingModule {

  @Provides
  @Singleton
  fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://hacker-news.firebaseio.com/v0/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(GuavaCallAdapterFactory.create())
        .client(okHttpClient)
        .build()
  }

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
    val clientBuilder = OkHttpClient.Builder()

    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
    clientBuilder.addInterceptor(httpLoggingInterceptor)

    val dispatcher = Dispatcher()
    dispatcher.maxRequests = 5
    clientBuilder.dispatcher(dispatcher)

    return clientBuilder.build()
  }
}

@Module
class NetworkConverterModule {

  @Provides
  @Singleton
  fun provideGson(): Gson = GsonBuilder()
      .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
      .setPrettyPrinting()
      .create()
}

@Module
class NetworkRetrofitApiServices {

  @Provides
  @Singleton
  fun provideHackerNewsItemService(retrofit: Retrofit): HackerNewsItemService = retrofit.create(HackerNewsItemService::class.java)
}


@Subcomponent
interface NetworkingComponent {

  @Subcomponent.Builder
  interface Builder {
    fun build(): NetworkingComponent
  }
}