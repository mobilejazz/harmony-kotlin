package com.mobilejazz.kotlin.core.sample.app.ui.items

import android.app.Activity
import com.mobilejazz.kotlin.core.sample.app.di.ActivityScope
import dagger.Module
import dagger.Provides

@Module
abstract class ItemsModule {

  @Module
  companion object {
    @Provides
    @ActivityScope
    @JvmStatic
    fun provideActivity(activity: ItemsActivity): Activity {
      return activity
    }
  }

}