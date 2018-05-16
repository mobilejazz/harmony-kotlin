package com.mobilejazz.kotlin.core.sample.repository.network

import com.mobilejazz.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.sample.repository.entity.ItemEntity
import com.mobilejazz.kotlin.core.threading.extensions.Future
import com.mobilejazz.kotlin.core.threading.extensions.emptyFuture

class ItemNetworkDataSource : GetDataSource<ItemEntity>, PutDataSource<ItemEntity>, DeleteDataSource {
    override fun get(query: Query): Future<ItemEntity> {
        return Future { itemEntity() }
    }

    override fun getAll(query: Query): Future<List<ItemEntity>> {
        return Future { itemEntities() }
    }

    override fun put(query: Query, value: ItemEntity?): Future<ItemEntity> {
        return Future { value!! }
    }

    override fun putAll(query: Query, value: List<ItemEntity>?): Future<List<ItemEntity>> {
        return Future { value!! }
    }

    override fun delete(query: Query): Future<Void> {
        return emptyFuture()
    }

    override fun deleteAll(query: Query): Future<Void> {
        return emptyFuture()
    }

    private fun itemEntity() = ItemEntity("fake-id", "fake-name", 12.3, 0, "image-url")

    private fun itemEntities() = listOf(itemEntity(), itemEntity(), itemEntity())

}