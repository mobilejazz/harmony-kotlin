package com.mobilejazz.kotlin.core.repository

import com.mobilejazz.kotlin.core.repository.mapper.Mapper
import com.mobilejazz.kotlin.core.repository.mapper.VoidMapper
import com.mobilejazz.kotlin.core.repository.mapper.map
import com.mobilejazz.kotlin.core.repository.operation.Operation
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.threading.extensions.Future
import com.mobilejazz.kotlin.core.threading.extensions.map
import javax.inject.Inject


/**
 * This repository uses mappers to map objects and redirects them to the contained repository, acting as a simple "translator".
 *
 * @param getRepository Repository with get operations
 * @param putRepository Repository with put operations
 * @param deleteRepository Repository with delete operations
 * @param repositoryEntityToDomainModelMapper Mapper to map data objects to domain objects
 * @param domainModelToRepositoryEntityMapper Mapper to map domain objects to data objects
 */
class RepositoryMapper<DataEntity, DomainModel> @Inject constructor(
    private val getRepository: GetRepository<DataEntity>,
    private val putRepository: PutRepository<DataEntity>,
    private val deleteRepository: DeleteRepository,
    private val repositoryEntityToDomainModelMapper: Mapper<DataEntity, DomainModel>,
    private val domainModelToRepositoryEntityMapper: Mapper<DomainModel, DataEntity>
) : GetRepository<DomainModel>, PutRepository<DomainModel>, DeleteRepository {

  override fun get(query: Query, operation: Operation): Future<DomainModel> = getRepository.get(query, operation).map { repositoryEntityToDomainModelMapper.map(it) }

  override fun getAll(query: Query, operation: Operation): Future<List<DomainModel>> = getRepository.getAll(query, operation).map { repositoryEntityToDomainModelMapper.map(it) }

  override fun put(query: Query, value: DomainModel?, operation: Operation): Future<DomainModel> {
    val mapped = value?.let { domainModelToRepositoryEntityMapper.map(it) }
    return putRepository.put(query, mapped, operation).map {
      repositoryEntityToDomainModelMapper.map(it)
    }
  }

  override fun putAll(query: Query, value: List<DomainModel>?, operation: Operation): Future<List<DomainModel>> {
    val mapped = value?.let { domainModelToRepositoryEntityMapper.map(value) }
    return putRepository.putAll(query, mapped, operation).map { repositoryEntityToDomainModelMapper.map(it) }
  }

  override fun delete(query: Query, operation: Operation): Future<Unit> = deleteRepository.delete(query, operation)

  override fun deleteAll(query: Query, operation: Operation): Future<Unit> = deleteRepository.deleteAll(query, operation)

}