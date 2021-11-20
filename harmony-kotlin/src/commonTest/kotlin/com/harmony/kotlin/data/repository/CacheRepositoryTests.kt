@file:Suppress("IllegalIdentifier")

package com.harmony.kotlin.data.repository

import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.common.runTest
import com.harmony.kotlin.data.datasource.memory.InMemoryDataSource
import com.harmony.kotlin.data.datasource.memory.anyInMemoryDataSource
import com.harmony.kotlin.data.error.ObjectNotValidException
import com.harmony.kotlin.data.error.OperationNotAllowedException
import com.harmony.kotlin.data.operation.CacheOperation
import com.harmony.kotlin.data.operation.MainOperation
import com.harmony.kotlin.data.operation.MainSyncOperation
import com.harmony.kotlin.data.operation.anyOperation
import com.harmony.kotlin.data.query.anyKeyQuery
import com.harmony.kotlin.data.query.anyVoidQuery
import com.harmony.kotlin.data.utilities.InsertionValue
import com.harmony.kotlin.data.utilities.anyInsertionValue
import com.harmony.kotlin.data.validator.Validator
import com.harmony.kotlin.data.validator.anyMockValidator
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
    val mainDataSource = anyInMemoryDataSource(listOf(InsertionValue(anyQuery, expectedValue)))
    val cacheRepository = givenCacheRepository(mainDataSource = mainDataSource)

    val value = cacheRepository.get(anyQuery, MainOperation)

    assertEquals(expectedValue, value)
  }

  @Test
  fun `should retrieves the value from the cache datasource when CacheOperation is provided when get() is called`() = runTest {
    val expectedValue = randomString()
    val anyQuery = anyKeyQuery(randomString())
    val cacheDataSource = anyInMemoryDataSource(listOf(InsertionValue(anyQuery, expectedValue)))
    val cacheRepository = givenCacheRepository(cacheDataSource = cacheDataSource)

    val value = cacheRepository.get(anyQuery, CacheOperation())

    assertEquals(expectedValue, value)
  }

  @Test
  fun `should throw OperationNotAllowedException exception if invalid operation is provided when get() is called`() = runTest {
    assertFailsWith<OperationNotAllowedException> {
      val cacheRepository = givenCacheRepository<String>()
      val invalidOperation = anyOperation()

      cacheRepository.get(anyVoidQuery(), invalidOperation)
    }
  }

  @Test
  fun `should store the response from the main datasource into the cache datasource when MainSyncOperation is provided when get() is called`() = runTest {
    val expectedValue = randomString()
    val anyQuery = anyKeyQuery(randomString())
    val insertionValues = listOf(InsertionValue(anyQuery, expectedValue))
    val mainDataSource = anyInMemoryDataSource(insertionValues)
    val cacheDataSource = anyInMemoryDataSource<String>()
    val cacheRepository = givenCacheRepository(mainDataSource, cacheDataSource)

    val value = cacheRepository.get(anyQuery, MainSyncOperation)

    assertEquals(expectedValue, value)

    val expectedValueInCache = cacheDataSource.get(anyQuery)
    assertEquals(expectedValue, expectedValueInCache)
  }

  @Test
  fun `should response the value from the cache when CacheOperation is provided and there is values within the cache when get() is called`() = runTest {
    val expectedInsertionValue = anyInsertionValue()
    val cacheDataSource = anyInMemoryDataSource(listOf(expectedInsertionValue))
    val cacheRepository = givenCacheRepository(cacheDataSource = cacheDataSource)

    val value = cacheRepository.get(expectedInsertionValue.query, CacheOperation())

    assertEquals(expectedInsertionValue.value, value)
  }

  @Test
  fun `should throw ObjectNotValidException when the object is not valid from the cache when get() is called`() = runTest {
    assertFailsWith<ObjectNotValidException> {
      val anyInsertionValue = anyInsertionValue()
      val cacheDataSource = anyInMemoryDataSource(listOf(anyInsertionValue))
      val validator = anyMockValidator<String>(validatorResponse = false)
      val cacheRepository = givenCacheRepository(cacheDataSource = cacheDataSource, validator = validator)

      cacheRepository.get(anyInsertionValue.query, CacheOperation())
    }
  }

  @Test
  fun `should response the value from the cache when is invalid and CacheOperation fallback is set to true when get() called`() = runTest {
    val expectedInsertionValue = anyInsertionValue()

    val cacheDataSource = anyInMemoryDataSource(listOf(expectedInsertionValue))

    val validator = anyMockValidator<String>(validatorResponse = false)
    val cacheRepository = givenCacheRepository(cacheDataSource = cacheDataSource, validator = validator)

    val cacheOperation = CacheOperation(fallback = { return@CacheOperation true })
    val value = cacheRepository.get(expectedInsertionValue.query, cacheOperation)

    assertEquals(expectedInsertionValue.value, value)
  }

  private fun <T> givenCacheRepository(
    mainDataSource: InMemoryDataSource<T> = anyInMemoryDataSource(),
    cacheDataSource: InMemoryDataSource<T> = anyInMemoryDataSource(),
    validator: Validator<T> = anyMockValidator()
  ): CacheRepository<T> {
    return CacheRepository(
      cacheDataSource, cacheDataSource, cacheDataSource,
      mainDataSource, mainDataSource, mainDataSource,
      validator
    )
  }
}
