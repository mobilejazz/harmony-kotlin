package com.harmony.kotlin.data.repository

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.data.datasource.DataSourceMapper
import com.harmony.kotlin.data.datasource.anyVoidDataSource
import com.harmony.kotlin.data.datasource.memory.InMemoryDataSource
import com.harmony.kotlin.data.datasource.memory.anyInMemoryDataSourceLegacy
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
import com.harmony.kotlin.data.utilities.anyInsertionValue
import com.harmony.kotlin.data.validator.Validator
import com.harmony.kotlin.data.validator.anyMockValidator
import com.harmony.kotlin.data.validator.mock.MockValidator
import com.harmony.kotlin.error.DataNotFoundException
import com.harmony.kotlin.error.DataNotValidException
import com.harmony.kotlin.error.DataSerializationException
import com.harmony.kotlin.error.OperationNotSupportedException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExperimentalCoroutinesApi
class CacheRepositoryTests : BaseTest() {

  //region get() - tests

  @Test
  fun `should retrieve the value from the main datasource when calling get function with MainOperation`() = runTest {
    val expectedValue = randomString()
    val anyQuery = anyKeyQuery(randomString())
    val mainDataSource = anyInMemoryDataSourceLegacy(listOf(InsertionValue(anyQuery, expectedValue)))
    val cacheRepository = givenCacheRepository(main = mainDataSource)

    val value = cacheRepository.get(anyQuery, MainOperation)

    assertEquals(expectedValue, value)
  }

  @Test
  fun `should retrieve the value from the cache datasource when calling get function with CacheOperation`() = runTest {
    val expectedValue = randomString()
    val anyQuery = anyKeyQuery(randomString())
    val cacheDataSource = anyInMemoryDataSourceLegacy(listOf(InsertionValue(anyQuery, expectedValue)))
    val cacheRepository = givenCacheRepository(cache = cacheDataSource)

    val value = cacheRepository.get(anyQuery, CacheOperation())

    assertEquals(expectedValue, value)
  }

  @Test
  fun `should throw operation not allowed when calling get function given an unsupported Operation`() = runTest {
    val cacheRepository = givenCacheRepository<String>()
    assertFailsWith<OperationNotSupportedException> {
      cacheRepository.get(anyQuery(), anyOperation())
    }
  }

  @Test
  fun `should store the response from the main datasource into the cache datasource when calling get function with MainSyncOperation`() = runTest {
    val expectedValue = randomString()
    val anyQuery = anyKeyQuery(randomString())
    val insertionValues = listOf(InsertionValue(anyQuery, expectedValue))
    val mainDataSource = anyInMemoryDataSourceLegacy(insertionValues)
    val cacheDataSource = anyInMemoryDataSourceLegacy<String>()
    val cacheRepository = givenCacheRepository(mainDataSource, cacheDataSource)

    val value = cacheRepository.get(anyQuery, MainSyncOperation)

    assertEquals(expectedValue, value)

    val expectedValueInCache = cacheDataSource.get(anyQuery)
    assertEquals(expectedValue, expectedValueInCache)
  }

  @Test
  fun `should response the value from the cache when calling get function with CacheOperation and there is values within the cache`() = runTest {
    val expectedInsertionValue = anyInsertionValue()
    val cacheDataSource = anyInMemoryDataSourceLegacy(listOf(expectedInsertionValue))
    val cacheRepository = givenCacheRepository(cache = cacheDataSource)

    val value = cacheRepository.get(expectedInsertionValue.query, CacheOperation())

    assertEquals(expectedInsertionValue.value, value)
  }

  @Test
  fun `should throw ObjectNotValidException when calling get function with CacheOperation given that the object is not valid`() = runTest {
    assertFailsWith<DataNotValidException> {
      val anyInsertionValue = anyInsertionValue()
      val cacheDataSource = anyInMemoryDataSourceLegacy(listOf(anyInsertionValue))
      val validator = anyMockValidator<String>(validatorResponse = false)
      val cacheRepository = givenCacheRepository(cache = cacheDataSource, validator = validator)

      cacheRepository.get(anyInsertionValue.query, CacheOperation())
    }
  }

  @Test
  fun `should get cached value when calling get function with CacheOperation given that the object is not valid and fallback returns true`() =
    runTest {
      val expectedInsertionValue = anyInsertionValue()

      val cacheDataSource = anyInMemoryDataSourceLegacy(listOf(expectedInsertionValue))

      val validator = anyMockValidator<String>(validatorResponse = false)
      val cacheRepository = givenCacheRepository(cache = cacheDataSource, validator = validator)

      val cacheOperation = CacheOperation(fallback = { return@CacheOperation true })
      val value = cacheRepository.get(expectedInsertionValue.query, cacheOperation)

      assertEquals(expectedInsertionValue.value, value)
    }

  @Test
  fun `should return cache value when calling get function with CacheSyncOperation given that cache value exists and is valid`() = runTest {
    val expectedValue = anyInsertionValue()
    val cacheDataSource = anyInMemoryDataSourceLegacy(putValues = listOf(expectedValue))
    val validator = anyMockValidator<String>(true)
    val cacheRepository = givenCacheRepository(cache = cacheDataSource, validator = validator)

    val value = cacheRepository.get(expectedValue.query, CacheSyncOperation())

    assertEquals(expectedValue.value, value)
  }

  @Test
  fun `should request to main datasource and store it into cache when calling get function with CacheSyncOperation given that cache value doesn't exist`() =
    runTest {
      val expectedValue = anyInsertionValue()
      val cacheDataSource = anyInMemoryDataSourceLegacy<String>()
      val mainDataSource = anyInMemoryDataSourceLegacy(putValues = listOf(expectedValue))
      val cacheRepository = givenCacheRepository(main = mainDataSource, cache = cacheDataSource)

      val value = cacheRepository.get(expectedValue.query, CacheSyncOperation())
      val cacheValue = cacheDataSource.get(expectedValue.query)

      assertEquals(expectedValue.value, value)
      assertEquals(expectedValue.value, cacheValue)
    }

  @Test
  fun `should request to main datasource and store it into cache when calling get function with CacheSyncOperation given that cache value not valid`() =
    runTest {
      val expectedValue = anyInsertionValue()
      val cacheDataSource = anyInMemoryDataSourceLegacy(putValues = listOf(expectedValue))
      val mainDataSource = anyInMemoryDataSourceLegacy(putValues = listOf(expectedValue))
      val validator = anyMockValidator<String>(false)
      val cacheRepository = givenCacheRepository(main = mainDataSource, cache = cacheDataSource, validator)

      val value = cacheRepository.get(expectedValue.query, CacheSyncOperation())
      val cacheValue = cacheDataSource.get(expectedValue.query)

      assertEquals(expectedValue.value, value)
      assertEquals(expectedValue.value, cacheValue)
    }

  @Test
  fun `should obtain the value from main when using CacheSyncOperation given that cache get function throws DataSerializationException`() =
    runTest {
      val expectedValue = anyInsertionValue()
      val cacheDataSource = anyInMemoryDataSourceLegacy(putValues = listOf(expectedValue))
      var counter = 0
      val mockMapper = ClosureMapper<String, String> {
        if (counter == 0) {
          counter++
          throw DataSerializationException()
        } else {
          it
        }
      }
      val cacheDataSourceMapper = DataSourceMapper(cacheDataSource, cacheDataSource, cacheDataSource, mockMapper, mockMapper)
      val mainDataSource = anyInMemoryDataSourceLegacy(putValues = listOf(expectedValue))
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
  fun `should throw exception when calling get function function with CacheSyncOperation given that cache throws a exception and fallback returns false`() =
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
  fun `should get cached value when function calling get function with CacheSyncOperation given that cached value isn't valid and fallback returns true`() =
    runTest {
      val expectedValue = anyInsertionValue()
      val cacheDataSource = anyInMemoryDataSourceLegacy(putValues = listOf(expectedValue))
      val validator = MockValidator<String>(false)
      val cacheRepository = givenCacheRepository(cache = cacheDataSource, validator = validator)

      val value = cacheRepository.get(expectedValue.query, CacheSyncOperation(fallback = { _, _ -> true }))

      assertEquals(expectedValue.value, value)
    }

  //endregion

  //region put() - tests

  @Test
  fun `should store value in main datasource when using MainOperation`() = runTest {
    val mainDataSource = anyInMemoryDataSourceLegacy<String>()
    val cacheRepository = givenCacheRepository(main = mainDataSource)
    val expectedValue = anyInsertionValue()

    val value = cacheRepository.put(expectedValue.query, expectedValue.value, MainOperation)
    val mainValue = mainDataSource.get(expectedValue.query)

    assertEquals(expectedValue.value, value)
    assertEquals(expectedValue.value, mainValue)
  }

  @Test
  fun `should store value in cache datasource when using CacheOperation`() = runTest {
    val cacheDataSource = anyInMemoryDataSourceLegacy<String>()
    val cacheRepository = givenCacheRepository(cache = cacheDataSource)
    val expectedValue = anyInsertionValue()

    val value = cacheRepository.put(expectedValue.query, expectedValue.value, CacheOperation())
    val mainValue = cacheDataSource.get(expectedValue.query)

    assertEquals(expectedValue.value, value)
    assertEquals(expectedValue.value, mainValue)
  }

  @Test
  fun `should store the value first in main datasource and cache datasource when using MainSyncOperation`() = runTest {
    val cacheDataSource = anyInMemoryDataSourceLegacy<String>()
    val mainDataSource = anyInMemoryDataSourceLegacy<String>()
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
  fun `should replicate MainSyncOperation behaviour when DefaultOperation is provided using put function`() = runTest {
    val cacheDataSource = anyInMemoryDataSourceLegacy<String>()
    val mainDataSource = anyInMemoryDataSourceLegacy<String>()
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
  fun `should store the value first in cache datasource and main datasource when using CacheSyncOperation`() = runTest {
    val cacheDataSource = anyInMemoryDataSourceLegacy<String>()
    val mainDataSource = anyInMemoryDataSourceLegacy<String>()
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
  fun `should throw operation not allowed using put function`() = runTest {
    val cacheRepository = givenCacheRepository<String>()

    assertFailsWith<OperationNotSupportedException> {
      cacheRepository.put(anyQuery(), randomString(), anyOperation())
    }
  }
  //endregion

  //region delete() - tests

  @Test
  fun `should delete value from the main datasource when using MainOperation`() = runTest {
    val expectedValue = anyInsertionValue()
    val mainDataSource = anyInMemoryDataSourceLegacy(putValues = listOf(expectedValue))
    val cacheRepository = givenCacheRepository(mainDataSource)

    cacheRepository.delete(expectedValue.query, MainOperation)

    assertFailsWith<DataNotFoundException> {
      mainDataSource.get(expectedValue.query)
    }
  }

  @Test
  fun `should delete value from the cache datasource when using CacheOperation`() = runTest {
    val expectedValue = anyInsertionValue()
    val cacheDataSource = anyInMemoryDataSourceLegacy(putValues = listOf(expectedValue))
    val cacheRepository = givenCacheRepository(cache = cacheDataSource)

    cacheRepository.delete(expectedValue.query, CacheOperation())

    assertFailsWith<DataNotFoundException> {
      cacheDataSource.get(expectedValue.query)
    }
  }

  @Test
  fun `should delete value from the main datasource and cache datasource when using MainSyncOperation`() = runTest {
    val expectedValue = anyInsertionValue()
    val mainDataSource = anyInMemoryDataSourceLegacy(putValues = listOf(expectedValue))
    val cacheDataSource = anyInMemoryDataSourceLegacy(putValues = listOf(expectedValue))
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
    val mainDataSource = anyInMemoryDataSourceLegacy(putValues = listOf(expectedValue))
    val cacheDataSource = anyInMemoryDataSourceLegacy(putValues = listOf(expectedValue))
    val cacheRepository = givenCacheRepository(main = mainDataSource, cache = cacheDataSource)

    cacheRepository.delete(expectedValue.query, CacheSyncOperation())

    assertFailsWith<DataNotFoundException> {
      cacheDataSource.get(expectedValue.query)
      mainDataSource.get(expectedValue.query)
    }
  }

  @Test
  fun `should throw operation not allowed using delete function`() = runTest {
    val cacheRepository = givenCacheRepository<String>()

    assertFailsWith<OperationNotSupportedException> {
      cacheRepository.delete(anyQuery(), anyOperation())
    }
  }
  //endregion

  private fun <T> givenCacheRepository(
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
