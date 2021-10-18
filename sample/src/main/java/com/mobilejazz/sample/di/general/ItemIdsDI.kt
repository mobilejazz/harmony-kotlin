package com.mobilejazz.sample.di.general

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harmony.kotlin.android.data.datasource.DeviceStorageDataSource
import com.harmony.kotlin.android.data.datasource.DeviceStorageObjectAssemblerDataSource
import com.harmony.kotlin.data.datasource.VoidDeleteDataSource
import com.harmony.kotlin.data.datasource.VoidPutDataSource
import com.harmony.kotlin.data.mapper.ListModelToStringMapper
import com.harmony.kotlin.data.mapper.ModelToStringMapper
import com.harmony.kotlin.data.mapper.StringToListModelMapper
import com.harmony.kotlin.data.mapper.StringToModelMapper
import com.harmony.kotlin.data.repository.CacheRepository
import com.harmony.kotlin.data.repository.GetRepository
import com.harmony.kotlin.data.repository.RepositoryMapper
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

    val deviceStorageObjectAssemblerDataSource =
      DeviceStorageObjectAssemblerDataSource(toStringMapper, toModelMapper, toListModelMapper, toStringListMapper, deviceStorageDataSource)

    // Now, we need to get a network dataSource to fetch the item ids
    val itemIdsNetworkDataSource = ItemIdsNetworkDataSource(hackerNewsItemService)

    // We want to establish a simple caching system, so we are going to use the CacheRepository to automatically have this feature.
    val cacheRepository = CacheRepository(
      deviceStorageObjectAssemblerDataSource, deviceStorageObjectAssemblerDataSource,
      deviceStorageObjectAssemblerDataSource,
      itemIdsNetworkDataSource, VoidPutDataSource(), VoidDeleteDataSource()
    )

    // As our business model only understand the ItemIds model instead of the ItemIdsEntity model, we need to use the RepositoryMapper to map the different
    // types
    return RepositoryMapper(
      cacheRepository, cacheRepository, cacheRepository, ItemIdsEntityToItemIdsMapper(),
      ItemIdsToItemIdsEntityMapper()
    )
  }


//  @Provides
//  @Singleton
//  fun provideGetRepository(singleDataSourceRepository: SingleDataSourceRepository<Int>): GetRepository<Int> = singleDataSourceRepository
}
