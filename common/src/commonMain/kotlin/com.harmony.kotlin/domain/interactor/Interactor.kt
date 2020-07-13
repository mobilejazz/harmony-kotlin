package com.harmony.kotlin.domain.interactor

import com.harmony.kotlin.data.operation.DefaultOperation
import com.harmony.kotlin.data.operation.Operation
import com.harmony.kotlin.data.query.IdQuery
import com.harmony.kotlin.data.query.IdsQuery
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.query.VoidQuery
import com.harmony.kotlin.data.repository.DeleteRepository
import com.harmony.kotlin.data.repository.GetRepository
import com.harmony.kotlin.data.repository.PutRepository
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetInteractor<M>(private val coroutineContext: CoroutineContext, private val getRepository: GetRepository<M>) {

  suspend operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation): M =
      withContext(coroutineContext) {
        getRepository.get(query, operation)
      }
}

class GetAllInteractor<M>(private val coroutineContext: CoroutineContext, private val getRepository: GetRepository<M>) {

  suspend operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation): List<M> =
      withContext(coroutineContext) {
        getRepository.getAll(query, operation)
      }
}

class PutInteractor<M>(private val coroutineContext: CoroutineContext, private val putRepository: PutRepository<M>) {

  suspend operator fun invoke(m: M? = null, query: Query = VoidQuery, operation: Operation = DefaultOperation): M =
      withContext(coroutineContext) {
        putRepository.put(query, m, operation)
      }
}

class PutAllInteractor<M>(private val coroutineContext: CoroutineContext, private val putRepository: PutRepository<M>) {

  suspend operator fun invoke(m: List<M>? = null, query: Query = VoidQuery, operation: Operation = DefaultOperation): List<M> =
      withContext(coroutineContext) {
        putRepository.putAll(query, m, operation)
      }
}

class DeleteInteractor(private val coroutineContext: CoroutineContext, private val deleteRepository: DeleteRepository) {

  suspend operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation) =
      withContext(coroutineContext) {
        deleteRepository.delete(query, operation)
      }
}

class DeleteAllInteractor(private val coroutineContext: CoroutineContext, private val deleteRepository: DeleteRepository) {

  suspend operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation) =
      withContext(coroutineContext) {
        deleteRepository.deleteAll(query, operation)
      }
}


suspend fun <K, V> GetInteractor<V>.execute(id: K, operation: Operation = DefaultOperation): V = this.invoke(IdQuery(id), operation)

suspend fun <K, V> GetAllInteractor<V>.execute(ids: List<K>, operation: Operation = DefaultOperation): List<V> = this.invoke(IdsQuery(ids), operation)

suspend fun <K, V> PutInteractor<V>.execute(id: K, value: V?, operation: Operation = DefaultOperation): V = this.invoke(value, IdQuery(id), operation)

suspend fun <K, V> PutAllInteractor<V>.execute(ids: List<K>, values: List<V>? = emptyList(), operation: Operation = DefaultOperation) = this.invoke(values, IdsQuery(ids), operation)

suspend fun <K> DeleteInteractor.execute(id: K, operation: Operation = DefaultOperation) = this.invoke(IdQuery(id), operation)

suspend fun <K> DeleteAllInteractor.execute(ids: List<K>, operation: Operation = DefaultOperation) = this.invoke(IdsQuery(ids), operation)


fun <V> GetRepository<V>.toGetInteractor(coroutineContext: CoroutineContext) = GetInteractor(coroutineContext, this)

fun <V> GetRepository<V>.toGetAllInteractor(coroutineContext: CoroutineContext) = GetAllInteractor(coroutineContext, this)

fun <V> PutRepository<V>.toPutInteractor(coroutineContext: CoroutineContext) = PutInteractor(coroutineContext, this)

fun <V> PutRepository<V>.toPutAllInteractor(coroutineContext: CoroutineContext) = PutAllInteractor(coroutineContext, this)

fun DeleteRepository.toDeleteInteractor(coroutineContext: CoroutineContext) = DeleteInteractor(coroutineContext, this)

fun DeleteRepository.toDeleteAllInteractor(coroutineContext: CoroutineContext) = DeleteAllInteractor(coroutineContext, this)