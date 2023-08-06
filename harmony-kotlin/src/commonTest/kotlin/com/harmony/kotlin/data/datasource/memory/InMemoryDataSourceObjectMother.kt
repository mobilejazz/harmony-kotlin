package com.harmony.kotlin.data.datasource.memory

import com.harmony.kotlin.data.utilities.InsertionValue
import com.harmony.kotlin.data.utilities.InsertionValues

suspend fun <T> anyInMemoryDataSourceLegacy(
  putValues: List<InsertionValue<T>> = emptyList(),
): InMemoryDataSource<T> {
  val inMemoryDataSource = InMemoryDataSource<T>()

  putValues.forEach {
    inMemoryDataSource.put(it.query, it.value)
  }

  return inMemoryDataSource
}

suspend fun <T> anyInMemoryDataSource(
  putValues: List<InsertionValue<T>> = emptyList()
): InMemoryDataSource<T> {
  val inMemoryDataSource = InMemoryDataSource<T>()

  putValues.forEach {
    inMemoryDataSource.put(it.query, it.value)
  }

  return inMemoryDataSource
}

suspend fun <T> anyInMemoryDataSourceList(
  putValues: List<InsertionValues<T>> = emptyList()
): InMemoryDataSource<List<T>> {
  val inMemoryDataSource = InMemoryDataSource<List<T>>()

  putValues.forEach {
    inMemoryDataSource.put(it.query, it.value)
  }

  return inMemoryDataSource
}
