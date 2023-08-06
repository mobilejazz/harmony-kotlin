package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.query.Query

/**
 * This data source uses mappers to map objects and redirects them to the contained data source, acting as a simple "translator".
 *
 * @param getDataSource Data source with get operations
 * @param putDataSource Data source with put operations
 * @param deleteDataSource Data source with delete operations
 * @param toOutMapper Mapper to map data source objects to repository objects
 * @param toInMapper Mapper to map repository objects to data source objects
 */
class DataSourceMapper<In, Out>(
  private val getDataSource: GetDataSource<In>,
  private val putDataSource: PutDataSource<In>,
  private val deleteDataSource: DeleteDataSource,
  private val toOutMapper: Mapper<In, Out>,
  private val toInMapper: Mapper<Out, In>
) : GetDataSource<Out>, PutDataSource<Out>, DeleteDataSource {

  override suspend fun get(query: Query): Out = get(getDataSource, toOutMapper, query)

  override suspend fun put(query: Query, value: Out?): Out = put(putDataSource, toOutMapper, toInMapper, value, query)

  override suspend fun delete(query: Query): Unit = deleteDataSource.delete(query)
}

class GetDataSourceMapper<In, Out>(
  private val getDataSource: GetDataSource<In>,
  private val toOutMapper: Mapper<In, Out>
) : GetDataSource<Out> {

  override suspend fun get(query: Query): Out = get(getDataSource, toOutMapper, query)
}

class PutDataSourceMapper<In, Out>(
  private val putDataSource: PutDataSource<In>,
  private val toOutMapper: Mapper<In, Out>,
  private val toInMapper: Mapper<Out, In>
) : PutDataSource<Out> {

  override suspend fun put(query: Query, value: Out?): Out = put(putDataSource, toOutMapper, toInMapper, value, query)
}

private suspend fun <In, Out> get(
  getDataSource: GetDataSource<In>,
  toOutMapper: Mapper<In, Out>,
  query: Query
): Out = getDataSource.get(query).let { toOutMapper.map(it) }

private suspend fun <In, Out> put(
  putDataSource: PutDataSource<In>,
  toOutMapper: Mapper<In, Out>,
  toInMapper: Mapper<Out, In>,
  value: Out?,
  query: Query
): Out {
  val mapped = value?.let { toInMapper.map(it) }
  return putDataSource.put(query, mapped).let {
    toOutMapper.map(it)
  }
}
