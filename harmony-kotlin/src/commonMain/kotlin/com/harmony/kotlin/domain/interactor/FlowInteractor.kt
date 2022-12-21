package com.harmony.kotlin.domain.interactor

import com.harmony.kotlin.data.operation.DefaultOperation
import com.harmony.kotlin.data.operation.Operation
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.query.VoidQuery
import com.harmony.kotlin.data.repository.flow.FlowDeleteRepository
import com.harmony.kotlin.data.repository.flow.FlowGetRepository
import com.harmony.kotlin.data.repository.flow.FlowPutRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class FlowGetInteractor<M>(private val scope: CoroutineScope, private val getRepository: FlowGetRepository<M>) {

  operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation): Flow<M> = getRepository.get(query, operation)
}

@Deprecated(message = "Use FlowGetInteractor instead")
class FlowGetAllInteractor<M>(private val scope: CoroutineScope, private val getRepository: FlowGetRepository<M>) {

  operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation): Flow<List<M>> = getRepository.getAll(query, operation)
}

class FlowPutInteractor<M>(private val scope: CoroutineScope, private val putRepository: FlowPutRepository<M>) {

  operator fun invoke(m: M?, query: Query = VoidQuery, operation: Operation = DefaultOperation): Flow<M> = putRepository.put(query, m, operation)
}

@Deprecated(message = "Use FlowPutInteractor instead")
class FlowPutAllInteractor<M>(private val scope: CoroutineScope, private val putRepository: FlowPutRepository<M>) {

  operator fun invoke(m: List<M>?, query: Query = VoidQuery, operation: Operation = DefaultOperation): Flow<List<M>> = putRepository.putAll(
    query, m,
    operation
  )
}

class FlowDeleteInteractor(private val scope: CoroutineScope, private val deleteRepository: FlowDeleteRepository) {

  operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation) = deleteRepository.delete(query, operation)
}

//region Creation
fun <V> FlowGetRepository<V>.toFlowGetInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = FlowGetInteractor(scope, this)

@Deprecated(message = "Replaced by toFlowGetInteractor")
fun <V> FlowGetRepository<V>.toFlowGetAllInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = FlowGetAllInteractor(scope, this)

fun <V> FlowPutRepository<V>.toFlowPutInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = FlowPutInteractor(scope, this)

@Deprecated(message = "Replaced by toFlowPutInteractor")
fun <V> FlowPutRepository<V>.toFlowPutAllInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = FlowPutAllInteractor(scope, this)

fun FlowDeleteRepository.toFlowDeleteInteractor(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) = FlowDeleteInteractor(scope, this)
//endregion
