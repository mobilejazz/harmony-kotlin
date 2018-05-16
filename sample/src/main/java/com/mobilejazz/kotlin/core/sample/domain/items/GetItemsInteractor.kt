package com.mobilejazz.kotlin.core.sample.domain.items

import com.google.common.util.concurrent.ListeningExecutorService
import com.mobilejazz.kotlin.core.repository.GetRepository
import com.mobilejazz.kotlin.core.sample.app.di.ActivityScope
import com.mobilejazz.kotlin.core.sample.domain.model.Item
import com.mobilejazz.kotlin.core.threading.AppExecutor
import com.mobilejazz.kotlin.core.threading.extensions.Future
import com.mobilejazz.kotlin.core.threading.extensions.toFuture
import javax.inject.Inject

@ActivityScope
class GetItemsInteractor @Inject constructor(
    private val executor: AppExecutor,
    val itemRepository: GetRepository<Item>
) {

  operator fun invoke(executor: AppExecutor = this.executor): Future<List<Item>> {
    return listOf(
        Item("1", "bla bla", 1.0, 2, "http://lorempixel.com/400/200"),
        Item("2", "bla bla 2", 2.0, 3, "http://lorempixel.com/400/200"))
        .toFuture()
  }

}