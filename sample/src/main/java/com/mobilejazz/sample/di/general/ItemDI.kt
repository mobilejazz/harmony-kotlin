package com.mobilejazz.sample.di.general

import android.content.SharedPreferences
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.mobilejazz.harmony.kotlin.android.repository.datasource.srpreferences.DeviceStorageDataSource
import com.mobilejazz.harmony.kotlin.android.repository.datasource.srpreferences.DeviceStorageObjectAssemblerDataSource
import com.mobilejazz.harmony.kotlin.core.repository.*
import com.mobilejazz.harmony.kotlin.core.repository.datasource.VoidDeleteDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.VoidPutDataSource
import com.mobilejazz.harmony.kotlin.core.repository.mapper.*
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.ValidationServiceManager
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.timestamp.TimestampValidationStrategy
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.toVastraValidator
import com.mobilejazz.sample.core.data.mapper.ItemEntityToItemMapper
import com.mobilejazz.sample.core.data.model.ItemEntity
import com.mobilejazz.sample.core.data.network.ItemNetworkDataSource
import com.mobilejazz.sample.core.domain.model.Item
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class ItemDI {

  @Provides
  @Singleton
  fun provideSharedPreferencesStorage(sharedPreferences: SharedPreferences): DeviceStorageObjectAssemblerDataSource<ItemEntity> {
    val deviceStorageDataSource = DeviceStorageDataSource<String>(sharedPreferences)

    val gson = Gson()
    val toStringMapper = ModelToStringMapper<ItemEntity>(gson)
    val toModelMapper = StringToModelMapper(ItemEntity::class.java, gson)
    val toListModelMapper = ListModelToStringMapper<ItemEntity>(gson)
    val toStringListMapper = StringToListModelMapper(object : TypeToken<List<ItemEntity>>() {}, gson)
    return DeviceStorageObjectAssemblerDataSource(toStringMapper, toModelMapper, toListModelMapper, toStringListMapper, deviceStorageDataSource)
  }

  // Repositories
  // --> Main
  @Provides
  @Singleton
  fun provideCacheRepository(cacheDataSource: DeviceStorageObjectAssemblerDataSource<ItemEntity>, mainDataSource: ItemNetworkDataSource): CacheRepository<ItemEntity> {
    return CacheRepository(
        cacheDataSource, cacheDataSource, cacheDataSource,
        mainDataSource, VoidPutDataSource(), VoidDeleteDataSource(),
        ValidationServiceManager(arrayListOf(TimestampValidationStrategy())).toVastraValidator())
  }

  @Provides
  @Singleton
  fun provideGetEntityRepository(cacheRepository: CacheRepository<ItemEntity>): GetRepository<ItemEntity> = cacheRepository

  @Provides
  @Singleton
  fun providePutEntityRepository(cacheRepository: CacheRepository<ItemEntity>): PutRepository<ItemEntity> = cacheRepository

  @Provides
  @Singleton
  @Named("delete-item-entity-repository-main")
  fun provideDeleteEntityRepository(cacheRepository: CacheRepository<ItemEntity>): DeleteRepository = cacheRepository

  // Repositories
  // ---> Mappers

  @Provides
  @Singleton
  fun provideRepositoryMapper(getRepository: GetRepository<ItemEntity>,
                              putRepository: PutRepository<ItemEntity>,
                              @Named("delete-item-entity-repository-main") deleteRepository: DeleteRepository): RepositoryMapper<ItemEntity, Item> {
    return RepositoryMapper(getRepository, putRepository, deleteRepository, ItemEntityToItemMapper(), VoidMapper())
  }

  // Repositories
  // --> Public to the domain model
  @Provides
  @Singleton
  fun provideGetRepository(repositoryMapper: RepositoryMapper<ItemEntity, Item>): GetRepository<Item> = repositoryMapper

  @Provides
  @Singleton
  fun providPutRepository(repositoryMapper: RepositoryMapper<ItemEntity, Item>): PutRepository<Item> = repositoryMapper

  @Provides
  @Singleton
  @Named("delete-item-repository-main")
  fun provideDeleteRepository(repositoryMapper: RepositoryMapper<ItemEntity, Item>): DeleteRepository = repositoryMapper

}