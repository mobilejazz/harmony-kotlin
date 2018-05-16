package com.mobilejazz.kotlin.core

import android.util.Log
import com.mobilejazz.kotlin.core.domain.interactor.GetAllInteractor
import com.mobilejazz.kotlin.core.repository.*
import com.mobilejazz.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.kotlin.core.repository.datasource.memory.InMemoryDataSource
import com.mobilejazz.kotlin.core.repository.operation.StorageSyncOperation
import com.mobilejazz.kotlin.core.repository.query.StringKeyQuery
import com.mobilejazz.kotlin.core.sample.domain.model.Item
import com.mobilejazz.kotlin.core.sample.repository.entity.ItemEntity
import com.mobilejazz.kotlin.core.sample.repository.mapper.ItemEntityToItemMapper
import com.mobilejazz.kotlin.core.sample.repository.mapper.ItemToItemEntityMapper
import com.mobilejazz.kotlin.core.sample.repository.network.ItemNetworkDataSource
import com.mobilejazz.kotlin.core.threading.DirectExecutor
import com.mobilejazz.kotlin.core.threading.extensions.onCompleteDirect
import org.junit.Assert.assertEquals
import org.junit.Test

data class Foo(val id: Int)

class ExampleTest {

    @Test
    fun example() {

        val itemNetworkDataSource = ItemNetworkDataSource()

        val getNetworkDatasource: GetDataSource<ItemEntity> = itemNetworkDataSource
        val putNetworkDataSource: PutDataSource<ItemEntity> = itemNetworkDataSource
        val deleteNetworkDataSource: DeleteDataSource = itemNetworkDataSource

        val itemStorageDataSource = InMemoryDataSource<ItemEntity>()

        val getStorageDataSource: GetDataSource<ItemEntity> = itemStorageDataSource
        val putStorageDataSource: PutDataSource<ItemEntity> = itemStorageDataSource
        val deleteStorageDataSource: DeleteDataSource = itemStorageDataSource

        val itemNetworkStorageRepository = NetworkStorageRepository(getStorageDataSource, putStorageDataSource, deleteStorageDataSource, getNetworkDatasource, putNetworkDataSource, deleteNetworkDataSource)

        val toItemEntityMapper = ItemToItemEntityMapper()
        val toItemMapper = ItemEntityToItemMapper()

        val itemRepositoryMapper = RepositoryMapper(itemNetworkStorageRepository, itemNetworkStorageRepository, itemNetworkStorageRepository, toItemEntityMapper, toItemMapper)

        val getRepository: GetRepository<Item> = itemRepositoryMapper
        val putRepository: PutRepository<Item> = itemRepositoryMapper
        val deleteRepository: DeleteRepository = itemRepositoryMapper


        val getAllItem = GetAllInteractor(DirectExecutor, getRepository)

        getAllItem(StringKeyQuery("all-items"), StorageSyncOperation).onCompleteDirect(onSuccess = {
            it!!.forEach { System.out.println(it.name) }
        }, onFailure = {
            Log.d("TEST", "Error")
        })

        System.out.println("=======================")

        getAllItem(StringKeyQuery("all-items"), StorageSyncOperation).onCompleteDirect(onSuccess = {
            it!!.forEach { System.out.println(it.name) }
        }, onFailure = {
            Log.d("TEST", it.localizedMessage)
        })

        assertEquals(true, true)
    }

}

