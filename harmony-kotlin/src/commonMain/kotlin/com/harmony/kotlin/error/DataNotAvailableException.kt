package com.harmony.kotlin.error

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
