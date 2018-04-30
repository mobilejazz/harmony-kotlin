package com.mobilejazz.kotlin.core.sample.app.ui

import com.mobilejazz.kotlin.core.sample.app.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication


class App : DaggerApplication() {
  override fun applicationInjector(): AndroidInjector<out DaggerApplication> = DaggerAppComponent.builder().create(this@App)
}