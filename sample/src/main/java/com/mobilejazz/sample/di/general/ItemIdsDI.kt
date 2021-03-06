package com.mobilejazz.sample.di.general

import android.content.SharedPreferences
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.mobilejazz.harmony.kotlin.android.repository.datasource.srpreferences.DeviceStorageDataSource
import com.mobilejazz.harmony.kotlin.core.repository.CacheRepository
import com.mobilejazz.harmony.kotlin.core.repository.GetRepository
import com.mobilejazz.harmony.kotlin.core.repository.RepositoryMapper
import com.mobilejazz.harmony.kotlin.core.repository.datasource.SerializationDataSourceMapper
import com.mobilejazz.harmony.kotlin.core.repository.datasource.VoidDeleteDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.VoidPutDataSource
import com.mobilejazz.harmony.kotlin.core.repository.mapper.ListModelToStringMapper
import com.mobilejazz.harmony.kotlin.core.repository.mapper.ModelToStringMapper
import com.mobilejazz.harmony.kotlin.core.repository.mapper.StringToListModelMapper
import com.mobilejazz.harmony.kotlin.core.repository.mapper.StringToModelMapper
import com.mobilejazz.sample.core.data.mapper.ItemIdsEntityToItemIdsMapper
import com.mobilejazz.sample.core.data.mapper.ItemIdsToItemIdsEntityMapper
import com.mobilejazz.sample.core.data.model.ItemIdsEntity
import com.mobilejazz.sample.core.data.network.HackerNewsItemService
import com.mobilejazz.sample.core.data.network.ItemIdsNetworkDataSource
import com.mobilejazz.sample.core.domain.model.ItemIds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ItemIdsDI {

  @Provides
  @Singleton
  fun provideGetRepository(sharedPreferences: SharedPreferences, hackerNewsItemService: HackerNewsItemService): GetRepository<ItemIds> {
    // We need to create the DeviceStorageDataSource to store all data in the Android SharedPreferences
    val deviceStorageDataSource = DeviceStorageDataSource<String>(sharedPreferences)

    // As DeviceStorageDataSource doesn't support store model objects, we need to use the DeviceStorageObjectAssemblerDataSource to be able to serialize the
    // object into a json string
    val gson = Gson()
    val toStringMapper = ModelToStringMapper<ItemIdsEntity>(gson)
    val toModelMapper = StringToModelMapper(ItemIdsEntity::class.java, gson)
    val toListModelMapper = ListModelToStringMapper<ItemIdsEntity>(gson)
    val toStringListMapper = StringToListModelMapper(object : TypeToken<List<ItemIdsEntity>>() {}, gson)

    val deviceStorageObjectAssemblerDataSource = SerializationDataSourceMapper(
        deviceStorageDataSource,
        deviceStorageDataSource,
        deviceStorageDataSource,
        toModelMapper,
        toStringListMapper,
        toStringMapper,
        toListModelMapper)

    // Now, we need to get a network dataSource to fetch the item ids
    val itemIdsNetworkDataSource = ItemIdsNetworkDataSource(hackerNewsItemService)

    // We want to establish a simple caching system, so we are going to use the CacheRepository to automatically have this feature.
    val cacheRepository = CacheRepository(deviceStorageObjectAssemblerDataSource, deviceStorageObjectAssemblerDataSource,
        deviceStorageObjectAssemblerDataSource,
        itemIdsNetworkDataSource, VoidPutDataSource(), VoidDeleteDataSource())

    // As our business model only understand the ItemIds model instead of the ItemIdsEntity model, we need to use the RepositoryMapper to map the different
    // types
    return RepositoryMapper(cacheRepository, cacheRepository, cacheRepository, ItemIdsEntityToItemIdsMapper(),
        ItemIdsToItemIdsEntityMapper())
  }


//  @Provides
//  @Singleton
//  fun provideGetRepository(singleDataSourceRepository: SingleDataSourceRepository<Int>): GetRepository<Int> = singleDataSourceRepository
}