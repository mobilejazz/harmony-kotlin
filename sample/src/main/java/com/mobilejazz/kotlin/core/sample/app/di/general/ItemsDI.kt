package com.mobilejazz.kotlin.core.sample.app.di.general

import com.mobilejazz.kotlin.core.domain.interactor.GetAllInteractor
import com.mobilejazz.kotlin.core.repository.GetRepository
import com.mobilejazz.kotlin.core.repository.NetworkStorageRepository
import com.mobilejazz.kotlin.core.repository.RepositoryMapper
import com.mobilejazz.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.kotlin.core.repository.datasource.VoidDataSource
import com.mobilejazz.kotlin.core.repository.datasource.memory.InMemoryDataSource
import com.mobilejazz.kotlin.core.sample.data.network.items.GetItemNetworkDataSource
import com.mobilejazz.kotlin.core.sample.data.network.items.ItemApiService
import com.mobilejazz.kotlin.core.sample.domain.model.Item
import com.mobilejazz.kotlin.core.sample.repository.entity.ItemEntity
import com.mobilejazz.kotlin.core.sample.repository.mapper.ItemEntityToItemMapper
import com.mobilejazz.kotlin.core.sample.repository.mapper.ItemToItemEntityMapper
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
    /* NetworkDataSource*/
    val getNetworkDataSource = GetItemNetworkDataSource(itemApiService, errorAdapter)

    /* StorageDataSource */
    val itemStorageDataSource = InMemoryDataSource<ItemEntity>()
    val getStorageDataSource: GetDataSource<ItemEntity> = itemStorageDataSource
    val putStorageDataSource: PutDataSource<ItemEntity> = itemStorageDataSource
    val deleteStorageDataSource: DeleteDataSource = itemStorageDataSource

    /* VoidDataSource */
    val voidDataSource = VoidDataSource<ItemEntity>()

    /* Repository */
    val itemNetworkStorageRepository = NetworkStorageRepository(
        getStorageDataSource, putStorageDataSource, deleteStorageDataSource,
        getNetworkDataSource, voidDataSource, voidDataSource
    )

    /* Mapper */
    val toItemEntityMapper = ItemToItemEntityMapper()
    val toItemMapper = ItemEntityToItemMapper()

    return RepositoryMapper(
        itemNetworkStorageRepository, itemNetworkStorageRepository, itemNetworkStorageRepository, toItemEntityMapper, toItemMapper)
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