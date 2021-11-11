package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.mapper.map
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
  getDataSource: GetDataSource<In>,
  putDataSource: PutDataSource<In>,
  private val deleteDataSource: DeleteDataSource,
  toOutMapper: Mapper<In, Out>,
  toInMapper: Mapper<Out, In>
) : GetDataSource<Out>, PutDataSource<Out>, DeleteDataSource {

  private val getDataSourceMapper = GetDataSourceMapper(getDataSource, toOutMapper)
  private val putDataSourceMapper = PutDataSourceMapper(putDataSource, toOutMapper, toInMapper)

  override suspend fun get(query: Query): Out = getDataSourceMapper.get(query)

  override suspend fun getAll(query: Query): List<Out> = getDataSourceMapper.getAll(query)

  override suspend fun put(query: Query, value: Out?): Out = putDataSourceMapper.put(query, value)

  override suspend fun putAll(query: Query, value: List<Out>?): List<Out> = putDataSourceMapper.putAll(query, value)

  override suspend fun delete(query: Query): Unit = deleteDataSource.delete(query)
}

class GetDataSourceMapper<In, Out>(
  private val getDataSource: GetDataSource<In>,
  private val toOutMapper: Mapper<In, Out>
) : GetDataSource<Out> {

  override suspend fun get(query: Query): Out = getDataSource.get(query).let { toOutMapper.map(it) }

  override suspend fun getAll(query: Query): List<Out> = getDataSource.getAll(query).let { toOutMapper.map(it) }
}

class PutDataSourceMapper<In, Out>(
  private val putDataSource: PutDataSource<In>,
  private val toOutMapper: Mapper<In, Out>,
  private val toInMapper: Mapper<Out, In>
) : PutDataSource<Out> {

  override suspend fun put(query: Query, value: Out?): Out {
    val mapped = value?.let { toInMapper.map(it) }
    return putDataSource.put(query, mapped)
      .let { toOutMapper.map(it) }
  }

  override suspend fun putAll(query: Query, value: List<Out>?): List<Out> {
    val mapped = value?.let { toInMapper.map(it) }
    return putDataSource.putAll(query, mapped)
      .map { toOutMapper.map(it) }
  }
}
