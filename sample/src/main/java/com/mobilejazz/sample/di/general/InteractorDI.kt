package com.mobilejazz.sample.di.general

import com.mobilejazz.harmony.kotlin.core.interactor.GetInteractor
import com.mobilejazz.harmony.kotlin.core.threading.AppExecutor
import com.mobilejazz.harmony.kotlin.core.threading.Executor
import com.mobilejazz.sample.core.domain.interactor.GetItemsByIdInteractor
import com.mobilejazz.sample.core.domain.model.Item
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Singleton


@Module(subcomponents = [(InteractorsComponent::class)])
class InteractorsModule {

  @Provides
  @Singleton
  fun provideAppListeningExecutorService(): Executor = AppExecutor

  // Global Interactors
  @Provides
  @Singleton
  fun provideGetItemsByIdInteractor(executor: Executor, getItemInteractor: GetInteractor<Item>): GetItemsByIdInteractor {
    return GetItemsByIdInteractor(executor, getItemInteractor)
  }


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