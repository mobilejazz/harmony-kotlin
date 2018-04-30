package com.mobilejazz.kotlin.core.sample.domain.items

import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.ListeningExecutorService
import com.mobilejazz.kotlin.core.sample.app.di.ActivityScope
import com.mobilejazz.kotlin.core.sample.domain.model.Item
import com.mobilejazz.kotlin.core.threading.AppExecutor
import com.mobilejazz.kotlin.core.threading.extensions.toListenableFuture
import java.math.BigDecimal
import javax.inject.Inject

@ActivityScope
class GetItemsInteractor @Inject constructor(private val executor: AppExecutor) {

  operator fun invoke(executor: ListeningExecutorService = this.executor): ListenableFuture<List<Item>> {
    return listOf(
        Item("1", "bla bla", BigDecimal.TEN, 2, "http://lorempixel.com/400/200"),
        Item("2", "bla bla 2", BigDecimal.TEN, 3, "http://lorempixel.com/400/200"))
        .toListenableFuture()
  }

}