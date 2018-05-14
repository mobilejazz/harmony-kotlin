package com.mobilejazz.kotlin.core.repository.datasource

import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.repository.mapper.Mapper
import com.mobilejazz.kotlin.core.repository.mapper.map
import com.mobilejazz.kotlin.core.threading.Future
import com.mobilejazz.kotlin.core.threading.extensions.map
import javax.inject.Inject

/**
 * This data source uses mappers to map objects and redirects them to the contained data source, acting as a simple "translator".
 */
class DataSourceMapper<From, To> @Inject constructor(private val getDataSource: GetDataSource<To>,
                                                     private val putDataSource: PutDataSource<To>,
                                                     private val deleteDataSource: DeleteDataSource,
                                                     private val toToMapper: Mapper<From, To>,
                                                     private val toFromMapper: Mapper<To, From>) : GetDataSource<From>, PutDataSource<From>, DeleteDataSource {

  override fun get(query: Query): Future<From> = getDataSource.get(query).map { toFromMapper.map(it) }

  override fun getAll(query: Query): Future<List<From>> = getDataSource.getAll(query).map { toFromMapper.map(it) }

  override fun put(query: Query, value: From): Future<From> = putDataSource.put(query, toToMapper.map(value)).map { toFromMapper.map(it) }

  override fun putAll(query: Query, value: List<From>): Future<List<From>> = putDataSource.putAll(query, toToMapper.map(value)).map { toFromMapper.map(it) }

  override fun delete(query: Query): Future<Void> = deleteDataSource.delete(query)

  override fun deleteAll(query: Query): Future<Void> = deleteDataSource.deleteAll(query)
}