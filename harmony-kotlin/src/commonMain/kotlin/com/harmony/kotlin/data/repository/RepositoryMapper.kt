package com.harmony.kotlin.data.repository

import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.mapper.map
import com.harmony.kotlin.data.operation.Operation
import com.harmony.kotlin.data.query.Query

/**
 * This repository uses mappers to map objects and redirects them to the contained repository, acting as a simple "translator".
 *
 * @param getRepository Repository with get operations
 * @param putRepository Repository with put operations
 * @param deleteRepository Repository with delete operations
 * @param toOutMapper Mapper to map data objects to domain objects
 * @param toInMapper Mapper to map domain objects to data objects
 */
class RepositoryMapper<In, Out>(
  private val getRepository: GetRepository<In>,
  private val putRepository: PutRepository<In>,
  private val deleteRepository: DeleteRepository,
  private val toOutMapper: Mapper<In, Out>,
  private val toInMapper: Mapper<Out, In>
) : GetRepository<Out>, PutRepository<Out>, DeleteRepository {

  override suspend fun get(query: Query, operation: Operation): Out = get(getRepository, toOutMapper, query, operation)

  override suspend fun put(query: Query, value: Out?, operation: Operation): Out = put(putRepository, toOutMapper, toInMapper, value, query, operation)

  override suspend fun delete(query: Query, operation: Operation) = deleteRepository.delete(query, operation)
}

class GetRepositoryMapper<In, Out>(
  private val getRepository: GetRepository<In>,
  private val toOutMapper: Mapper<In, Out>
) : GetRepository<Out> {

  override suspend fun get(query: Query, operation: Operation): Out = get(getRepository, toOutMapper, query, operation)
}

class PutRepositoryMapper<In, Out>(
  private val putRepository: PutRepository<In>,
  private val toOutMapper: Mapper<In, Out>,
  private val toInMapper: Mapper<Out, In>
) : PutRepository<Out> {

  override suspend fun put(query: Query, value: Out?, operation: Operation): Out = put(putRepository, toOutMapper, toInMapper, value, query, operation)
}

private suspend fun <In, Out> get(
  getRepository: GetRepository<In>,
  toOutMapper: Mapper<In, Out>,
  query: Query,
  operation: Operation
): Out = getRepository.get(query, operation).let { toOutMapper.map(it) }

private suspend fun <In, Out> put(
  putRepository: PutRepository<In>,
  toOutMapper: Mapper<In, Out>,
  toInMapper: Mapper<Out, In>,
  value: Out?,
  query: Query,
  operation: Operation
): Out {
  val mapped = value?.let { toInMapper.map(it) }
  return putRepository.put(query, mapped, operation).let {
    toOutMapper.map(it)
  }
}
