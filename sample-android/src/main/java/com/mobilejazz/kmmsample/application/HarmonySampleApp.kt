package com.mobilejazz.kmmsample.application

import android.app.Application
import android.content.Context
import com.harmony.kotlin.BuildConfig
import com.harmony.kotlin.common.logger.AndroidLogger
import com.harmony.kotlin.data.datasource.cache.CacheSQLAndroidDefaultConfiguration
import com.mobilejazz.kmmsample.core.ApplicationComponent
import com.mobilejazz.kmmsample.core.ApplicationDefaultModule

class HarmonySampleApp : Application() {

  companion object {
    private lateinit var appContext: Context

    val appProvider: ApplicationComponent by lazy {
      ApplicationDefaultModule(AndroidLogger(BuildConfig.DEBUG), CacheSQLAndroidDefaultConfiguration(appContext))
    }
  }

  override fun onCreate() {
    super.onCreate()
    appContext = this
  }
}
