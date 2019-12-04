package com.mobilejazz.harmony.kotlin.core.domain.interactor

import com.harmony.kotlin.data.operation.DefaultOperation
import com.harmony.kotlin.data.operation.Operation
import com.harmony.kotlin.data.query.IdQuery
import com.harmony.kotlin.data.query.IdsQuery
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.query.VoidQuery
import com.harmony.kotlin.data.repository.DeleteRepository
import com.harmony.kotlin.data.repository.GetRepository
import com.harmony.kotlin.data.repository.PutRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetInteractor<M>(private val scope: CoroutineScope, private val getRepository: GetRepository<M>) {

  suspend operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation): M =
      withContext(scope.coroutineContext) {
        getRepository.get(query, operation)
      }
}

class GetAllInteractor<M>(private val scope: CoroutineScope, private val getRepository: GetRepository<M>) {

  suspend operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation): List<M> =
      withContext(scope.coroutineContext) {
        getRepository.getAll(query, operation)
      }
}

class PutInteractor<M>(private val scope: CoroutineScope, private val putRepository: PutRepository<M>) {

  suspend operator fun invoke(m: M?, query: Query = VoidQuery, operation: Operation = DefaultOperation): M =
      withContext(scope.coroutineContext) {
        putRepository.put(query, m, operation)
      }
}

class PutAllInteractor<M>(private val scope: CoroutineScope, private val putRepository: PutRepository<M>) {

  suspend operator fun invoke(m: List<M>?, query: Query = VoidQuery, operation: Operation = DefaultOperation): List<M> =
      withContext(scope.coroutineContext) {
        putRepository.putAll(query, m, operation)
      }
}

class DeleteInteractor(private val scope: CoroutineScope, private val deleteRepository: DeleteRepository) {

  suspend operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation) =
      withContext(scope.coroutineContext) {
        deleteRepository.delete(query, operation)
      }
}

class DeleteAllInteractor(private val scope: CoroutineScope, private val deleteRepository: DeleteRepository) {

  suspend operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation) =
      withContext(scope.coroutineContext) {
        deleteRepository.deleteAll(query, operation)
      }
}


suspend fun <K, V> GetInteractor<V>.execute(id: K, operation: Operation = DefaultOperation): V = this.invoke(IdQuery(id), operation)

suspend fun <K, V> GetAllInteractor<V>.execute(ids: List<K>, operation: Operation = DefaultOperation): List<V> = this.invoke(IdsQuery(ids), operation)

suspend fun <K, V> PutInteractor<V>.execute(id: K, value: V?, operation: Operation = DefaultOperation): V = this.invoke(value, IdQuery(id), operation)

suspend fun <K, V> PutAllInteractor<V>.execute(ids: List<K>, values: List<V>? = emptyList(), operation: Operation = DefaultOperation) = this.invoke(values, IdsQuery(ids), operation)

suspend fun <K> DeleteInteractor.execute(id: K, operation: Operation = DefaultOperation) = this.invoke(IdQuery(id), operation)

suspend fun <K> DeleteAllInteractor.execute(ids: List<K>, operation: Operation = DefaultOperation) = this.invoke(IdsQuery(ids), operation)


fun <V> GetRepository<V>.toGetInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = GetInteractor(scope, this)

fun <V> GetRepository<V>.toGetAllInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = GetAllInteractor(scope, this)

fun <V> PutRepository<V>.toPutInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = PutInteractor(scope, this)

fun <V> PutRepository<V>.toPutAllInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = PutAllInteractor(scope, this)

fun DeleteRepository.toDeleteInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = DeleteInteractor(scope, this)

fun DeleteRepository.toDeleteAllInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = DeleteAllInteractor(scope, this)