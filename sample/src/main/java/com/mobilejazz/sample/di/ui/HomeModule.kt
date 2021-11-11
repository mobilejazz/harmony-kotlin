package com.mobilejazz.sample.di.ui

import com.harmony.kotlin.android.application.di.ActivityScope
import com.harmony.kotlin.data.datasource.flow.FlowGetDataSource
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.repository.GetRepository
import com.harmony.kotlin.data.repository.flow.FlowGetRepositoryMapper
import com.harmony.kotlin.data.repository.flow.SingleFlowGetDataSourceRepository
import com.harmony.kotlin.domain.interactor.FlowGetInteractor
import com.harmony.kotlin.domain.interactor.GetInteractor
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

    val flowDatasource = object : FlowGetDataSource<ItemIdsEntity> {
      override fun get(query: Query): Flow<ItemIdsEntity> = flow {
        emit(imperativeDatasource.get(query))
      }

      override fun getAll(query: Query): Flow<List<ItemIdsEntity>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
      }
    }
    val singleFlowGetDataSourceRepository = SingleFlowGetDataSourceRepository(flowDatasource)
    val repositoryMapper = FlowGetRepositoryMapper(singleFlowGetDataSourceRepository, ItemIdsEntityToItemIdsMapper())
    return FlowGetInteractor(scope, repositoryMapper)
  }
}
