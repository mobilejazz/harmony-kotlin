package com.harmony.kotlin.domain.interactor

import com.harmony.kotlin.data.operation.DefaultOperation
import com.harmony.kotlin.data.operation.Operation
import com.harmony.kotlin.data.query.IdQuery
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

class PutInteractor<M>(private val coroutineContext: CoroutineContext, private val putRepository: PutRepository<M>) {

  suspend operator fun invoke(m: M? = null, query: Query = VoidQuery, operation: Operation = DefaultOperation): M =
    withContext(coroutineContext) {
      putRepository.put(query, m, operation)
    }
}

class DeleteInteractor(private val coroutineContext: CoroutineContext, private val deleteRepository: DeleteRepository) {

  suspend operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation) =
    withContext(coroutineContext) {
      deleteRepository.delete(query, operation)
    }
}

suspend fun <K, V> GetInteractor<V>.execute(id: K, operation: Operation = DefaultOperation): V = this.invoke(IdQuery(id), operation)

suspend fun <K, V> PutInteractor<V>.execute(id: K, value: V?, operation: Operation = DefaultOperation): V = this.invoke(value, IdQuery(id), operation)

suspend fun <K> DeleteInteractor.execute(id: K, operation: Operation = DefaultOperation) = this.invoke(IdQuery(id), operation)

fun <V> GetRepository<V>.toGetInteractor(coroutineContext: CoroutineContext) = GetInteractor(coroutineContext, this)

fun <V> PutRepository<V>.toPutInteractor(coroutineContext: CoroutineContext) = PutInteractor(coroutineContext, this)

fun DeleteRepository.toDeleteInteractor(coroutineContext: CoroutineContext) = DeleteInteractor(coroutineContext, this)
