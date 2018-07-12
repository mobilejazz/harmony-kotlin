package com.mobilejazz.kotlin.core.repository

import com.mobilejazz.kotlin.core.repository.mapper.Mapper
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
 * @param toToMapper Mapper to map objects
 * @param toFromMapper Mapper to map objects
 */
class RepositoryMapper<From, To> @Inject constructor(private val getRepository: GetRepository<To>,
                                                     private val putRepository: PutRepository<To>,
                                                     private val deleteRepository: DeleteRepository,
                                                     private val toToMapper: Mapper<From, To>,
                                                     private val toFromMapper: Mapper<To, From>) : GetRepository<From>, PutRepository<From>, DeleteRepository {

  override fun get(query: Query, operation: Operation): Future<From> = getRepository.get(query, operation).map { toFromMapper.map(it) }

  override fun getAll(query: Query, operation: Operation): Future<List<From>> = getRepository.getAll(query, operation).map { toFromMapper.map(it) }

  override fun put(query: Query, value: From?, operation: Operation): Future<From> {
    val mapped = value?.let { toToMapper.map(it) }
    return putRepository.put(query, mapped, operation).map { toFromMapper.map(it)
    }
  }

  override fun putAll(query: Query, value: List<From>?, operation: Operation): Future<List<From>> {
    val mapped = value?.let { toToMapper.map(value) }
    return putRepository.putAll(query, mapped, operation).map { toFromMapper.map(it) }
  }

  override fun delete(query: Query, operation: Operation): Future<Unit> = deleteRepository.delete(query, operation)

  override fun deleteAll(query: Query, operation: Operation): Future<Unit> = deleteRepository.deleteAll(query, operation)

}