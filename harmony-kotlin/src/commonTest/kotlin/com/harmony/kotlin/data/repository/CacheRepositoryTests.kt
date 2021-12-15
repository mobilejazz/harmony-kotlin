@file:Suppress("IllegalIdentifier")

package com.harmony.kotlin.data.repository

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.data.datasource.DataSourceMapper
import com.harmony.kotlin.data.datasource.anyVoidDataSource
import com.harmony.kotlin.data.datasource.memory.InMemoryDataSource
import com.harmony.kotlin.data.datasource.memory.anyInMemoryDataSource
import com.harmony.kotlin.data.error.DataNotFoundException
import com.harmony.kotlin.data.error.DataNotValidException
import com.harmony.kotlin.data.error.MappingException
import com.harmony.kotlin.data.error.OperationNotAllowedException
import com.harmony.kotlin.data.mapper.ClosureMapper
import com.harmony.kotlin.data.operation.CacheOperation
import com.harmony.kotlin.data.operation.CacheSyncOperation
import com.harmony.kotlin.data.operation.DefaultOperation
import com.harmony.kotlin.data.operation.MainOperation
import com.harmony.kotlin.data.operation.MainSyncOperation
import com.harmony.kotlin.data.operation.anyOperation
import com.harmony.kotlin.data.query.anyKeyQuery
import com.harmony.kotlin.data.query.anyQuery
import com.harmony.kotlin.data.utilities.InsertionValue
import com.harmony.kotlin.data.utilities.InsertionValues
import com.harmony.kotlin.data.utilities.anyInsertionValue
import com.harmony.kotlin.data.utilities.anyInsertionValues
import com.harmony.kotlin.data.validator.Validator
import com.harmony.kotlin.data.validator.anyMockValidator
import com.harmony.kotlin.data.validator.mock.MockValidator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExperimentalCoroutinesApi
class CacheRepositoryTests : BaseTest() {

  //region get() - tests

  @Test
  fun `should retrieve the value from the main datasource when calling get() with MainOperation`() = runTest {
    val expectedValue = randomString()
    val anyQuery = anyKeyQuery(randomString())
    val mainDataSource = anyInMemoryDataSource(listOf(InsertionValue(anyQuery, expectedValue)))
    val cacheRepository = givenCacheRepository(main = mainDataSource)

    val value = cacheRepository.get(anyQuery, MainOperation)

    assertEquals(expectedValue, value)
  }

  @Test
  fun `should retrieve the value from the cache datasource when calling get() with CacheOperation`() = runTest {
    val expectedValue = randomString()
    val anyQuery = anyKeyQuery(randomString())
    val cacheDataSource = anyInMemoryDataSource(listOf(InsertionValue(anyQuery, expectedValue)))
    val cacheRepository = givenCacheRepository(cache = cacheDataSource)

    val value = cacheRepository.get(anyQuery, CacheOperation())

    assertEquals(expectedValue, value)
  }

  @Test
  fun `should throw operation not allowed when calling get() given an unsupported Operation`() = runTest {
    val cacheRepository = givenCacheRepository<String>()
    assertFailsWith<OperationNotAllowedException> {
      cacheRepository.get(anyQuery(), anyOperation())
    }
  }

  @Test
  fun `should store the response from the main datasource into the cache datasource when calling get() with MainSyncOperation`() = runTest {
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
  fun `should response the value from the cache when calling get() with CacheOperation and there is values within the cache`() = runTest {
    val expectedInsertionValue = anyInsertionValue()
    val cacheDataSource = anyInMemoryDataSource(listOf(expectedInsertionValue))
    val cacheRepository = givenCacheRepository(cache = cacheDataSource)

    val value = cacheRepository.get(expectedInsertionValue.query, CacheOperation())

    assertEquals(expectedInsertionValue.value, value)
  }

  @Test
  fun `should throw ObjectNotValidException when calling get() with CacheOperation given that the object is not valid`() = runTest {
    assertFailsWith<DataNotValidException> {
      val anyInsertionValue = anyInsertionValue()
      val cacheDataSource = anyInMemoryDataSource(listOf(anyInsertionValue))
      val validator = anyMockValidator<String>(validatorResponse = false)
      val cacheRepository = givenCacheRepository(cache = cacheDataSource, validator = validator)

      cacheRepository.get(anyInsertionValue.query, CacheOperation())
    }
  }

  @Test
  fun `should get cached value when calling get() with CacheOperation given that the object is not valid and fallback returns true`() =
    runTest {
      val expectedInsertionValue = anyInsertionValue()

      val cacheDataSource = anyInMemoryDataSource(listOf(expectedInsertionValue))

      val validator = anyMockValidator<String>(validatorResponse = false)
      val cacheRepository = givenCacheRepository(cache = cacheDataSource, validator = validator)

      val cacheOperation = CacheOperation(fallback = { return@CacheOperation true })
      val value = cacheRepository.get(expectedInsertionValue.query, cacheOperation)

      assertEquals(expectedInsertionValue.value, value)
    }

  @Test
  fun `should return cache value when calling get() with CacheSyncOperation given that cache value exists and is valid`() = runTest {
    val expectedValue = anyInsertionValue()
    val cacheDataSource = anyInMemoryDataSource(putValues = listOf(expectedValue))
    val validator = anyMockValidator<String>(true)
    val cacheRepository = givenCacheRepository(cache = cacheDataSource, validator = validator)

    val value = cacheRepository.get(expectedValue.query, CacheSyncOperation())

    assertEquals(expectedValue.value, value)
  }

  @Test
  fun `should request to main datasource and store it into cache when calling get() with CacheSyncOperation given that cache value doesn't exist`() = runTest {
    val expectedValue = anyInsertionValue()
    val cacheDataSource = anyInMemoryDataSource<String>()
    val mainDataSource = anyInMemoryDataSource(putValues = listOf(expectedValue))
    val cacheRepository = givenCacheRepository(main = mainDataSource, cache = cacheDataSource)

    val value = cacheRepository.get(expectedValue.query, CacheSyncOperation())
    val cacheValue = cacheDataSource.get(expectedValue.query)

    assertEquals(expectedValue.value, value)
    assertEquals(expectedValue.value, cacheValue)
  }

  @Test
  fun `should request to main datasource and store it into cache when calling get() with CacheSyncOperation given that cache value not valid`() = runTest {
    val expectedValue = anyInsertionValue()
    val cacheDataSource = anyInMemoryDataSource(putValues = listOf(expectedValue))
    val mainDataSource = anyInMemoryDataSource(putValues = listOf(expectedValue))
    val validator = anyMockValidator<String>(false)
    val cacheRepository = givenCacheRepository(main = mainDataSource, cache = cacheDataSource, validator)

    val value = cacheRepository.get(expectedValue.query, CacheSyncOperation())
    val cacheValue = cacheDataSource.get(expectedValue.query)

    assertEquals(expectedValue.value, value)
    assertEquals(expectedValue.value, cacheValue)
  }

  @Test
  fun `should request to main datasource and store it into cache when calling get() with CacheSyncOperation given that cache throw MappingException`() =
    runTest {
      val expectedValue = anyInsertionValue()
      val cacheDataSource = anyInMemoryDataSource(putValues = listOf(expectedValue))
      var counter = 0
      val mockMapper = ClosureMapper<String, String> {
        if (counter == 0) {
          counter++
          throw MappingException()
        } else {
          it
        }
      }
      val cacheDataSourceMapper = DataSourceMapper(cacheDataSource, cacheDataSource, cacheDataSource, mockMapper, mockMapper)
      val mainDataSource = anyInMemoryDataSource(putValues = listOf(expectedValue))
      val cacheRepository = CacheRepository(
        cacheDataSourceMapper, cacheDataSourceMapper, cacheDataSourceMapper,
        mainDataSource, mainDataSource, mainDataSource,
        anyMockValidator()
      )

      val value = cacheRepository.get(expectedValue.query, CacheSyncOperation())
      val cacheValue = cacheDataSource.get(expectedValue.query)

      assertEquals(expectedValue.value, value)
      assertEquals(expectedValue.value, cacheValue)
    }

  @Test
  fun `should throw exception when calling get() with CacheSyncOperation given that cache throws a exception and fallback returns false`() =
    runTest {
      val mainDataSource = anyVoidDataSource<String>()
      val cacheDataSource = anyVoidDataSource<String>()
      val cacheRepository = CacheRepository(
        cacheDataSource, cacheDataSource, cacheDataSource,
        mainDataSource, mainDataSource, mainDataSource,
        MockValidator(true)
      )

      assertFailsWith<UnsupportedOperationException> {
        cacheRepository.get(anyQuery(), CacheSyncOperation())
      }
    }

  @Test
  fun
  `should get cached value when calling get() with CacheSyncOperation given that cached value isn't valid and fallback returns true`() =
    runTest {
      val expectedValue = anyInsertionValue()
      val cacheDataSource = anyInMemoryDataSource(putValues = listOf(expectedValue))
      val validator = MockValidator<String>(false)
      val cacheRepository = givenCacheRepository(cache = cacheDataSource, validator = validator)

      val value = cacheRepository.get(expectedValue.query, CacheSyncOperation(fallback = { _, _ -> true }))

      assertEquals(expectedValue.value, value)
    }

  //endregion

  //region getAll() - tests

  @Test
  fun `should retrieves the value from the main datasource when MainOperation is provided when getAll() is called`() = runTest {
    val expectedValues = anyInsertionValues()
    val mainDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
    val cacheRepository = givenCacheRepository(main = mainDataSource)

    val value = cacheRepository.getAll(expectedValues.query, MainOperation)

    assertEquals(expectedValues.value.size, value.size)
    assertContentEquals(expectedValues.value, value)
  }

  @Test
  fun `should retrieves the value from the cache datasource when CacheOperation is provided when getAll() is called`() = runTest {
    val expectedValues = anyInsertionValues()
    val cacheDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
    val cacheRepository = givenCacheRepository(cache = cacheDataSource)

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
    val cacheRepository = givenCacheRepository(cache = cacheDataSource)

    val value = cacheRepository.getAll(expectedValues.query, CacheOperation())

    assertContentEquals(expectedValues.value, value)
  }

  @Test
  fun `should throw ObjectNotValidException when the object is not valid from the cache when getAll() is called`() = runTest {
    assertFailsWith<DataNotValidException> {
      val expectedValues = anyInsertionValues()
      val cacheDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
      val validator = anyMockValidator<String>(validatorResponse = false)
      val cacheRepository = givenCacheRepository(cache = cacheDataSource, validator = validator)

      cacheRepository.getAll(expectedValues.query, CacheOperation())
    }
  }

  @Test
  fun `should response the value from the cache when is invalid and CacheOperation fallback is set to true when getAll() called`() = runTest {
    val expectedValues = anyInsertionValues()
    val cacheDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
    val validator = anyMockValidator<String>(validatorResponse = false)
    val cacheRepository = givenCacheRepository(cache = cacheDataSource, validator = validator)

    val cacheOperation = CacheOperation(fallback = { return@CacheOperation true })
    val value = cacheRepository.getAll(expectedValues.query, cacheOperation)

    assertContentEquals(expectedValues.value, value)
  }

  @Test
  fun `should return cache value when it's valid using CacheSyncOperation in getAll()`() = runTest {
    val expectedValues = anyInsertionValues()
    val cacheDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
    val validator = anyMockValidator<String>(true)
    val cacheRepository = givenCacheRepository(cache = cacheDataSource, validator = validator)

    val value = cacheRepository.getAll(expectedValues.query, CacheSyncOperation())

    assertContentEquals(expectedValues.value, value)
  }

  @Test
  fun `should request to main datasource when not found in the cache and store it when CacheSyncOperation in getAll()`() = runTest {
    val expectedValues = anyInsertionValues()
    val cacheDataSource = anyInMemoryDataSource<String>()
    val mainDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
    val cacheRepository = givenCacheRepository(main = mainDataSource, cache = cacheDataSource)

    val value = cacheRepository.getAll(expectedValues.query, CacheSyncOperation())
    val cacheValue = cacheDataSource.getAll(expectedValues.query)

    assertContentEquals(expectedValues.value, value)
    assertContentEquals(expectedValues.value, cacheValue)
  }

  @Test
  fun `should request to main datasource and store it into cache when cache value not valid using CacheSyncOperation in getAll()`() = runTest {
    val expectedValues = anyInsertionValues()
    val cacheDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
    val mainDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
    val validator = anyMockValidator<String>(false)
    val cacheRepository = givenCacheRepository(main = mainDataSource, cache = cacheDataSource, validator)

    val value = cacheRepository.getAll(expectedValues.query, CacheSyncOperation())
    val cacheValue = cacheDataSource.getAll(expectedValues.query)

    assertContentEquals(expectedValues.value, value)
    assertContentEquals(expectedValues.value, cacheValue)
  }

  @Test
  fun `should request to main datasource and store it into cache when cache throw MappingException using CacheSyncOperation in getAll()`() = runTest {
    val expectedValues = anyInsertionValues()
    val cacheDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
    var counter = 0
    val mockMapper = ClosureMapper<String, String> {
      if (counter == 0) {
        counter++
        throw MappingException()
      } else {
        it
      }
    }
    val cacheDataSourceMapper = DataSourceMapper(cacheDataSource, cacheDataSource, cacheDataSource, mockMapper, mockMapper)
    val mainDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
    val cacheRepository = CacheRepository(
      cacheDataSourceMapper, cacheDataSourceMapper, cacheDataSourceMapper,
      mainDataSource, mainDataSource, mainDataSource,
      anyMockValidator()
    )

    val value = cacheRepository.getAll(expectedValues.query, CacheSyncOperation())
    val cacheValue = cacheDataSource.getAll(expectedValues.query)

    assertContentEquals(expectedValues.value, value)
    assertContentEquals(expectedValues.value, cacheValue)
  }

  @Test
  fun `should throw exception when not handled by the cache and fallback is false using CacheSyncOperation in getAll()`() = runTest {
    val mainDataSource = anyVoidDataSource<String>()
    val cacheDataSource = anyVoidDataSource<String>()
    val cacheRepository = CacheRepository(
      cacheDataSource, cacheDataSource, cacheDataSource,
      mainDataSource, mainDataSource, mainDataSource,
      MockValidator(true)
    )

    assertFailsWith<UnsupportedOperationException> {
      cacheRepository.getAll(anyQuery(), CacheSyncOperation())
    }
  }

  @Test
  fun `should return cache value when fallback is true using CacheSyncOperation in getAll()`() = runTest {
    val expectedValues = anyInsertionValues()
    val cacheDataSource = anyInMemoryDataSource(putAllValues = listOf(expectedValues))
    val validator = MockValidator<String>(false)
    val cacheRepository = givenCacheRepository(cache = cacheDataSource, validator = validator)

    val value = cacheRepository.getAll(expectedValues.query, CacheSyncOperation(fallback = { _, _ -> true }))

    assertContentEquals(expectedValues.value, value)
  }

  //endregion

  //region put() - tests

  @Test
  fun `should store value in main datasource when using MainOperation`() = runTest {
    val mainDataSource = anyInMemoryDataSource<String>()
    val cacheRepository = givenCacheRepository(main = mainDataSource)
    val expectedValue = anyInsertionValue()

    val value = cacheRepository.put(expectedValue.query, expectedValue.value, MainOperation)
    val mainValue = mainDataSource.get(expectedValue.query)

    assertEquals(expectedValue.value, value)
    assertEquals(expectedValue.value, mainValue)
  }

  @Test
  fun `should store value in cache datasource when using CacheOperation`() = runTest {
    val cacheDataSource = anyInMemoryDataSource<String>()
    val cacheRepository = givenCacheRepository(cache = cacheDataSource)
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
    val cacheRepository = givenCacheRepository(main = mainDataSource)
    val expectedValues = anyInsertionValues()

    val value = cacheRepository.putAll(expectedValues.query, expectedValues.value, MainOperation)
    val mainValue = mainDataSource.getAll(expectedValues.query)

    assertContentEquals(expectedValues.value, value)
    assertContentEquals(expectedValues.value, mainValue)
  }

  @Test
  fun `should store values in cache datasource when using CacheOperation`() = runTest {
    val cacheDataSource = anyInMemoryDataSource<String>()
    val cacheRepository = givenCacheRepository(cache = cacheDataSource)
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
    val cacheRepository = givenCacheRepository(cache = cacheDataSource)

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
    val cacheRepository = givenCacheRepository(main = mainDataSource, cache = cacheDataSource)

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
    val cacheRepository = givenCacheRepository(main = mainDataSource, cache = cacheDataSource)

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

  private suspend fun <T> givenCacheRepository(
    main: InMemoryDataSource<T>? = null,
    cache: InMemoryDataSource<T>? = null,
    validator: Validator<T> = anyMockValidator()
  ): CacheRepository<T> {
    val mainDataSource = main ?: InMemoryDataSource()
    val cacheDataSource = cache ?: InMemoryDataSource()
    return CacheRepository(
      cacheDataSource, cacheDataSource, cacheDataSource,
      mainDataSource, mainDataSource, mainDataSource,
      validator
    )
  }
}
