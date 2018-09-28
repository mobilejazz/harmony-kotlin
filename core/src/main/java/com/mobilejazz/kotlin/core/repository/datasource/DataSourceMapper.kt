package com.mobilejazz.kotlin.core.repository.datasource

import com.mobilejazz.kotlin.core.repository.mapper.Mapper
import com.mobilejazz.kotlin.core.repository.mapper.VoidMapper
import com.mobilejazz.kotlin.core.repository.mapper.map
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.threading.extensions.Future
import com.mobilejazz.kotlin.core.threading.extensions.map
import javax.inject.Inject

/**
 * This data source uses mappers to map objects and redirects them to the contained data source, acting as a simple "translator".
 *
 * @param getDataSource Data source with get operations
 * @param putDataSource Data source with put operations
 * @param deleteDataSource Data source with delete operations
 * @param dataSourceEntityToRepositoryEntityMapper Mapper to map data source objects to repository objects
 * @param repositoryEntityToDataSourceEntityMapper Mapper to map repository objects to data source objects
 */
class DataSourceMapper<DataSourceEntity, RepositoryEntity> @Inject constructor(
    private val getDataSource: GetDataSource<DataSourceEntity> = VoidGetDataSource(),
    private val putDataSource: PutDataSource<DataSourceEntity> = VoidPutDataSource(),
    private val deleteDataSource: DeleteDataSource = VoidDeleteDataSource(),
    private val dataSourceEntityToRepositoryEntityMapper: Mapper<DataSourceEntity, RepositoryEntity> = VoidMapper(),
    private val repositoryEntityToDataSourceEntityMapper: Mapper<RepositoryEntity, DataSourceEntity> = VoidMapper()
) : GetDataSource<RepositoryEntity>, PutDataSource<RepositoryEntity>, DeleteDataSource {

  override fun get(query: Query): Future<RepositoryEntity> = getDataSource.get(query).map { dataSourceEntityToRepositoryEntityMapper.map(it) }

  override fun getAll(query: Query): Future<List<RepositoryEntity>> = getDataSource.getAll(query).map { dataSourceEntityToRepositoryEntityMapper.map(it) }

  override fun put(query: Query, value: RepositoryEntity?): Future<RepositoryEntity> {
    val mapped = value?.let { repositoryEntityToDataSourceEntityMapper.map(value) }
    return putDataSource.put(query, mapped)
        .map { dataSourceEntityToRepositoryEntityMapper.map(it) }
  }

  override fun putAll(query: Query, value: List<RepositoryEntity>?): Future<List<RepositoryEntity>> {
    val mapped = value?.let { repositoryEntityToDataSourceEntityMapper.map(value) }
    return putDataSource.putAll(query, mapped)
        .map { dataSourceEntityToRepositoryEntityMapper.map(it) }
  }

  override fun delete(query: Query): Future<Unit> = deleteDataSource.delete(query)

  override fun deleteAll(query: Query): Future<Unit> = deleteDataSource.deleteAll(query)
}