package com.mobilejazz.sample

import com.mobilejazz.sample.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class App : DaggerApplication() {

  override fun onCreate() {
    super.onCreate()
  }

  override fun applicationInjector(): AndroidInjector<out DaggerApplication> = DaggerAppComponent.builder().create(this)

}