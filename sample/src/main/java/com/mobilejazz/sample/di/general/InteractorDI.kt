package com.mobilejazz.sample.di.general

import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.repository.GetRepository
import com.harmony.kotlin.domain.interactor.FlowGetInteractor
import com.harmony.kotlin.domain.interactor.GetInteractor
import com.harmony.kotlin.data.datasource.flow.FlowGetDataSource
import com.harmony.kotlin.data.datasource.flow.toFlowGetRepository
import com.harmony.kotlin.domain.interactor.toFlowGetInteractor
import com.mobilejazz.sample.core.domain.interactor.GetItemsByIdInteractor
import com.mobilejazz.sample.core.domain.model.Item
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

  @Provides
  @Singleton
  fun providerGetItemInteractor(scope: CoroutineScope, repository: GetRepository<Item>): GetInteractor<Item> {
    return GetInteractor(scope, repository)
  }

  @Provides
  @Singleton
  fun providesTestFlowGetInteractor(): FlowGetInteractor<Int> {

    val datasource = object : FlowGetDataSource<Int> {
      override fun get(query: Query): Flow<Int> = flow {
        (1..5).forEach {
          emit(it)
        }
      }

      override fun getAll(query: Query): Flow<List<Int>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
      }

    }

    return datasource.toFlowGetRepository().toFlowGetInteractor()

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