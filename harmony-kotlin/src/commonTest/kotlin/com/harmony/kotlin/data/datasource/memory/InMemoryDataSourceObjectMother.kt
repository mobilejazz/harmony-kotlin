package com.harmony.kotlin.data.datasource.memory

import com.harmony.kotlin.common.runTest
import com.harmony.kotlin.data.utilities.InsertionValue
import com.harmony.kotlin.data.utilities.InsertionValues
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> anyInMemoryDataSource(
  putValues: List<InsertionValue<T>> = emptyList(),
  putAllValues: List<InsertionValues<T>> = emptyList()
): InMemoryDataSource<T> {
  val inMemoryDataSource = InMemoryDataSource<T>()

  runTest {
    putValues.forEach {
      inMemoryDataSource.put(it.query, it.value)
    }

    putAllValues.forEach {
      inMemoryDataSource.putAll(it.query, it.value)
    }
  }

  return inMemoryDataSource
}
