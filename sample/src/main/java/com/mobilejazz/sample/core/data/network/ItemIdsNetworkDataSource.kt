package com.mobilejazz.sample.core.data.network

import com.mobilejazz.harmony.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.sample.core.data.model.ItemIdsEntity
import javax.inject.Inject

class ItemIdsNetworkDataSource @Inject constructor(private val hackerNewsItemService: HackerNewsItemService) : GetDataSource<ItemIdsEntity> {

  override suspend fun get(query: Query): ItemIdsEntity {
    val askStoriesIds = hackerNewsItemService.askStories()
    return ItemIdsEntity(askStoriesIds)
  }


  override suspend fun getAll(query: Query): List<ItemIdsEntity> = throw UnsupportedOperationException("Unsupported operation")
}