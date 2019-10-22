package com.mobilejazz.harmony.kotlin.core.repository

import com.mobilejazz.harmony.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.VoidDataSource
import com.mobilejazz.harmony.kotlin.core.repository.error.DataNotFoundException
import com.mobilejazz.harmony.kotlin.core.repository.operation.CacheSyncOperation
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.ValidationServiceManager
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.VastraValidator
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.ValidationStrategy
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.ValidationStrategyDataSource
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.ValidationStrategyResult
import com.mobilejazz.harmony.kotlin.core.threading.extensions.Future
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito.*


class CacheRepositoryTest {

  private data class AnyObject(val id: Int) : ValidationStrategyDataSource

  private val anyObject = AnyObject(123)
  private val anyList = listOf(AnyObject(1), AnyObject(2))
  private val alwaysInvalidStrategy by lazy {
    return@lazy object : ValidationStrategy {
      override fun <T : ValidationStrategyDataSource> isValid(t: T): ValidationStrategyResult {
        return ValidationStrategyResult.INVALID
      }
    }
  }
  private val cacheDataSource by lazy {
    val cacheDataSource: GetDataSource<AnyObject> = spy(GetDataSource::class.java) as GetDataSource<AnyObject>
    doReturn(Future { anyObject }).`when`(cacheDataSource).get(any<Query>() ?: Query())
    doReturn(Future { anyList }).`when`(cacheDataSource).getAll(any<Query>() ?: Query())
    cacheDataSource
  }

  private val failingMainDataSource by lazy {
    return@lazy object : GetDataSource<AnyObject> {
      override fun get(query: Query): Future<AnyObject> {
        throw DataNotFoundException()
      }

      override fun getAll(query: Query): Future<List<AnyObject>> {
        throw DataNotFoundException()
      }
    }
  }

  // region CacheOperationSync with fallback tests

  @Test
  internal fun shouldFallbackToCache_WhenGettingObject_GivenCacheSyncOperationNotValid_And_MainSyncOperationFailing() {

    // Given
    val cacheRepository = CacheRepository<AnyObject>(
        cacheDataSource, VoidDataSource(), VoidDataSource<AnyObject>(),
        failingMainDataSource, VoidDataSource(), VoidDataSource<AnyObject>(),
        VastraValidator<AnyObject>(ValidationServiceManager(listOf(alwaysInvalidStrategy))))

    // When
    val actual = cacheRepository.get(Query(), CacheSyncOperation(fallback = { it is DataNotFoundException })).get()

    // Then
    Assert.assertEquals(anyObject, actual)
  }

  @Test
  internal fun shouldFallbackToCache_WhenGettingList_GivenCacheSyncOperationNotValid_And_MainSyncOperationFailing() {
    // Given
    val cacheRepository = CacheRepository<AnyObject>(
        cacheDataSource, VoidDataSource(), VoidDataSource<AnyObject>(),
        failingMainDataSource, VoidDataSource(), VoidDataSource<AnyObject>(),
        VastraValidator<AnyObject>(ValidationServiceManager(listOf(alwaysInvalidStrategy))))

    // When
    val actual = cacheRepository.getAll(Query(), CacheSyncOperation(fallback = { it is DataNotFoundException })).get()

    // Then
    Assert.assertEquals(anyList, actual)
  }

  // endregion CacheOperationSync with fallback tests

}