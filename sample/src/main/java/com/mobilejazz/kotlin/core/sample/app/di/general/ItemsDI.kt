package com.mobilejazz.kotlin.core.sample.app.di.general

import com.mobilejazz.kotlin.core.domain.interactor.GetAllInteractor
import com.mobilejazz.kotlin.core.repository.GetRepository
import com.mobilejazz.kotlin.core.repository.NetworkStorageRepository
import com.mobilejazz.kotlin.core.repository.RepositoryMapper
import com.mobilejazz.kotlin.core.sample.app.di.ActivityScope
import com.mobilejazz.kotlin.core.sample.data.VoidDeleteDataSource
import com.mobilejazz.kotlin.core.sample.data.VoidGetDataSource
import com.mobilejazz.kotlin.core.sample.data.VoidPutDataSource
import com.mobilejazz.kotlin.core.sample.data.network.items.GetItemNetworkDataSource
import com.mobilejazz.kotlin.core.sample.data.network.items.ItemApiService
import com.mobilejazz.kotlin.core.sample.domain.model.Item
import com.mobilejazz.kotlin.core.sample.repository.entity.ItemEntity
import com.mobilejazz.kotlin.core.threading.AppExecutor
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import org.worldreader.classroom.dataprovider.network.error.RetrofitErrorAdapter
import javax.inject.Singleton

@Module(subcomponents = [(ItemsComponent::class)])

class ItemsModule {
  @Provides
  @Singleton
  fun provideGetItemsInteractor(executor: AppExecutor, getRepository: GetRepository<Item>): GetAllInteractor<Item> {
    return GetAllInteractor(executor, getRepository)
  }

  @Provides
  @Singleton
  fun provideGetRepository(itemApiService: ItemApiService, errorAdapter: RetrofitErrorAdapter): GetRepository<Item> {
    return NetworkStorageRepository(
        VoidGetDataSource(),
        VoidPutDataSource(),
        VoidDeleteDataSource(),
        GetItemNetworkDataSource(itemApiService, errorAdapter),
        VoidPutDataSource(),
        VoidDeleteDataSource())
  }
  

}

@Subcomponent
interface ItemsComponent {

  fun getRepository(): GetRepository<Item>

  @Subcomponent.Builder
  interface Builder {
    fun requestModule(module: ItemsModule): Builder
    fun build(): ItemsComponent
  }

}