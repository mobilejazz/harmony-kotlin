package com.harmony.kotlin.data.datasource.flow

import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.query.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * This data source uses mappers to map objects and redirects them to the contained data source, acting as a simple "translator".
 * This class map a List to a single Object when using putAll, in this case it will call the put method on the contained data source
 * This class map a Object to a List when using getAll, int his case it will call the get method on the contained data source
 *
 * @param getDataSource Data source with get operations
 * @param putDataSource Data source with put operations
 * @param deleteDataSource Data source with delete operations
 * @param toOutMapper Mapper to map data source objects to repository objects
 * @param toOutListMapper Mapper to map data source objects to repository object lists
 * @param toInMapper Mapper to map repository objects to data source objects
 * @param toInMapperFromList Mapper to map repository object lists to data source objects
 */
class FlowSerializationDataSourceMapper<SerializedIn, Out>(
        private val getDataSource: FlowGetDataSource<SerializedIn>,
        private val putDataSource: FlowPutDataSource<SerializedIn>,
        private val deleteDataSource: FlowDeleteDataSource,
        private val toOutMapper: Mapper<SerializedIn, Out>,
        private val toOutListMapper: Mapper<SerializedIn, List<Out>>,
        private val toInMapper: Mapper<Out, SerializedIn>,
        private val toInMapperFromList: Mapper<List<Out>, SerializedIn>
) : FlowGetDataSource<Out>, FlowPutDataSource<Out>, FlowDeleteDataSource {

  override fun get(query: Query) = getDataSource.get(query).map { toOutMapper.map(it) }

  override fun getAll(query: Query): Flow<List<Out>> = getDataSource.get(query).map { toOutListMapper.map(it) }

  override fun put(query: Query, value: Out?): Flow<Out> {
    val mapped = value?.let { toInMapper.map(it) }
    return putDataSource.put(query, mapped)
        .map { toOutMapper.map(it)}
  }

  override fun putAll(query: Query, value: List<Out>?): Flow<List<Out>> {
    val mapped = value?.let { toInMapperFromList.map(it) }
    return putDataSource.put(query, mapped)
        .map { toOutListMapper.map(it) }
  }

  override fun delete(query: Query) = deleteDataSource.delete(query)

}