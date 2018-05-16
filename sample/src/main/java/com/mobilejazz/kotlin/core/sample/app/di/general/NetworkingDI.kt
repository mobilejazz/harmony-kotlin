package com.mobilejazz.kotlin.core.sample.app.di.general

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mobilejazz.kotlin.core.sample.app.ui.App
import com.mobilejazz.kotlin.core.sample.data.network.general.RetrofitApiManager
import com.mobilejazz.kotlin.core.sample.data.network.general.SampleRetrofitApiManager
import com.mobilejazz.kotlin.core.sample.data.network.items.ItemApiService
import com.mobilejazz.logger.library.Logger
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.worldreader.classroom.dataprovider.network.error.RetrofitErrorAdapter
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(
    subcomponents = [(NetworkingComponent::class)],
    includes = [(NetworkConverterModule::class), (ApiServicesModule::class)]
)
class NetworkingModule {

  @Provides
  @Singleton
  fun provideOkHttpCache(context: App): Cache {
    val path = context.cacheDir.absolutePath
    val name = "HttpCache"
    val size = 100 * 1024 * 1024L // 100MB
    return Cache(File(path, name), size)
  }

  @Provides
  @Singleton
  fun provideOkHttpClient(
      cache: Cache,
      logger: Logger
  ): OkHttpClient {

    return OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .connectionPool(ConnectionPool())
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // TODO Add debug interceptors

//      return EnhancedOkHttpBuilder().cache(cache)
//      .retryOnConnectionFailure(true)
//      .connectionPool(ConnectionPool())
//      .addInterceptor(RelativeUriPathFixerInterceptor(BuildConfig.CLASSROOM_API_ENDPOINT))
//      .addInterceptor(ImageCacheInterceptor(simpleImageDownloader))
//      .addInterceptor(countryInterceptor)
//      .addInterceptor(ClassroomBooksServerInterceptor(BuildConfig.CLASSROOM_API_ENPOINT_TOKEN, BuildConfig.CLASSROOOM_API_CLIENT))
//      .logging(BuildConfig.ENABLE_HTTP_LOGS, EnhancedOkHttpBuilder.LogLevel.valueOf(BuildConfig.HTTP_LOGS_LEVEL), HttpLoggingInterceptors.logger(logger))
//      .connectTimeout(30, TimeUnit.SECONDS)
//      .writeTimeout(30, TimeUnit.SECONDS)
//      .readTimeout(30, TimeUnit.SECONDS)
//      .apply {
//        if (BuildConfig.DEBUG) {
//          addNetworkInterceptor(StethoInterceptor())
//        }
//      }.build()

  }

  @Provides
  @Singleton
  fun provideApiEndpoint(): HttpUrl =
      HttpUrl.parse("http://demo5266963.mockable.io/api/") ?: throw IllegalArgumentException("Invalid url constructed! Revise build.gradle file!")

  @Provides
  @Singleton
  fun provideClassroomRetrofitApiManager(
      endpoint: HttpUrl,
      okHttpClient: OkHttpClient,
      gson: Gson
  ): SampleRetrofitApiManager =
      SampleRetrofitApiManager(endpoint, okHttpClient, RetrofitApiManager.Executors.sameThreadExecutor(), gson)

  @Provides
  @Singleton
  fun provideErrorAdapter(): RetrofitErrorAdapter {
    return RetrofitErrorAdapter()
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
class ApiServicesModule {
  @Provides
  @Singleton
  fun provideBookApiService(manager: SampleRetrofitApiManager): ItemApiService = manager.itemService()
}

@Subcomponent
interface NetworkingComponent {

  @Subcomponent.Builder
  interface Builder {
    fun build(): NetworkingComponent
  }
}