package com.mobilejazz.harmony.kotlin.core.domain.interactor

import com.mobilejazz.harmony.kotlin.core.repository.flow.FlowDeleteRepository
import com.mobilejazz.harmony.kotlin.core.repository.flow.FlowGetRepository
import com.mobilejazz.harmony.kotlin.core.repository.flow.FlowPutRepository
import com.mobilejazz.harmony.kotlin.core.repository.operation.DefaultOperation
import com.mobilejazz.harmony.kotlin.core.repository.operation.Operation
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.repository.query.VoidQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FlowGetInteractor<M> @Inject constructor(val scope: CoroutineScope, val getRepository: FlowGetRepository<M>) {

  operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation): Flow<M> = getRepository.get(query, operation)
}

class FlowGetAllInteractor<M> @Inject constructor(private val scope: CoroutineScope, private val getRepository: FlowGetRepository<M>) {

  operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation): Flow<List<M>> = getRepository.getAll(query, operation)
}

class FlowPutInteractor<M> @Inject constructor(private val scope: CoroutineScope, private val putRepository: FlowPutRepository<M>) {

  operator fun invoke(m: M?, query: Query = VoidQuery, operation: Operation = DefaultOperation): Flow<M> = putRepository.put(query, m, operation)

}

class FlowPutAllInteractor<M> @Inject constructor(private val scope: CoroutineScope, private val putRepository: FlowPutRepository<M>) {

  operator fun invoke(m: List<M>?, query: Query = VoidQuery, operation: Operation = DefaultOperation): Flow<List<M>> = putRepository.putAll(query, m,
      operation)
}

class FlowDeleteInteractor @Inject constructor(private val scope: CoroutineScope, private val deleteRepository: FlowDeleteRepository) {

  operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation) = deleteRepository.delete(query, operation)
}

class FlowDeleteAllInteractor @Inject constructor(private val scope: CoroutineScope, private val deleteRepository: FlowDeleteRepository) {

  operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation) = deleteRepository.deleteAll(query, operation)
}


//region Creation
fun <V> FlowGetRepository<V>.toFlowGetInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = FlowGetInteractor(scope, this)

fun <V> FlowGetRepository<V>.toFlowGetAllInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = FlowGetAllInteractor(scope, this)

fun <V> FlowPutRepository<V>.toFlowPutInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = FlowPutInteractor(scope, this)

fun <V> FlowPutRepository<V>.toFlowPutAllInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = FlowPutAllInteractor(scope, this)

fun FlowDeleteRepository.toFlowDeleteInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = FlowDeleteInteractor(scope, this)

fun FlowDeleteRepository.toFlowDeleteAllInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = FlowDeleteAllInteractor(scope, this)
//endregion