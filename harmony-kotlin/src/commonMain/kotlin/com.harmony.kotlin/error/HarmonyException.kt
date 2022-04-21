package com.harmony.kotlin.error

// region Harmony Exceptions
/**
 * Parent exception for all controlled Exceptions on Harmony and apps
 */
abstract class HarmonyException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

/**
 * Data is not available (for any reason)
 */
sealed class DataNotAvailableException(message: String? = null, cause: Throwable? = null) : HarmonyException(message, cause)

/**
 * Data is not found.
 */
class DataNotFoundException(message: String? = null, cause: Throwable? = null) : DataNotAvailableException(message, cause)

/**
 * Data is found but it is not valid (e.g: Cache is expired)
 */
class DataNotValidException(message: String? = null, cause: Throwable? = null) : DataNotAvailableException(message, cause)

/**
 * Data serialization/deserialization failed
 */
class DataSerializationException(message: String? = null, cause: Throwable? = null) : DataNotAvailableException(message, cause)

/**
 * Exception for network connectivity problems
 */
class NetworkConnectivityException(message: String? = null, cause: Throwable? = null) : HarmonyException(message, cause)

/**
 * Exception for http client(40X) & server(50X) errors
 */
class HttpException(val statusCode: Int, val response: String?) : HarmonyException()

/**
 * Not authorized to access the data
 */
class UnauthorizedException(message: String? = null, cause: Throwable, isResolved: Boolean) : HarmonyException(message, cause)
// endregion

// region Runtime Exceptions
class OperationNotSupportedException(message: String? = "Operation not allowed", cause: Throwable? = null) : UnsupportedOperationException(message, cause)

class QueryNotSupportedException(message: String? = "Query not supported", cause: Throwable? = null) : UnsupportedOperationException(message, cause)

class NotImplementedException(message: String? = "Not implemented", cause: Throwable? = null) : UnsupportedOperationException(message, cause)
// endregion

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
