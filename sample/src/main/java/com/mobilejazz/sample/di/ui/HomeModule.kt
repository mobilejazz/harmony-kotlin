package com.mobilejazz.sample.di.ui

import com.mobilejazz.harmony.kotlin.android.di.ActivityScope
import com.mobilejazz.harmony.kotlin.core.domain.interactor.FlowGetInteractor
import com.mobilejazz.harmony.kotlin.core.domain.interactor.GetInteractor
import com.mobilejazz.harmony.kotlin.core.domain.interactor.toFlowGetInteractor
import com.mobilejazz.harmony.kotlin.core.repository.GetRepository
import com.mobilejazz.harmony.kotlin.core.repository.flowdatasource.FlowGetDataSource
import com.mobilejazz.harmony.kotlin.core.repository.flowdatasource.toFlowGetRepository
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.sample.core.data.mapper.ItemIdsEntityToItemIdsMapper
import com.mobilejazz.sample.core.data.model.ItemIdsEntity
import com.mobilejazz.sample.core.data.network.HackerNewsItemService
import com.mobilejazz.sample.core.data.network.ItemIdsNetworkDataSource
import com.mobilejazz.sample.core.domain.model.ItemIds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Module
class HomeModule {

  @Provides
  @ActivityScope
  fun provideGetAllStories(scope: CoroutineScope, getRepository: GetRepository<ItemIds>): GetInteractor<ItemIds> {
    return GetInteractor(scope, getRepository)
  }

  @Provides
  @ActivityScope
  fun provideFlowGetAllStories(scope: CoroutineScope, hackerNewsItemService: HackerNewsItemService): FlowGetInteractor<ItemIds> {

    val imperativeDatasource = ItemIdsNetworkDataSource(hackerNewsItemService)

    val flowDatasource = object: FlowGetDataSource<ItemIdsEntity> {
      override fun get(query: Query): Flow<ItemIdsEntity> = flow {
        emit(imperativeDatasource.get(query))
      }

      override fun getAll(query: Query): Flow<List<ItemIdsEntity>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
      }

    }

    return flowDatasource.toFlowGetRepository(ItemIdsEntityToItemIdsMapper()).toFlowGetInteractor()
  }
}