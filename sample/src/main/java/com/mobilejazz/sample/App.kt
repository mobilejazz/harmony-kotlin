package com.mobilejazz.sample

import android.util.Log
import com.mobilejazz.sample.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import sample.Platform

class App : DaggerApplication() {

  override fun onCreate() {
    super.onCreate()

      Log.d("TEST", Platform.name)
  }

  override fun applicationInjector(): AndroidInjector<out DaggerApplication> = DaggerAppComponent.builder().create(this)

}