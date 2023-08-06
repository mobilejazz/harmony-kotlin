package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.common.logger.Logger
import com.harmony.kotlin.data.query.Query

class RetryDataSource<V>(
  private val getDataSource: GetDataSource<V>,
  private val putDataSource: PutDataSource<V>,
  private val deleteDataSource: DeleteDataSource,
  private val maxAmountOfExecutions: Int = 2,
  private val retryIf: (Exception) -> Boolean = { true },
  private val logger: Logger? = null,
) : GetDataSource<V>, PutDataSource<V>, DeleteDataSource {

  init {
    check(maxAmountOfExecutions > 0)
  }

  override suspend fun get(query: Query): V =
    executeWithRetries(retryCount = maxAmountOfExecutions) {
      getDataSource.get(query)
    }

  override suspend fun put(query: Query, value: V?): V =
    executeWithRetries(retryCount = maxAmountOfExecutions) {
      putDataSource.put(query, value)
    }

  override suspend fun delete(query: Query) =
    executeWithRetries(retryCount = maxAmountOfExecutions) {
      deleteDataSource.delete(query)
    }

  private suspend fun <K> executeWithRetries(retryCount: Int, functionToRetry: suspend () -> K): K =
    @Suppress("TooGenericExceptionCaught")
    try {
      functionToRetry()
    } catch (error: Exception) {
      logger?.d("RetryDataSource", "failed attempt ${this.maxAmountOfExecutions - retryCount + 1} of max retries ${this.maxAmountOfExecutions}:\n\t$error")
      if (retryCount > 1 && retryIf(error)) {
        executeWithRetries(retryCount - 1, functionToRetry)
      } else throw error
    }
}
