package com.mobilejazz.sample.core.data.network

import com.mobilejazz.harmony.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.harmony.kotlin.core.repository.query.IntegerIdQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.IntegerIdsQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.threading.extensions.Future
import com.mobilejazz.sample.core.data.model.ItemEntity
import java.util.*
import javax.inject.Inject

class ItemNetworkDataSource @Inject constructor(private val hackerNewsItemService: HackerNewsItemService) : GetDataSource<ItemEntity> {

  override fun get(query: Query): Future<ItemEntity> = when (query) {
    is IntegerIdQuery -> {

      Future {
        val item = hackerNewsItemService.newItem(query.identifier).get()
        return@Future item.copy(lastUpdate = Date())
      }

    }
    else -> throw IllegalArgumentException("Query not mapped correctly!")
  }


  override fun getAll(query: Query): Future<List<ItemEntity>> = when (query) {
    is IntegerIdsQuery -> {

      Future {
        return@Future query.identifiers.map {
          get(IntegerIdQuery(it)).get()
        }
      }

    }
    else -> throw IllegalArgumentException("Query not mapped correctly!")
  }

}