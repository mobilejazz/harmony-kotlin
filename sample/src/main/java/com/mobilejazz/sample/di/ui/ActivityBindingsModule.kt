package com.mobilejazz.sample.di.ui

import com.mobilejazz.sample.screens.detail.ItemDetailActivity
import com.mobilejazz.sample.screens.home.HomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingsModule {

  @ContributesAndroidInjector(modules = [(HomeModule::class)])
  abstract fun provisioningHomeActivityInjector(): HomeActivity

  @ContributesAndroidInjector(modules = [(ItemDetailModule::class)])
  abstract fun provisioningItemDetailActivityInjector(): ItemDetailActivity
}
