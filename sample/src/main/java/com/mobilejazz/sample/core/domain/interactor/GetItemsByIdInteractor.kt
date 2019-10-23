package com.mobilejazz.sample.core.domain.interactor

import com.mobilejazz.harmony.kotlin.core.domain.interactor.GetInteractor
import com.mobilejazz.harmony.kotlin.core.repository.operation.CacheSyncOperation
import com.mobilejazz.harmony.kotlin.core.repository.query.IntegerIdQuery
import com.mobilejazz.harmony.kotlin.core.threading.DirectExecutor
import com.mobilejazz.harmony.kotlin.core.threading.Executor
import com.mobilejazz.harmony.kotlin.core.threading.extensions.Future
import com.mobilejazz.sample.core.domain.model.Item
import java.util.concurrent.Callable
import javax.inject.Inject

class GetItemsByIdInteractor @Inject constructor(private val executor: Executor,
                                                 private val getItemInteractor: GetInteractor<Item>) {

  operator fun invoke(ids: List<Int>, executor: Executor = this.executor): Future<List<Item>> {
    return executor.submit(Callable {
      return@Callable ids.map {
        getItemInteractor(IntegerIdQuery(it), operation = CacheSyncOperation(), executor = DirectExecutor).get()
      }
    })
  }
}