package com.mobilejazz.kotlin.core.repository.datasource

import com.mobilejazz.kotlin.core.repository.mapper.ListToObjectMapper
import com.mobilejazz.kotlin.core.repository.mapper.Mapper
import com.mobilejazz.kotlin.core.repository.mapper.ObjectToListMapper
import com.mobilejazz.kotlin.core.repository.mapper.map
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.threading.extensions.Future
import com.mobilejazz.kotlin.core.threading.extensions.map
import javax.inject.Inject

/**
 * This data source uses mappers to map objects and redirects them to the contained data source, acting as a simple "translator".
 * This class map a List to a single Object when using putAll, in this case it will call the put method on the contained data source
 * This class map a Object to a List when using getAll, int his case it will call the get method on the contained data source
 *
 * @param getDataSource Data source with get operations
 * @param putDataSource Data source with put operations
 * @param deleteDataSource Data source with delete operations
 * @param listToObjectOutMapper Mapper to map data source objects to repository objects
 * @param objectToListInMapper Mapper to map repository objects to data source objects
 */
class ListJoinDataSourceMapper<In, Out> @Inject constructor(
    private val getDataSource: GetDataSource<In>,
    private val putDataSource: PutDataSource<In>,
    private val deleteDataSource: DeleteDataSource,
    private val listToObjectOutMapper: ListToObjectMapper<In, Out>,
    private val objectToListOutMapper: ObjectToListMapper<In, Out>,
    private val listToObjectInMapper: ListToObjectMapper<Out, In>,
    private val objectToListInMapper: ObjectToListMapper<Out, In>
) : GetDataSource<Out>, PutDataSource<Out>, DeleteDataSource {

  override fun get(query: Query): Future<Out> = getDataSource.get(query).map { listToObjectOutMapper.map(it) }

  override fun getAll(query: Query): Future<List<Out>> = getDataSource.get(query).map { objectToListOutMapper.mapToList(it) }

  override fun put(query: Query, value: Out?): Future<Out> {
    val mapped = value?.let { objectToListInMapper.map(value) }
    return putDataSource.put(query, mapped)
        .map { listToObjectOutMapper.map(it) }
  }

  override fun putAll(query: Query, value: List<Out>?): Future<List<Out>> {
    val mapped = value?.let { listToObjectInMapper.mapToObject(value) }
    return putDataSource.put(query, mapped)
        .map { objectToListOutMapper.mapToList(it) }
  }

  override fun delete(query: Query): Future<Unit> = deleteDataSource.delete(query)

  override fun deleteAll(query: Query): Future<Unit> = deleteDataSource.deleteAll(query)
}
