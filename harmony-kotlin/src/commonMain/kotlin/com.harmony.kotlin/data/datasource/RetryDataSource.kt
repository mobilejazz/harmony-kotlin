package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.common.logger.Logger
import com.harmony.kotlin.data.query.Query

class RetryDataSource<V>(
  private val getDataSource: GetDataSource<V>? = null,
  private val putDataSource: PutDataSource<V>? = null,
  private val deleteDataSource: DeleteDataSource? = null,
  private val maxAmountOfRetries: Int = 2,
  private val retryIf: (Exception) -> Boolean = { true },
  private val logger: Logger? = null,
) : GetDataSource<V>, PutDataSource<V>, DeleteDataSource {

  init {
    check(maxAmountOfRetries > 0)
  }

  override suspend fun get(query: Query): V =
    executeWithRetries(retryCount = maxAmountOfRetries) {
      getDataSource().get(query)
    }

  override suspend fun getAll(query: Query): List<V> =
    executeWithRetries(retryCount = maxAmountOfRetries) {
      getDataSource().getAll(query)
    }

  override suspend fun put(query: Query, value: V?): V =
    executeWithRetries(retryCount = maxAmountOfRetries) {
      putDataSource().put(query, value)
    }

  override suspend fun putAll(query: Query, value: List<V>?): List<V> =
    executeWithRetries(retryCount = maxAmountOfRetries) {
      putDataSource().putAll(query, value)
    }

  override suspend fun delete(query: Query) =
    executeWithRetries(retryCount = maxAmountOfRetries) {
      deleteDataSource().delete(query)
    }

  private suspend fun <K> executeWithRetries(retryCount: Int, functionToRetry: suspend () -> K): K =
    @Suppress("TooGenericExceptionCaught")
    try {
      functionToRetry()
    } catch (error: Exception) {
      logger?.e("RetryDataSource", "failed attempt ${this.maxAmountOfRetries - retryCount + 1} of max retries ${this.maxAmountOfRetries}:\n\t$error")
      if (retryCount > 1 && retryIf(error)) {
        executeWithRetries(retryCount - 1, functionToRetry)
      } else throw error
    }

  private fun getDataSource(): GetDataSource<V> {
    val getDataSource = this.getDataSource
    checkNotNull(getDataSource) { "Expected ${GetDataSource::class}" }
    return getDataSource
  }

  private fun putDataSource(): PutDataSource<V> {
    val putDataSource = this.putDataSource
    checkNotNull(putDataSource) { "Expected ${PutDataSource::class}" }
    return putDataSource
  }

  private fun deleteDataSource(): DeleteDataSource {
    val deleteDataSource = this.deleteDataSource
    checkNotNull(deleteDataSource) { "Expected ${DeleteDataSource::class}" }
    return deleteDataSource
  }
}
