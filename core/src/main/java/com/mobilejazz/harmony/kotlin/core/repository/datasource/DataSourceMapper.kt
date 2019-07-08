package com.mobilejazz.harmony.kotlin.core.repository.datasource

import com.mobilejazz.harmony.kotlin.core.repository.mapper.Mapper
import com.mobilejazz.harmony.kotlin.core.repository.mapper.map
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.threading.extensions.Future
import com.mobilejazz.harmony.kotlin.core.threading.extensions.map
import javax.inject.Inject

/**
 * This data source uses mappers to map objects and redirects them to the contained data source, acting as a simple "translator".
 *
 * @param getDataSource Data source with get operations
 * @param putDataSource Data source with put operations
 * @param deleteDataSource Data source with delete operations
 * @param toOutMapper Mapper to map data source objects to repository objects
 * @param toInMapper Mapper to map repository objects to data source objects
 */
class DataSourceMapper<In, Out> @Inject constructor(
    getDataSource: GetDataSource<In>,
    putDataSource: PutDataSource<In>,
    private val deleteDataSource: DeleteDataSource,
    toOutMapper: Mapper<In, Out>,
    toInMapper: Mapper<Out, In>
) : GetDataSource<Out>, PutDataSource<Out>, DeleteDataSource {

  private val getDataSourceMapper = GetDataSourceMapper(getDataSource, toOutMapper)
  private val putDataSourceMapper = PutDataSourceMapper(putDataSource, toOutMapper, toInMapper)

  override fun get(query: Query): Future<Out> = getDataSourceMapper.get(query)

  override fun getAll(query: Query): Future<List<Out>> = getDataSourceMapper.getAll(query)

  override fun put(query: Query, value: Out?): Future<Out> = putDataSourceMapper.put(query, value)

  override fun putAll(query: Query, value: List<Out>?): Future<List<Out>> = putDataSourceMapper.putAll(query, value)

  override fun delete(query: Query): Future<Unit> = deleteDataSource.delete(query)

  override fun deleteAll(query: Query): Future<Unit> = deleteDataSource.deleteAll(query)
}

class GetDataSourceMapper<In, Out> @Inject constructor(
    private val getDataSource: GetDataSource<In>,
    private val toOutMapper: Mapper<In, Out>) : GetDataSource<Out> {

  override fun get(query: Query): Future<Out> = getDataSource.get(query).map { toOutMapper.map(it) }

  override fun getAll(query: Query): Future<List<Out>> = getDataSource.getAll(query).map { toOutMapper.map(it) }
}

class PutDataSourceMapper<In, Out> @Inject constructor(
    private val putDataSource: PutDataSource<In>,
    private val toOutMapper: Mapper<In, Out>,
    private val toInMapper: Mapper<Out, In>) : PutDataSource<Out> {

  override fun put(query: Query, value: Out?): Future<Out> {
    val mapped = value?.let { toInMapper.map(value) }
    return putDataSource.put(query, mapped)
        .map { toOutMapper.map(it) }
  }

  override fun putAll(query: Query, value: List<Out>?): Future<List<Out>> {
    val mapped = value?.let { toInMapper.map(value) }
    return putDataSource.putAll(query, mapped)
        .map { toOutMapper.map(it) }
  }
}
