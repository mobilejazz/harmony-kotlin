package com.harmony.kotlin.data.error

import com.harmony.kotlin.error.HarmonyException

// region Data Exceptions
/**
 * Generic exception for errors on the data layer
 */
open class DataException(message: String? = null, cause: Throwable? = null) : HarmonyException(message, cause)

/**
 * Data is not available (for any reason)
 */
open class DataNotAvailableException(message: String? = null, cause: Throwable? = null) : DataException(message, cause)

/**
 * Data is not found.
 */
open class DataNotFoundException(message: String? = null, cause: Throwable? = null) : DataNotAvailableException(message, cause)

/**
 * Data is not found due to existing data being invalid
 */
class DataNotValidException(message: String? = null, cause: Throwable? = null) : DataNotAvailableException(message, cause)

/**
 * Not authorized to access the data
 */
class UnauthorizedException(message: String? = null, cause: Throwable, isResolved: Boolean) : DataException(message, cause)

// endregion

// region Development Exceptions

class MappingException(message: String? = "Exception thrown during mapping", cause: Throwable? = null) : DataException(message, cause)

class QueryNotSupportedException(message: String? = "Query not supported", cause: Throwable? = null) : DataException(message, cause)

class OperationNotAllowedException(message: String? = "This operation is not allowed on this repository", cause: Throwable? = null) :
  DataException(message, cause)


// endregion
