package com.harmony.kotlin.data.datasource.memory

import com.harmony.kotlin.common.runTest
import com.harmony.kotlin.data.utilities.InsertionValue
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> anyInMemoryDataSource(insertionValues: List<InsertionValue<T>> = emptyList()): InMemoryDataSource<T> {
  val inMemoryDataSource = InMemoryDataSource<T>()

  runTest {
    insertionValues.forEach {
      inMemoryDataSource.put(it.query, it.value)
    }
  }

  return inMemoryDataSource
}
