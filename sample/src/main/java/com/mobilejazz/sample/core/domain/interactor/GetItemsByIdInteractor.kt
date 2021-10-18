package com.mobilejazz.sample.core.domain.interactor

import android.util.Log
import com.harmony.kotlin.data.operation.CacheSyncOperation
import com.harmony.kotlin.domain.interactor.GetInteractor
import com.harmony.kotlin.data.query.IntegerIdQuery
import com.mobilejazz.sample.core.domain.model.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetItemsByIdInteractor @Inject constructor(private val scope: CoroutineScope, private val getItemInteractor: GetInteractor<Item>) {

  suspend operator fun invoke(ids: List<Int>): List<Item> {
    return withContext(scope.coroutineContext) {
      val startTime = System.currentTimeMillis()

      val deferred = ids.map { async { getItemInteractor(IntegerIdQuery(it), operation = CacheSyncOperation()) } }
      val values = deferred.awaitAll()

      val endTime = System.currentTimeMillis()
      Log.d("JOSE_THREAD", "Time taken: ${endTime - startTime}")

      return@withContext values
//      val values = ids.map { getItemInteractor(IntegerIdQuery(it), operation = CacheSyncOperation) }
//
//      val endTime = System.currentTimeMillis()
//      Log.d("JOSE_THREAD", "Time taken: ${endTime - startTime}")
//
//      return@withContext values
    }
  }
}
