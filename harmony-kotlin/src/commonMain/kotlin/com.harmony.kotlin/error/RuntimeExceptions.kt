package com.harmony.kotlin.error

class OperationNotSupportedException(message: String? = "Operation not allowed", cause: Throwable? = null) : UnsupportedOperationException(message, cause)

class QueryNotSupportedException(message: String? = "Query not supported", cause: Throwable? = null) : UnsupportedOperationException(message, cause)

class NotImplementedException(message: String? = "Not implemented", cause: Throwable? = null) : UnsupportedOperationException(message, cause)

// region helper functions
/**
 * @throws QueryNotSupportedException
 */
fun notSupportedQuery(): Nothing = throw QueryNotSupportedException()

/**
 * @throws OperationNotSupportedException
 */
fun notSupportedOperation(): Nothing = throw OperationNotSupportedException()

/**
 * @throws NotImplementedException
 */
fun notImplemented(): Nothing = throw NotImplementedException()
// endregion
