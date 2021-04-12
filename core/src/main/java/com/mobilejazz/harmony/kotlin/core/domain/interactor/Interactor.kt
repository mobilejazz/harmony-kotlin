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

interface GetInteractor<M> {
  operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation, executor: Executor? = null): Future<M>
}

class DefaultGetInteractor<M> @Inject constructor(private val executor: Executor, private val getRepository: GetRepository<M>) : GetInteractor<M> {

  override operator fun invoke(query: Query, operation: Operation, executor: Executor?): Future<M> {
    val exec = executor ?: this.executor
    return exec.submit(Callable {
      getRepository.get(query, operation).get()
    })
  }
}


interface GetAllInteractor<M> {
  operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation, executor: Executor? = null): Future<List<M>>
}

class DefaultGetAllInteractor<M> @Inject constructor(private val executor: Executor, private val getRepository: GetRepository<M>) : GetAllInteractor<M> {

  override operator fun invoke(query: Query, operation: Operation, executor: Executor?): Future<List<M>> {
    val exec = executor ?: this.executor
    return exec.submit(Callable {
      getRepository.getAll(query, operation).get()
    })
  }
}


interface PutInteractor<M> {
  operator fun invoke(m: M?, query: Query = VoidQuery, operation: Operation = DefaultOperation, executor: Executor? = null): Future<M>
}

class DefaultPutInteractor<M> @Inject constructor(private val executor: Executor, private val putRepository: PutRepository<M>) : PutInteractor<M> {

  override operator fun invoke(m: M?, query: Query, operation: Operation, executor: Executor?): Future<M> {
    val exec = executor ?: this.executor
    return exec.submit(Callable {
      putRepository.put(query, m, operation).get()
    })
  }
}


interface PutAllInteractor<M> {
  operator fun invoke(m: List<M>?, query: Query = VoidQuery, operation: Operation = DefaultOperation, executor: Executor? = null): Future<List<M>>
}

class DefaultPutAllInteractor<M> @Inject constructor(private val executor: Executor, private val putRepository: PutRepository<M>) : PutAllInteractor<M> {

  override operator fun invoke(m: List<M>?, query: Query, operation: Operation, executor: Executor?): Future<List<M>> {
    val exec = executor ?: this.executor
    return exec.submit(Callable {
      putRepository.putAll(query, m, operation).get()
    })
  }
}


interface DeleteInteractor {
  operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation, executor: Executor? = null): Future<Unit>
}

class DefaultDeleteInteractor @Inject constructor(private val executor: Executor, private val deleteRepository: DeleteRepository) : DeleteInteractor {

  override operator fun invoke(query: Query, operation: Operation, executor: Executor?): Future<Unit> {
    val exec = executor ?: this.executor
    return exec.submit(Callable {
      deleteRepository.delete(query, operation).get()
    })
  }
}


interface DeleteAllInteractor {
  operator fun invoke(query: Query = VoidQuery, operation: Operation = DefaultOperation, executor: Executor? = null): Future<Unit>
}

class DefaultDeleteAllInteractor @Inject constructor(private val executor: Executor, private val deleteRepository: DeleteRepository) : DeleteAllInteractor {

  override operator fun invoke(query: Query, operation: Operation, executor: Executor?): Future<Unit> {
    val exec = executor ?: this.executor
    return exec.submit(Callable {
      deleteRepository.deleteAll(query, operation).get()
    })
  }
}


fun <K, V> GetInteractor<V>.execute(id: K, operation: Operation = DefaultOperation, executor: Executor = DirectExecutor): Future<V> = this.invoke(IdQuery(id),
    operation,
    executor)

fun <K, V> GetAllInteractor<V>.execute(ids: List<K>, operation: Operation = DefaultOperation, executor: Executor = DirectExecutor): Future<List<V>> = this
    .invoke(IdsQuery(ids), operation, executor)

fun <K, V> PutInteractor<V>.execute(id: K, value: V?, operation: Operation = DefaultOperation, executor: Executor = DirectExecutor): Future<V> = this.invoke(value,
    IdQuery
    (id),
    operation, executor)

fun <K, V> PutAllInteractor<V>.execute(ids: List<K>, values: List<V>? = emptyList(), operation: Operation = DefaultOperation, executor: Executor = DirectExecutor) = this
    .invoke(values, IdsQuery
    (ids), operation, executor)

fun <K> DeleteInteractor.execute(id: K, operation: Operation = DefaultOperation, executor: Executor = DirectExecutor) = this.invoke(IdQuery(id), operation,
    executor)

fun <K> DeleteAllInteractor.execute(ids: List<K>, operation: Operation = DefaultOperation, executor: Executor = DirectExecutor) = this.invoke(IdsQuery(ids),
    operation, executor)


fun <V> GetRepository<V>.toGetInteractor(executor: Executor = AppExecutor) = DefaultGetInteractor(executor, this)

fun <V> GetRepository<V>.toGetAllInteractor(executor: Executor = AppExecutor) = DefaultGetAllInteractor(executor, this)

fun <V> PutRepository<V>.toPutInteractor(executor: Executor = AppExecutor) = DefaultPutInteractor(executor, this)

fun <V> PutRepository<V>.toPutAllInteractor(executor: Executor = AppExecutor) = DefaultPutAllInteractor(executor, this)

fun DeleteRepository.toDeleteInteractor(executor: Executor = AppExecutor) = DefaultDeleteInteractor(executor, this)

fun DeleteRepository.toDeleteAllInteractor(executor: Executor = AppExecutor) = DefaultDeleteAllInteractor(executor, this)