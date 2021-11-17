@file:Suppress("IllegalIdentifier")

package com.harmony.kotlin.data.repository

import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.common.runTest
import com.harmony.kotlin.data.datasource.memory.InMemoryDataSource
import com.harmony.kotlin.data.error.OperationNotAllowedException
import com.harmony.kotlin.data.operation.*
import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.data.query.anyKeyQuery
import com.harmony.kotlin.data.query.anyVoidQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExperimentalCoroutinesApi
class CacheRepositoryTests {

  @Test
  fun `should retrieves the value from the main datasource when MainOperation is provided when get() is called`() = runTest {
    val expectedValue = randomString()
    val anyQuery = anyKeyQuery(randomString())
    val mainDataSource = givenInMemoryDataSource(listOf(InsertionValue(anyQuery, expectedValue)))
    val cacheRepository = givenCacheRepository(mainDataSource = mainDataSource)

    val value = cacheRepository.get(anyQuery, MainOperation)

    assertEquals(expectedValue, value)
  }

  @Test
  fun `should retrieves the value from the cache datasource when CacheOperation is provided when get() is called`() = runTest {
    val expectedValue = randomString()
    val anyQuery = anyKeyQuery(randomString())
    val cacheDataSource = givenInMemoryDataSource(listOf(InsertionValue(anyQuery, expectedValue)))
    val cacheRepository = givenCacheRepository(cacheDataSource = cacheDataSource)

    val value = cacheRepository.get(anyQuery, CacheOperation())

    assertEquals(expectedValue, value)
  }

  @Test
  fun `should throw OperationNotAllowedException exception if invalid operation is provided when get() is called`() = runTest {
    assertFailsWith<OperationNotAllowedException> {
      val cacheRepository = givenCacheRepository()
      val invalidOperation = anyOperation()

      cacheRepository.get(anyVoidQuery(), invalidOperation)
    }
  }

  @Test
  fun `should store the response from the main datasource into the cache datasource when MainSyncOperation is provided when get() is called`() = runTest {
    val expectedValue = randomString()
    val anyQuery = anyKeyQuery(randomString())
    val insertionValues = listOf(InsertionValue(anyQuery, expectedValue))
    val mainDataSource = givenInMemoryDataSource(insertionValues)
    val cacheDataSource = givenInMemoryDataSource()
    val cacheRepository = givenCacheRepository(mainDataSource, cacheDataSource)

    val value = cacheRepository.get(anyQuery, MainSyncOperation)

    assertEquals(expectedValue, value)

    val expectedValueInCache = cacheDataSource.get(anyQuery)
    assertEquals(expectedValue, expectedValueInCache)
  }

  data class InsertionValue(val query: KeyQuery, val value: String)

  private fun givenInMemoryDataSource(insertionValues: List<InsertionValue> = emptyList()): InMemoryDataSource<String> {
    val inMemoryDataSource = InMemoryDataSource<String>()

    runTest {
      insertionValues.forEach {
        inMemoryDataSource.put(it.query, it.value)
      }
    }

    return inMemoryDataSource
  }

  private fun givenCacheRepository(
    mainDataSource: InMemoryDataSource<String> = givenInMemoryDataSource(),
    cacheDataSource: InMemoryDataSource<String> = givenInMemoryDataSource(),
  ): CacheRepository<String> {
    val validator = CacheRepository.DefaultValidator<String>()

    return CacheRepository(
      cacheDataSource, cacheDataSource, cacheDataSource,
      mainDataSource, mainDataSource, mainDataSource,
      validator
    )
  }
}
