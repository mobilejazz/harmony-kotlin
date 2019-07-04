package com.mobilejazz.kotlin.core.repository.datasource

import com.mobilejazz.kotlin.core.repository.mapper.Mapper
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
 * @param toInMapper Mapper to map data source objects to repository objects
 * @param toInMapperFromList Mapper to map repository objects to data source objects
 */
class SerializationDataSourceMapper<SerializedIn, Out> @Inject constructor(
    private val getDataSource: GetDataSource<SerializedIn>,
    private val putDataSource: PutDataSource<SerializedIn>,
    private val deleteDataSource: DeleteDataSource,
    private val toOutMapper: Mapper<SerializedIn, Out>,
    private val toOutListMapper: Mapper<SerializedIn, List<Out>>,
    private val toInMapper: Mapper<Out, SerializedIn>,
    private val toInMapperFromList: Mapper<List<Out>, SerializedIn>
) : GetDataSource<Out>, PutDataSource<Out>, DeleteDataSource {

  override fun get(query: Query): Future<Out> = getDataSource.get(query).map { toOutMapper.map(it) }

  override fun getAll(query: Query): Future<List<Out>> = getDataSource.get(query).map { toOutListMapper.map(it) }

  override fun put(query: Query, value: Out?): Future<Out> {
    val mapped = value?.let { toInMapper.map(value) }
    return putDataSource.put(query, mapped)
        .map { toOutMapper.map(it) }
  }

  override fun putAll(query: Query, value: List<Out>?): Future<List<Out>> {
    val mapped = value?.let { toInMapperFromList.map(value) }
    return putDataSource.put(query, mapped)
        .map { toOutListMapper.map(it) }
  }

  override fun delete(query: Query): Future<Unit> = deleteDataSource.delete(query)

  override fun deleteAll(query: Query): Future<Unit> = deleteDataSource.deleteAll(query)
}
