package com.mobilejazz.sample.di.general

import com.mobilejazz.harmony.kotlin.core.domain.interactor.GetInteractor
import com.mobilejazz.sample.core.domain.interactor.GetItemsByIdInteractor
import com.mobilejazz.sample.core.domain.model.Item
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


@Module(subcomponents = [(InteractorsComponent::class)])
class InteractorsModule {

  @Provides
  @Singleton
  fun provideAppCoroutineScope(): CoroutineScope = CoroutineScope(Dispatchers.Default)


  // Global Interactors
  @Provides
  @Singleton
  fun provideGetItemsByIdInteractor(scope: CoroutineScope, getItemInteractor: GetInteractor<Item>): GetItemsByIdInteractor {
    return GetItemsByIdInteractor(scope, getItemInteractor)
  }


}

@Subcomponent
interface InteractorsComponent {

  fun appScope(): CoroutineScope

  @Subcomponent.Builder
  interface Builder {
    fun build(): InteractorsComponent
  }

}