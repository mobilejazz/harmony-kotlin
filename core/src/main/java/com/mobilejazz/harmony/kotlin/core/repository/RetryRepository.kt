package com.mobilejazz.harmony.kotlin.core.repository

import com.mobilejazz.harmony.kotlin.core.repository.operation.DefaultOperation
import com.mobilejazz.harmony.kotlin.core.repository.operation.Operation
import com.mobilejazz.harmony.kotlin.core.repository.query.Query

data class RetryRule(val count: Int = 1, val retryIf: (Exception) -> Boolean) {

  fun canRetry(exception: Exception): Boolean = count > 0 && retryIf(exception)

  fun next(): RetryRule = RetryRule(count - 1, retryIf)
}

data class RetryOperation(val retryRule: RetryRule, val operation: Operation = DefaultOperation) : Operation()

class RetryRepository<T>(
    private val retryRule: RetryRule = RetryRule(1) { true },
    private val getRepository: GetRepository<T>,
    private val putRepository: PutRepository<T>,
    private val deleteRepository: DeleteRepository) : GetRepository<T>, PutRepository<T>, DeleteRepository {

  override suspend fun get(query: Query, operation: Operation): T {
    when (operation) {
      is RetryOperation -> {
        return try {
          getRepository.get(query, operation.operation)
        } catch (e: Exception) {
          val rule = operation.retryRule
          if (rule.canRetry(e)) {
            get(query, RetryOperation(rule.next(), operation.operation))
          } else {
            throw e
          }
        }
      }
      else -> {
        return get(query, RetryOperation(retryRule, operation))
      }
    }
  }

  override suspend fun getAll(query: Query, operation: Operation): List<T> {
    when (operation) {
      is RetryOperation -> {
        return try {
          getRepository.getAll(query, operation.operation)
        } catch (e: Exception) {
          val rule = operation.retryRule
          if (rule.canRetry(e)) {
            getAll(query, RetryOperation(rule.next(), operation.operation))
          } else {
            throw e
          }
        }
      }
      else -> {
        return getAll(query, RetryOperation(retryRule, operation))
      }
    }
  }

  override suspend fun put(query: Query, value: T?, operation: Operation): T {
    when (operation) {
      is RetryOperation -> {
        return try {
          putRepository.put(query, value, operation.operation)
        } catch (e: Exception) {
          val rule = operation.retryRule
          if (rule.canRetry(e)) {
            put(query, value, RetryOperation(rule.next(), operation.operation))
          } else {
            throw e
          }
        }
      }
      else -> {
        return put(query, value, RetryOperation(retryRule, operation))
      }
    }
  }

  override suspend fun putAll(query: Query, value: List<T>?, operation: Operation): List<T> {
    when (operation) {
      is RetryOperation -> {
        return try {
          putRepository.putAll(query, value, operation.operation)
        } catch (e: Exception) {
          val rule = operation.retryRule
          if (rule.canRetry(e)) {
            putAll(query, value, RetryOperation(rule.next(), operation.operation))
          } else {
            throw e
          }
        }
      }
      else -> {
        return putAll(query, value, RetryOperation(retryRule, operation))
      }
    }
  }

  override suspend fun delete(query: Query, operation: Operation) {
    when (operation) {
      is RetryOperation -> {
        return try {
          deleteRepository.delete(query, operation.operation)
        } catch (e: Exception) {
          val rule = operation.retryRule
          if (rule.canRetry(e)) {
            delete(query, RetryOperation(rule.next(), operation.operation))
          } else {
            throw e
          }
        }
      }
      else -> {
        return delete(query, RetryOperation(retryRule, operation))
      }
    }
  }

  override suspend fun deleteAll(query: Query, operation: Operation) {
    when (operation) {
      is RetryOperation -> {
        return try {
          deleteRepository.deleteAll(query, operation.operation)
        } catch (e: Exception) {
          val rule = operation.retryRule
          if (rule.canRetry(e)) {
            deleteAll(query, RetryOperation(rule.next(), operation.operation))
          } else {
            throw e
          }
        }
      }
      else -> {
        return deleteAll(query, RetryOperation(retryRule, operation))
      }
    }
  }
}
