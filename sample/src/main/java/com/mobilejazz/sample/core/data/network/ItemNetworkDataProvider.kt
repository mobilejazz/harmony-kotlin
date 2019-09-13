package com.mobilejazz.sample.core.data.network

import com.mobilejazz.harmony.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.harmony.kotlin.core.repository.query.IntegerIdQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.IntegerIdsQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.sample.core.data.model.ItemEntity
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ItemNetworkDataProvider @Inject constructor(private val hackerNewsItemService: HackerNewsItemService) : GetDataSource<ItemEntity> {

  override suspend fun get(query: Query): ItemEntity = when (query) {
    is IntegerIdQuery -> {
      val item = hackerNewsItemService.newItem(query.identifier)
      item.lastUpdate = Date()
      item.expiryTime = TimeUnit.MINUTES.toMillis(5)

      item
    }
    else -> throw IllegalArgumentException("Query not mapped correctly!")
  }


  override suspend fun getAll(query: Query): List<ItemEntity> = when (query) {
    is IntegerIdsQuery -> {
      query.identifiers.map {
        get(IntegerIdQuery(it))
      }
    }
    else -> throw IllegalArgumentException("Query not mapped correctly!")
  }

}