package com.harmony.kotlin.data.datasource.flow

import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.mapper.map
import com.harmony.kotlin.data.query.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * This data source uses mappers to map objects and redirects them to the contained data source, acting as a simple "translator".
 *
 * @param getDataSource Data source with get operations
 * @param putDataSource Data source with put operations
 * @param deleteDataSource Data source with delete operations
 * @param toOutMapper Mapper to map data source objects to repository objects
 * @param toInMapper Mapper to map repository objects to data source objects
 */
class FlowDataSourceMapper<In, Out>(
  getDataSource: FlowGetDataSource<In>,
  putDataSource: FlowPutDataSource<In>,
  private val deleteDataSource: FlowDeleteDataSource,
  toOutMapper: Mapper<In, Out>,
  toInMapper: Mapper<Out, In>
) : FlowGetDataSource<Out>, FlowPutDataSource<Out>, FlowDeleteDataSource {

  private val getDataSourceMapper = FlowGetDataSourceMapper(getDataSource, toOutMapper)
  private val putDataSourceMapper = FlowPutDataSourceMapper(putDataSource, toOutMapper, toInMapper)

  override fun get(query: Query): Flow<Out> = getDataSourceMapper.get(query)

  override fun put(query: Query, value: Out?): Flow<Out> = putDataSourceMapper.put(query, value)

  override fun delete(query: Query): Flow<Unit> = deleteDataSource.delete(query)
}

class FlowGetDataSourceMapper<In, Out>(
  private val getDataSource: FlowGetDataSource<In>,
  private val toOutMapper: Mapper<In, Out>
) : FlowGetDataSource<Out> {

  override fun get(query: Query): Flow<Out> = getDataSource.get(query).map { toOutMapper.map(it) }
}

class FlowPutDataSourceMapper<In, Out>(
  private val putDataSource: FlowPutDataSource<In>,
  private val toOutMapper: Mapper<In, Out>,
  private val toInMapper: Mapper<Out, In>
) : FlowPutDataSource<Out> {

  override fun put(query: Query, value: Out?): Flow<Out> {
    val mapped = value?.let { toInMapper.map(it) }
    return putDataSource.put(query, mapped)
      .map { toOutMapper.map(it) }
  }
}
