package com.mobilejazz.sample.di.general

import android.content.Context
import android.content.SharedPreferences
import com.mobilejazz.harmony.kotlin.android.logger.AndroidLogger
import com.mobilejazz.harmony.kotlin.core.logger.ConsoleLogger
import com.mobilejazz.harmony.kotlin.core.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Named
import javax.inject.Singleton

@Module(subcomponents = [(AndroidComponent::class)])
class AndroidModule {

  @Provides
  @Singleton
  fun provideSharedPreferences(c: Context): SharedPreferences = c.getSharedPreferences("items-preferences", Context.MODE_PRIVATE)

  @Provides
  @Singleton
  @Named("ConsoleLogger")
  fun provideConsoleLogger(): Logger = ConsoleLogger()

  @Provides
  @Singleton
  @Named("AndroidLogger")
  fun provideAndroidLogger(): Logger = AndroidLogger()
}

@Subcomponent
interface AndroidComponent {

  fun sharedPreferences(): SharedPreferences

  @Named("ConsoleLogger")
  fun consoleLogger(): Logger

  @Named("AndroidLogger")
  fun androidLogger(): Logger

  @Subcomponent.Builder
  interface Builder {
    fun build(): AndroidComponent
  }

}