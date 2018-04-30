package org.worldreader.classroom.app.di.ui

import com.mobilejazz.kotlin.core.sample.app.di.ActivityScope
import com.mobilejazz.kotlin.core.sample.app.ui.items.ItemsActivity
import com.mobilejazz.kotlin.core.sample.app.ui.items.ItemsModule
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBindingsModule {

  @ActivityScope
  @ContributesAndroidInjector(modules = [(ItemsModule::class)])
  abstract fun provisionSplashActivityInjector(): ItemsActivity

}