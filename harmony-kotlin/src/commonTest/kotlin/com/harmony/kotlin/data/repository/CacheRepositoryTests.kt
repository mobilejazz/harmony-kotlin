@file:Suppress("IllegalIdentifier")

package com.harmony.kotlin.data.repository

import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.common.runTest
import com.harmony.kotlin.data.datasource.memory.InMemoryDataSource
import com.harmony.kotlin.data.datasource.memory.anyInMemoryDataSource
import com.harmony.kotlin.data.error.DataNotFoundException
import com.harmony.kotlin.data.error.ObjectNotValidException
import com.harmony.kotlin.data.error.OperationNotAllowedException
import com.harmony.kotlin.data.operation.CacheOperation
import com.harmony.kotlin.data.operation.CacheSyncOperation
import com.harmony.kotlin.data.operation.DefaultOperation
import com.harmony.kotlin.data.operation.MainOperation
import com.harmony.kotlin.data.operation.MainSyncOperation
import com.harmony.kotlin.data.operation.anyOperation
import com.harmony.kotlin.data.query.anyKeyQuery
import com.harmony.kotlin.data.query.anyQuery
import com.harmony.kotlin.data.query.anyVoidQuery
import com.harmony.kotlin.data.utilities.InsertionValue
import com.harmony.kotlin.data.utilities.InsertionValues
import com.harmony.kotlin.data.utilities.anyInsertionValue
import com.harmony.kotlin.data.utilities.anyInsertionValues
import com.harmony.kotlin.data.validator.Validator
import com.harmony.kotlin.data.validator.anyMockValidator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExperimentalCoroutinesApi
class CacheRepositoryTests {

  //region get() - tests

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
  fun `should throw operation not allowed using get()`() = runTest {
    val cacheRepository = givenCacheRepository<String>()
    assertFailsWith<OperationNotAllowedException> {
      cacheRepository.get(anyQuery(), anyOperation())
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
  //endregion

  //region getAll() - tests

  @Test
  fun `should retrieves the value from the main datasource when MainOperation is provided when getAll() is called`() = runTest {
    val expectedValues = anyInsertionValues()
    val mainDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
    val cacheRepository = givenCacheRepository(mainDataSource = mainDataSource)

    val value = cacheRepository.getAll(expectedValues.query, MainOperation)

    assertEquals(expectedValues.value.size, value.size)
    assertContentEquals(expectedValues.value, value)
  }

  @Test
  fun `should retrieves the value from the cache datasource when CacheOperation is provided when getAll() is called`() = runTest {
    val expectedValues = anyInsertionValues()
    val cacheDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
    val cacheRepository = givenCacheRepository(cacheDataSource = cacheDataSource)

    val value = cacheRepository.getAll(expectedValues.query, CacheOperation())

    assertContentEquals(expectedValues.value, value)
  }

  @Test
  fun `should throw operation not allowed using getAll()`() = runTest {
    val cacheRepository = givenCacheRepository<String>()

    assertFailsWith<OperationNotAllowedException> {
      cacheRepository.getAll(anyQuery(), anyOperation())
    }
  }

  @Test
  fun `should store the response from the main datasource into the cache datasource when MainSyncOperation is provided when getAll() is called`() = runTest {
    val expectedValues = anyInsertionValues()
    val mainDataSource = anyInMemoryDataSource(putAllValues = listOf(InsertionValues(expectedValues.query, expectedValues.value)))
    val cacheDataSource = anyInMemoryDataSource<String>()
    val cacheRepository = givenCacheRepository(mainDataSource, cacheDataSource)

    val value = cacheRepository.getAll(expectedValues.query, MainSyncOperation)

    assertContentEquals(expectedValues.value, value)

    val expectedValueInCache = cacheDataSource.getAll(expectedValues.query)
    assertContentEquals(expectedValues.value, expectedValueInCache)
  }

  @Test
  fun `should response the value from the cache when CacheOperation is provided and there is values within the cache when getAll() is called`() = runTest {
    val expectedValues = anyInsertionValues()
    val cacheDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
    val cacheRepository = givenCacheRepository(cacheDataSource = cacheDataSource)

    val value = cacheRepository.getAll(expectedValues.query, CacheOperation())

    assertContentEquals(expectedValues.value, value)
  }

  @Test
  fun `should throw ObjectNotValidException when the object is not valid from the cache when getAll() is called`() = runTest {
    assertFailsWith<ObjectNotValidException> {
      val expectedValues = anyInsertionValues()
      val cacheDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
      val validator = anyMockValidator<String>(validatorResponse = false)
      val cacheRepository = givenCacheRepository(cacheDataSource = cacheDataSource, validator = validator)

      cacheRepository.getAll(expectedValues.query, CacheOperation())
    }
  }

  @Test
  fun `should response the value from the cache when is invalid and CacheOperation fallback is set to true when getAll() called`() = runTest {
    val expectedValues = anyInsertionValues()
    val cacheDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
    val validator = anyMockValidator<String>(validatorResponse = false)
    val cacheRepository = givenCacheRepository(cacheDataSource = cacheDataSource, validator = validator)

    val cacheOperation = CacheOperation(fallback = { return@CacheOperation true })
    val value = cacheRepository.getAll(expectedValues.query, cacheOperation)

    assertContentEquals(expectedValues.value, value)
  }
  //endregion

  //region put() - tests

  @Test
  fun `should store value in main datasource when using MainOperation`() = runTest {
    val mainDataSource = anyInMemoryDataSource<String>()
    val cacheRepository = givenCacheRepository(mainDataSource = mainDataSource)
    val expectedValue = anyInsertionValue()

    val value = cacheRepository.put(expectedValue.query, expectedValue.value, MainOperation)
    val mainValue = mainDataSource.get(expectedValue.query)

    assertEquals(expectedValue.value, value)
    assertEquals(expectedValue.value, mainValue)
  }

  @Test
  fun `should store value in cache datasource when using CacheOperation`() = runTest {
    val cacheDataSource = anyInMemoryDataSource<String>()
    val cacheRepository = givenCacheRepository(cacheDataSource = cacheDataSource)
    val expectedValue = anyInsertionValue()

    val value = cacheRepository.put(expectedValue.query, expectedValue.value, CacheOperation())
    val mainValue = cacheDataSource.get(expectedValue.query)

    assertEquals(expectedValue.value, value)
    assertEquals(expectedValue.value, mainValue)
  }

  @Test
  fun `should store the value first in main datasouce and cache datasource when using MainSyncOperation`() = runTest {
    val cacheDataSource = anyInMemoryDataSource<String>()
    val mainDataSource = anyInMemoryDataSource<String>()
    val cacheRepository = givenCacheRepository(mainDataSource, cacheDataSource)
    val expectedValue = anyInsertionValue()

    val value = cacheRepository.put(expectedValue.query, expectedValue.value, MainSyncOperation)
    val mainDataSourceValue = mainDataSource.get(expectedValue.query)
    val cacheDataSourceValue = cacheDataSource.get(expectedValue.query)

    assertEquals(expectedValue.value, value)
    assertEquals(expectedValue.value, mainDataSourceValue)
    assertEquals(expectedValue.value, cacheDataSourceValue)
  }

  @Test
  fun `should replicate MainSyncOperation behaviour when DefaultOperation is provided using put()`() = runTest {
    val cacheDataSource = anyInMemoryDataSource<String>()
    val mainDataSource = anyInMemoryDataSource<String>()
    val cacheRepository = givenCacheRepository(mainDataSource, cacheDataSource)
    val expectedValue = anyInsertionValue()

    val value = cacheRepository.put(expectedValue.query, expectedValue.value, DefaultOperation)
    val mainDataSourceValue = mainDataSource.get(expectedValue.query)
    val cacheDataSourceValue = cacheDataSource.get(expectedValue.query)

    assertEquals(expectedValue.value, value)
    assertEquals(expectedValue.value, mainDataSourceValue)
    assertEquals(expectedValue.value, cacheDataSourceValue)
  }

  @Test
  fun `should store the value first in cache datasouce and main datasource when using CacheSyncOperation`() = runTest {
    val cacheDataSource = anyInMemoryDataSource<String>()
    val mainDataSource = anyInMemoryDataSource<String>()
    val cacheRepository = givenCacheRepository(mainDataSource, cacheDataSource)
    val expectedValue = anyInsertionValue()

    val value = cacheRepository.put(expectedValue.query, expectedValue.value, CacheSyncOperation())
    val cacheDataSourceValue = cacheDataSource.get(expectedValue.query)
    val mainDataSourceValue = mainDataSource.get(expectedValue.query)

    assertEquals(expectedValue.value, value)
    assertEquals(expectedValue.value, mainDataSourceValue)
    assertEquals(expectedValue.value, cacheDataSourceValue)
  }

  @Test
  fun `should throw operation not allowed using put()`() = runTest {
    val cacheRepository = givenCacheRepository<String>()

    assertFailsWith<OperationNotAllowedException> {
      cacheRepository.put(anyQuery(), randomString(), anyOperation())
    }
  }
  //endregion

  //region putAll() - tests

  @Test
  fun `should store values in main datasource when using MainOperation`() = runTest {
    val mainDataSource = anyInMemoryDataSource<String>()
    val cacheRepository = givenCacheRepository(mainDataSource = mainDataSource)
    val expectedValues = anyInsertionValues()

    val value = cacheRepository.putAll(expectedValues.query, expectedValues.value, MainOperation)
    val mainValue = mainDataSource.getAll(expectedValues.query)

    assertContentEquals(expectedValues.value, value)
    assertContentEquals(expectedValues.value, mainValue)
  }

  @Test
  fun `should store values in cache datasource when using CacheOperation`() = runTest {
    val cacheDataSource = anyInMemoryDataSource<String>()
    val cacheRepository = givenCacheRepository(cacheDataSource = cacheDataSource)
    val expectedValues = anyInsertionValues()

    val value = cacheRepository.putAll(expectedValues.query, expectedValues.value, CacheOperation())
    val mainValue = cacheDataSource.getAll(expectedValues.query)

    assertContentEquals(expectedValues.value, value)
    assertContentEquals(expectedValues.value, mainValue)
  }

  @Test
  fun `should store the values first in main datasouce and cache datasource when using MainSyncOperation`() = runTest {
    val cacheDataSource = anyInMemoryDataSource<String>()
    val mainDataSource = anyInMemoryDataSource<String>()
    val cacheRepository = givenCacheRepository(mainDataSource, cacheDataSource)
    val expectedValues = anyInsertionValues()

    val value = cacheRepository.putAll(expectedValues.query, expectedValues.value, MainSyncOperation)
    val mainDataSourceValue = mainDataSource.getAll(expectedValues.query)
    val cacheDataSourceValue = cacheDataSource.getAll(expectedValues.query)

    assertContentEquals(expectedValues.value, value)
    assertContentEquals(expectedValues.value, mainDataSourceValue)
    assertContentEquals(expectedValues.value, cacheDataSourceValue)
  }

  @Test
  fun `should replicate MainSyncOperation behaviour when DefaultOperation is provided`() = runTest {
    val cacheDataSource = anyInMemoryDataSource<String>()
    val mainDataSource = anyInMemoryDataSource<String>()
    val cacheRepository = givenCacheRepository(mainDataSource, cacheDataSource)
    val expectedValues = anyInsertionValues()

    val value = cacheRepository.putAll(expectedValues.query, expectedValues.value, DefaultOperation)
    val mainDataSourceValue = mainDataSource.getAll(expectedValues.query)
    val cacheDataSourceValue = cacheDataSource.getAll(expectedValues.query)

    assertContentEquals(expectedValues.value, value)
    assertContentEquals(expectedValues.value, mainDataSourceValue)
    assertContentEquals(expectedValues.value, cacheDataSourceValue)
  }

  @Test
  fun `should store the values first in cache datasouce and main datasource when using CacheSyncOperation`() = runTest {
    val cacheDataSource = anyInMemoryDataSource<String>()
    val mainDataSource = anyInMemoryDataSource<String>()
    val cacheRepository = givenCacheRepository(mainDataSource, cacheDataSource)
    val expectedValues = anyInsertionValues()

    val value = cacheRepository.putAll(expectedValues.query, expectedValues.value, CacheSyncOperation())
    val cacheDataSourceValue = cacheDataSource.getAll(expectedValues.query)
    val mainDataSourceValue = mainDataSource.getAll(expectedValues.query)

    assertContentEquals(expectedValues.value, value)
    assertContentEquals(expectedValues.value, mainDataSourceValue)
    assertContentEquals(expectedValues.value, cacheDataSourceValue)
  }

  @Test
  fun `should throw operation not allowed using putAll()`() = runTest {
    val cacheRepository = givenCacheRepository<String>()

    assertFailsWith<OperationNotAllowedException> {
      cacheRepository.putAll(anyQuery(), listOf(randomString()), anyOperation())
    }
  }
  //endregion

  //region delete() - tests

  @Test
  fun `should delete value from the main datasource when using MainOperation`() = runTest {
    val expectedValue = anyInsertionValue()
    val mainDataSource = anyInMemoryDataSource(putValues = listOf(expectedValue))
    val cacheRepository = givenCacheRepository(mainDataSource)

    cacheRepository.delete(expectedValue.query, MainOperation)

    assertFailsWith<DataNotFoundException> {
      mainDataSource.get(expectedValue.query)
    }
  }

  @Test
  fun `should delete value from the cache datasource when using CacheOperation`() = runTest {
    val expectedValue = anyInsertionValue()
    val cacheDataSource = anyInMemoryDataSource(putValues = listOf(expectedValue))
    val cacheRepository = givenCacheRepository(cacheDataSource = cacheDataSource)

    cacheRepository.delete(expectedValue.query, CacheOperation())

    assertFailsWith<DataNotFoundException> {
      cacheDataSource.get(expectedValue.query)
    }
  }

  @Test
  fun `should delete value from the main datasource and cache datasource when using MainSyncOperation`() = runTest {
    val expectedValue = anyInsertionValue()
    val mainDataSource = anyInMemoryDataSource(putValues = listOf(expectedValue))
    val cacheDataSource = anyInMemoryDataSource(putValues = listOf(expectedValue))
    val cacheRepository = givenCacheRepository(mainDataSource = mainDataSource, cacheDataSource = cacheDataSource)

    cacheRepository.delete(expectedValue.query, MainSyncOperation)

    assertFailsWith<DataNotFoundException> {
      mainDataSource.get(expectedValue.query)
      cacheDataSource.get(expectedValue.query)
    }
  }

  @Test
  fun `should delete value from the cache datasource and main datasource when using CacheSyncOperation`() = runTest {
    val expectedValue = anyInsertionValue()
    val mainDataSource = anyInMemoryDataSource(putValues = listOf(expectedValue))
    val cacheDataSource = anyInMemoryDataSource(putValues = listOf(expectedValue))
    val cacheRepository = givenCacheRepository(mainDataSource = mainDataSource, cacheDataSource = cacheDataSource)

    cacheRepository.delete(expectedValue.query, CacheSyncOperation())

    assertFailsWith<DataNotFoundException> {
      cacheDataSource.get(expectedValue.query)
      mainDataSource.get(expectedValue.query)
    }
  }

  @Test
  fun `should throw operation not allowed using delete()`() = runTest {
    val cacheRepository = givenCacheRepository<String>()

    assertFailsWith<OperationNotAllowedException> {
      cacheRepository.delete(anyQuery(), anyOperation())
    }
  }
  //endregion

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
