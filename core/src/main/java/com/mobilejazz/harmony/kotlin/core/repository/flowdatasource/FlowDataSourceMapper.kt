package com.mobilejazz.harmony.kotlin.core.repository.flowdatasource

import com.mobilejazz.harmony.kotlin.core.repository.mapper.Mapper
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
class FlowDataSourceMapper<In, Out> @Inject constructor(
    getDataSource: FlowGetDataSource<In>,
    putDataSource: FlowPutDataSource<In>,
    private val deleteDataSource: FlowDeleteDataSource,
    toOutMapper: Mapper<In, Out>,
    toInMapper: Mapper<Out, In>
) : FlowGetDataSource<Out>, FlowPutDataSource<Out>, FlowDeleteDataSource {

  private val getDataSourceMapper = FlowGetDataSourceMapper(getDataSource, toOutMapper)
  private val putDataSourceMapper = FlowPutDataSourceMapper(putDataSource, toOutMapper, toInMapper)

  override fun get(query: Query): Flow<Out> = getDataSourceMapper.get(query)

  override fun getAll(query: Query): Flow<List<Out>> = getDataSourceMapper.getAll(query)

  override fun put(query: Query, value: Out?): Flow<Out> = putDataSourceMapper.put(query, value)

  override fun putAll(query: Query, value: List<Out>?): Flow<List<Out>> = putDataSourceMapper.putAll(query, value)

  override fun delete(query: Query): Flow<Unit> = deleteDataSource.delete(query)

  override fun deleteAll(query: Query): Flow<Unit> = deleteDataSource.deleteAll(query)
}

class FlowGetDataSourceMapper<In, Out> @Inject constructor(
    private val getDataSource: FlowGetDataSource<In>,
    private val toOutMapper: Mapper<In, Out>) : FlowGetDataSource<Out> {

  override fun get(query: Query): Flow<Out> = getDataSource.get(query).map { toOutMapper(it) }

  override fun getAll(query: Query): Flow<List<Out>> = getDataSource.getAll(query).map { it.map(toOutMapper) }

}

class FlowPutDataSourceMapper<In, Out> @Inject constructor(
    private val putDataSource: FlowPutDataSource<In>,
    private val toOutMapper: Mapper<In, Out>,
    private val toInMapper: Mapper<Out, In>) : FlowPutDataSource<Out> {

  override fun put(query: Query, value: Out?): Flow<Out> {
    val mapped = value?.let(toInMapper)
    return putDataSource.put(query, mapped)
        .map { toOutMapper.map(it) }
  }

  override fun putAll(query: Query, value: List<Out>?): Flow<List<Out>> {
    val mapped = value?.map(toInMapper)
    return putDataSource.putAll(query, mapped)
        .map { it.map(toOutMapper) }
  }
}