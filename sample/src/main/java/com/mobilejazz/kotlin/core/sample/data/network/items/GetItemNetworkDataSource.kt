package com.mobilejazz.kotlin.core.sample.data.network.items

import com.mobilejazz.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.sample.data.network.model.ItemsNetwork
import com.mobilejazz.kotlin.core.sample.domain.model.Item
import com.mobilejazz.kotlin.core.sample.repository.entity.ItemEntity
import com.mobilejazz.kotlin.core.threading.extensions.Future
import com.mobilejazz.kotlin.core.threading.extensions.map
import com.mobilejazz.kotlin.core.threading.extensions.mapError
import org.worldreader.classroom.dataprovider.network.error.RetrofitErrorAdapter


class GetItemNetworkDataSource @javax.inject.Inject constructor(
    private val service: ItemApiService,
    private val errorAdapter: RetrofitErrorAdapter) : GetDataSource<ItemEntity> {

  override fun get(query: Query): Future<ItemEntity> {
    throw UnsupportedOperationException() // TODO What to return when the method is not supported at all?
  }

  override fun getAll(query: Query): Future<List<ItemEntity>> {
    return service.items().map { it.results }.mapError<List<ItemEntity>, Throwable> { errorAdapter.of(it) }
  }
}