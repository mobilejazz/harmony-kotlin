package com.mobilejazz.kotlin.core.sample.app.di.general

import com.mobilejazz.kotlin.core.threading.AppExecutor
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Singleton

@Module(subcomponents = [(InteractorsComponent::class)])
class InteractorsModule {

  @Provides
  @Singleton
  fun provideAppListeningExecutorService() = AppExecutor

}

@Subcomponent
interface InteractorsComponent {

  fun appExecutor(): AppExecutor

  @Subcomponent.Builder
  interface Builder {
    fun requestModule(module: InteractorsModule): Builder
    fun build(): InteractorsComponent
  }

}