package com.mobilejazz.kotlin.core.domain.interactor

import com.mobilejazz.kotlin.core.repository.*
import com.mobilejazz.kotlin.core.repository.operation.DefaultOperation
import com.mobilejazz.kotlin.core.repository.operation.Operation
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.threading.AppExecutor
import com.mobilejazz.kotlin.core.threading.Future
import java.util.concurrent.Callable

class GetObjectInteractor<M> constructor(private val executor: AppExecutor, private val getRepository: GetRepository<M>) {

    operator fun invoke(query: Query, operation: Operation = DefaultOperation()): Future<M> = executor.submit(Callable {
        getRepository.get(query, operation).get()
    })
}

class GetAllObjectsInteractor<M> constructor(private val executor: AppExecutor, private val getRepository: GetRepository<M>) {

    operator fun invoke(query: Query, operation: Operation = DefaultOperation()): Future<List<M>> = executor.submit(Callable {
        getRepository.getAll(query, operation).get()
    })
}

class PutObjectInteractor<M> constructor(private val executor: AppExecutor, private val putRepository: PutRepository<M>) {

    operator fun invoke(m: M, query: Query, operation: Operation = DefaultOperation()): Future<M> = executor.submit(Callable {
        putRepository.put(query, m, operation).get()
    })
}

class PutAllObjectsInteractor<M> constructor(private val executor: AppExecutor, private val putRepository: PutRepository<M>) {

    operator fun invoke(m: List<M>, query: Query, operation: Operation = DefaultOperation()): Future<List<M>> = executor.submit(Callable {
        putRepository.putAll(query, m, operation).get()
    })
}

class DeleteObjectInteractor constructor(private val executor: AppExecutor, private val deleteRepository: DeleteRepository) {

    operator fun invoke(query: Query, operation: Operation = DefaultOperation()): Future<Void> = executor.submit(Callable {
        deleteRepository.delete(query, operation).get()
    })
}

class DeleteAllObjectsInteractor constructor(private val executor: AppExecutor, private val deleteRepository: DeleteRepository) {

    operator fun invoke(query: Query, operation: Operation = DefaultOperation()): Future<Void> = executor.submit(Callable {
        deleteRepository.deleteAll(query, operation).get()
    })
}