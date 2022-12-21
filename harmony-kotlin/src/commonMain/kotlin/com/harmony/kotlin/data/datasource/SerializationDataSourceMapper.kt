package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.query.Query

/**
 * This data source uses mappers to map objects and redirects them to the contained data source, acting as a simple "translator".
 * This class map a List to a single Object when using putAll, in this case it will call the put method on the contained data source
 * This class map an Object to a List when using getAll, int his case it will call the get method on the contained data source
 *
 * @param getDataSource Data source with get operations
 * @param putDataSource Data source with put operations
 * @param toOutMapper Mapper to map data source objects to repository objects
 * @param toOutListMapper Mapper to map data source objects to repository object lists
 * @param toInMapper Mapper to map repository objects to data source objects
 * @param toInMapperFromList Mapper to map repository object lists to data source objects
 */
@Deprecated("Not needed after getAll & putAll deprecation. Use a normal DataSourceMapper")
class SerializationDataSourceMapper<SerializedIn, Out>(
  private val getDataSource: GetDataSource<SerializedIn>,
  private val putDataSource: PutDataSource<SerializedIn>,
  private val toOutMapper: Mapper<SerializedIn, Out>,
  private val toOutListMapper: Mapper<SerializedIn, List<Out>>,
  private val toInMapper: Mapper<Out, SerializedIn>,
  private val toInMapperFromList: Mapper<List<Out>, SerializedIn>
) : GetDataSource<Out>, PutDataSource<Out> {

  override suspend fun get(query: Query): Out = getDataSource.get(query).let { toOutMapper.map(it) }

  @Deprecated("Use get instead")
  override suspend fun getAll(query: Query): List<Out> = getDataSource.get(query).let { toOutListMapper.map(it) }

  override suspend fun put(query: Query, value: Out?): Out {
    val mapped = value?.let { toInMapper.map(value) }
    return putDataSource.put(query, mapped)
      .let { toOutMapper.map(it) }
  }

  @Deprecated("Use put instead")
  override suspend fun putAll(query: Query, value: List<Out>?): List<Out> {
    val mapped = value?.let { toInMapperFromList.map(value) }
    return putDataSource.put(query, mapped)
      .let { toOutListMapper.map(it) }
  }
}
