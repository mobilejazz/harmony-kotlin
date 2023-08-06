package com.harmony.kotlin.domain.interactor.either

import com.harmony.kotlin.common.either.Either
import com.harmony.kotlin.common.either.eitherOf
import com.harmony.kotlin.data.operation.DefaultOperation
import com.harmony.kotlin.data.operation.Operation
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.query.VoidQuery
import com.harmony.kotlin.data.repository.DeleteRepository
import com.harmony.kotlin.data.repository.GetRepository
import com.harmony.kotlin.data.repository.PutRepository
import com.harmony.kotlin.error.HarmonyException
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetInteractor<M>(val coroutineContext: CoroutineContext, val getRepository: GetRepository<M>) {

  suspend inline operator fun <reified E : HarmonyException> invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation): Either<E, M> =
    withContext(coroutineContext) {
      eitherOf { getRepository.get(query, operation) }
    }
}

class PutInteractor<M>(val coroutineContext: CoroutineContext, val putRepository: PutRepository<M>) {

  suspend inline operator fun <reified E : HarmonyException> invoke(
    m: M? = null,
    query: Query = VoidQuery,
    operation: Operation = DefaultOperation
  ): Either<E, M> =
    withContext(coroutineContext) {
      eitherOf { putRepository.put(query, m, operation) }
    }
}

class DeleteInteractor(val coroutineContext: CoroutineContext, val deleteRepository: DeleteRepository) {

  suspend inline operator fun <reified E : HarmonyException> invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation): Either<E, Unit> =
    withContext(coroutineContext) {
      eitherOf { deleteRepository.delete(query, operation) }
    }
}

fun <V> GetRepository<V>.toGetInteractor(coroutineContext: CoroutineContext) = GetInteractor(coroutineContext, this)

fun <V> PutRepository<V>.toPutInteractor(coroutineContext: CoroutineContext) = PutInteractor(coroutineContext, this)

fun <E> DeleteRepository.toDeleteInteractor(coroutineContext: CoroutineContext) = DeleteInteractor(coroutineContext, this)
