package com.mobilejazz.kotlin.core.sample.app.di.general

import com.mobilejazz.kotlin.core.sample.app.ui.App
import com.mobilejazz.kotlin.core.sample.helper.logger.AndroidLogger
import com.mobilejazz.logger.library.Logger
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Singleton


@Module(subcomponents = [(LoggerComponent::class)])
class LoggerModule {

  @Provides
  @Singleton
  fun provideLogger(context: App): Logger = AndroidLogger()
}

@Subcomponent
interface LoggerComponent {

  fun logger(): Logger

  @Subcomponent.Builder
  interface Builder {
    fun build(): LoggerComponent
  }

}