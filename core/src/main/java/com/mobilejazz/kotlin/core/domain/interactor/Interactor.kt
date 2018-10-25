package com.mobilejazz.kotlin.core.domain.interactor

import com.mobilejazz.kotlin.core.repository.DeleteRepository
import com.mobilejazz.kotlin.core.repository.GetRepository
import com.mobilejazz.kotlin.core.repository.PutRepository
import com.mobilejazz.kotlin.core.repository.operation.DefaultOperation
import com.mobilejazz.kotlin.core.repository.operation.Operation
import com.mobilejazz.kotlin.core.repository.query.IdQuery
import com.mobilejazz.kotlin.core.repository.query.IdsQuery
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.repository.query.VoidQuery
import com.mobilejazz.kotlin.core.threading.Executor
import com.mobilejazz.kotlin.core.threading.extensions.Future
import java.util.concurrent.Callable
import javax.inject.Inject

class GetInteractor<M> @Inject constructor(private val executor: Executor, private val getRepository: GetRepository<M>) {

  operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation, executor: Executor = this.executor): Future<M> =
      executor.submit(Callable {
        getRepository.get(query, operation).get()
      })
}

class GetAllInteractor<M> @Inject constructor(private val executor: Executor, private val getRepository: GetRepository<M>) {

  operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation, executor: Executor = this.executor): Future<List<M>> =
      executor.submit(Callable {
        getRepository.getAll(query, operation).get()
      })
}

class PutInteractor<M> @Inject constructor(private val executor: Executor, private val putRepository: PutRepository<M>) {

  operator fun invoke(m: M?, query: Query = VoidQuery, operation: Operation = DefaultOperation, executor: Executor = this.executor): Future<M> =
      executor.submit(Callable {
        putRepository.put(query, m, operation).get()
      })
}

class PutAllInteractor<M> @Inject constructor(private val executor: Executor, private val putRepository: PutRepository<M>) {

  operator fun invoke(m: List<M>?, query: Query = VoidQuery, operation: Operation = DefaultOperation, executor: Executor = this.executor): Future<List<M>> =
      executor.submit(Callable {
        putRepository.putAll(query, m, operation).get()
      })
}

class DeleteInteractor @Inject constructor(private val executor: Executor, private val deleteRepository: DeleteRepository) {

  operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation, executor: Executor = this.executor): Future<Unit> =
      executor.submit(Callable {
        deleteRepository.delete(query, operation).get()
      })
}

class DeleteAllInteractor @Inject constructor(private val executor: Executor, private val deleteRepository: DeleteRepository) {

  operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation, executor: Executor = this.executor): Future<Unit> =
      executor.submit(Callable {
        deleteRepository.deleteAll(query, operation).get()
      })
}


fun <K, V> GetInteractor<V>.execute(id: K, operation: Operation = DefaultOperation): Future<V> = this.invoke(IdQuery(id), operation)

fun <K, V> GetAllInteractor<V>.execute(ids: List<K>, operation: Operation = DefaultOperation): Future<List<V>> = this.invoke(IdsQuery(ids), operation)

fun <K, V> PutInteractor<V>.execute(id: K, value: V?, operation: Operation = DefaultOperation): Future<V> = this.invoke(value, IdQuery(id), operation)

fun <K, V> PutAllInteractor<V>.execute(ids: List<K>, values: List<V>? = emptyList(), operation: Operation = DefaultOperation) = this.invoke(values, IdsQuery(ids), operation)

fun <K> DeleteInteractor.execute(id: K, operation: Operation = DefaultOperation) = this.invoke(IdQuery(id), operation)

fun <K> DeleteAllInteractor.execute(ids: List<K>, operation: Operation = DefaultOperation) = this.invoke(IdsQuery(ids), operation)