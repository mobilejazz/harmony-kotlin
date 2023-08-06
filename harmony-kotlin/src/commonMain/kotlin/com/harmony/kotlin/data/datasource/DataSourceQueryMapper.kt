package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.query.Query

/**
 * This data source uses mappers to map queries and redirects them to the contained data source, acting as a simple "translator".
 *
 * @param getDataSource Data source with get operations
 * @param putDataSource Data source with put operations
 * @param deleteDataSource Data source with delete operations
 * @param getQueryMapper Mapper for get operations
 * @param putQueryMapper Mapper for put operations
 * @param deleteQueryMapper Mapper for delete operations
 */
@Suppress("UNCHECKED_CAST")
class DataSourceQueryMapper<V,
  GetQueryIn : Query, GetQueryOut : Query,
  PutQueryIn : Query, PutQueryOut : Query,
  DeleteQueryIn : Query, DeleteQueryOut : Query>(
  private val getDataSource: GetDataSource<V>,
  private val putDataSource: PutDataSource<V>,
  private val deleteDataSource: DeleteDataSource,
  private val getQueryMapper: Mapper<GetQueryIn, GetQueryOut>,
  private val putQueryMapper: Mapper<PutQueryIn, PutQueryOut>,
  private val deleteQueryMapper: Mapper<DeleteQueryIn, DeleteQueryOut>
) : GetDataSource<V>, PutDataSource<V>, DeleteDataSource {

  override suspend fun get(query: Query): V {
    val queryOut = getQueryMapper.map(query as GetQueryIn)
    return getDataSource.get(queryOut)
  }

  override suspend fun put(query: Query, value: V?): V {
    val queryOut = putQueryMapper.map(query as PutQueryIn)
    return putDataSource.put(queryOut, value)
  }

  override suspend fun delete(query: Query) {
    val queryOut = deleteQueryMapper.map(query as DeleteQueryIn)
    return deleteDataSource.delete(queryOut)
  }
}
