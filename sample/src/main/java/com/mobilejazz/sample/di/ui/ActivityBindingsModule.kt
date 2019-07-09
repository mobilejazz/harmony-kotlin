package com.mobilejazz.sample.di.ui

import com.mobilejazz.harmony.kotlin.android.di.ActivityScope
import com.mobilejazz.sample.screens.detail.ItemDetailActivity
import com.mobilejazz.sample.screens.home.HomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingsModule {

  @ActivityScope
  @ContributesAndroidInjector(modules = [(HomeModule::class)])
  abstract fun provisioningHomeActivityInjector(): HomeActivity

  @ActivityScope
  @ContributesAndroidInjector(modules = [(ItemDetailModule::class)])
  abstract fun provisioningItemDetailActivityInjector(): ItemDetailActivity
}