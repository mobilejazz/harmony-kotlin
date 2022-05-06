package com.harmony.kotlin.common

import com.harmony.kotlin.common.logger.Logger
import com.harmony.kotlin.error.DataSerializationException
import com.harmony.kotlin.error.HarmonyException
import com.harmony.kotlin.error.OperationNotSupportedException
import com.harmony.kotlin.error.QueryNotSupportedException

/**
 * This extension method is intended to manage complete/failure flow while automatically logging exceptions
 */
inline fun <T> Result<T>.onComplete(
  onFailureLogger: Logger,
  tag: String,
  onSuccess: (T) -> Unit,
  onFailure: ((Throwable) -> Unit) = { _ -> }
) {

  if (this.isSuccess) {
    this.onSuccess { onSuccess(it) }
  } else {
    this.onFailure {
      when (it) {
        // Exceptions below are expected but are most probably happening due to a Development error
        is OperationNotSupportedException, is QueryNotSupportedException, is DataSerializationException -> {
          onFailureLogger.e(tag, it.stackTraceToString())
          onFailureLogger.sendIssue(tag, it.stackTraceToString())
        }
        // Exceptions below are expected as we control them
        is HarmonyException -> onFailureLogger.d(tag, it.stackTraceToString())
        // Exceptions below are not expected, it can be a bug in the code or a contract being broken by the source of data (e.g: backend returning invalid json)
        else -> {
          onFailureLogger.e(tag, it.stackTraceToString())
          onFailureLogger.sendIssue(tag, it.stackTraceToString())
        }
      }
      onFailure(it)
    }
  }
}
