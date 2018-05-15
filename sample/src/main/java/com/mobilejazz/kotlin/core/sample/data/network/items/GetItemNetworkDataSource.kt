package com.mobilejazz.kotlin.core.sample.data.network.items

import com.mobilejazz.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.sample.domain.model.Item
import com.mobilejazz.kotlin.core.threading.Future
import com.mobilejazz.kotlin.core.threading.extensions.mapError
import org.worldreader.classroom.dataprovider.network.error.RetrofitErrorAdapter


class GetItemNetworkDataSource @javax.inject.Inject constructor(
    private val service: ItemApiService,
    private val errorAdapter: RetrofitErrorAdapter) : GetDataSource<Item> {

  override fun get(query: Query): Future<Item> {
    throw UnsupportedOperationException() // TODO What to return when the method is not supported at all
  }

  override fun getAll(query: Query): Future<List<Item>> {
    throw UnsupportedOperationException() // TODO What to return when the method is not supported at all

//    return service.items().mapError<List<Item>, Throwable> { errorAdapter.of(it) }
  }
}