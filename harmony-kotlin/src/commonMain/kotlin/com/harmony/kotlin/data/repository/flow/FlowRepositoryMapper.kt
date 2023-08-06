package com.harmony.kotlin.data.repository.flow

import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.operation.Operation
import com.harmony.kotlin.data.query.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * This repository uses mappers to map objects and redirects them to the contained repository, acting as a simple "translator".
 *
 * @param getRepository Repository with get operations
 * @param putRepository Repository with put operations
 * @param deleteRepository Repository with delete operations
 * @param toOutMapper Mapper to map data objects to domain objects
 * @param toInMapper Mapper to map domain objects to data objects
 */
class FlowRepositoryMapper<In, Out>(
  private val getRepository: FlowGetRepository<In>,
  private val putRepository: FlowPutRepository<In>,
  private val deleteRepository: FlowDeleteRepository,
  private val toOutMapper: Mapper<In, Out>,
  private val toInMapper: Mapper<Out, In>
) : FlowGetRepository<Out>, FlowPutRepository<Out>, FlowDeleteRepository {

  override fun get(query: Query, operation: Operation): Flow<Out> = getRepository.get(query, operation).map { toOutMapper.map(it) }

  override fun put(query: Query, value: Out?, operation: Operation): Flow<Out> {
    val mapped = value?.let { toInMapper.map(it) }
    return putRepository.put(query, mapped, operation).map {
      toOutMapper.map(it)
    }
  }

  override fun delete(query: Query, operation: Operation) = deleteRepository.delete(query, operation)
}

class FlowGetRepositoryMapper<In, Out>(
  private val getRepository: FlowGetRepository<In>,
  private val toOutMapper: Mapper<In, Out>
) : FlowGetRepository<Out> {

  override fun get(query: Query, operation: Operation): Flow<Out> = getRepository.get(query, operation).map { it.let { toOutMapper.map(it) } }
}

class FlowPutRepositoryMapper<In, Out>(
  private val putRepository: FlowPutRepository<In>,
  private val toOutMapper: Mapper<In, Out>,
  private val toInMapper: Mapper<Out, In>
) : FlowPutRepository<Out> {

  override fun put(query: Query, value: Out?, operation: Operation): Flow<Out> {
    val mapped = value?.let { toInMapper.map(it) }
    return putRepository.put(query, mapped, operation).map {
      toOutMapper.map(it)
    }
  }
}
