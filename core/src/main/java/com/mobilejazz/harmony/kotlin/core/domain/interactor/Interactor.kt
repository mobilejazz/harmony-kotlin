package com.mobilejazz.harmony.kotlin.core.domain.interactor

import com.mobilejazz.harmony.kotlin.core.repository.DeleteRepository
import com.mobilejazz.harmony.kotlin.core.repository.GetRepository
import com.mobilejazz.harmony.kotlin.core.repository.PutRepository
import com.mobilejazz.harmony.kotlin.core.repository.operation.DefaultOperation
import com.mobilejazz.harmony.kotlin.core.repository.operation.Operation
import com.mobilejazz.harmony.kotlin.core.repository.query.IdQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.IdsQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.repository.query.VoidQuery
import com.mobilejazz.harmony.kotlin.core.threading.AppExecutor
import com.mobilejazz.harmony.kotlin.core.threading.DirectExecutor
import com.mobilejazz.harmony.kotlin.core.threading.Executor
import com.mobilejazz.harmony.kotlin.core.threading.extensions.Future
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


fun <K, V> GetInteractor<V>.execute(id: K, operation: Operation = DefaultOperation, executor: Executor = DirectExecutor): Future<V> = this.invoke(IdQuery(id)
    , operation, executor)

fun <K, V> GetAllInteractor<V>.execute(ids: List<K>, operation: Operation = DefaultOperation, executor: Executor = DirectExecutor): Future<List<V>> = this
    .invoke(IdsQuery(ids), operation, executor)

fun <K, V> PutInteractor<V>.execute(id: K, value: V?, operation: Operation = DefaultOperation, executor: Executor = DirectExecutor): Future<V> = this.invoke(value,
    IdQuery
    (id),
    operation, executor)

fun <K, V> PutAllInteractor<V>.execute(ids: List<K>, values: List<V>? = emptyList(), operation: Operation = DefaultOperation, executor: Executor = DirectExecutor) = this.invoke(values, IdsQuery
(ids), operation, executor)

fun <K> DeleteInteractor.execute(id: K, operation: Operation = DefaultOperation, executor: Executor = DirectExecutor) = this.invoke(IdQuery(id), operation,
    executor)

fun <K> DeleteAllInteractor.execute(ids: List<K>, operation: Operation = DefaultOperation, executor: Executor = DirectExecutor) = this.invoke(IdsQuery(ids),
    operation, executor)


fun <V> GetRepository<V>.toGetInteractor(executor: Executor = AppExecutor) = GetInteractor(executor, this)

fun <V> GetRepository<V>.toGetAllInteractor(executor: Executor = AppExecutor) = GetAllInteractor(executor, this)

fun <V> PutRepository<V>.toPutInteractor(executor: Executor = AppExecutor) = PutInteractor(executor, this)

fun <V> PutRepository<V>.toPutAllInteractor(executor: Executor = AppExecutor) = PutAllInteractor(executor, this)

fun DeleteRepository.toDeleteInteractor(executor: Executor = AppExecutor) = DeleteInteractor(executor, this)

fun DeleteRepository.toDeleteAllInteractor(executor: Executor = AppExecutor) = DeleteAllInteractor(executor, this)