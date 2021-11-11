package com.harmony.kotlin.data.error

// Errors
/**
 * Generic exception for errors on the data layer
 */
open class RepositoryException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

open class DataNotFoundException(message: String? = "Data not found", cause: Throwable? = null) : RepositoryException(message, cause)

class DeleteObjectFailException(message: String? = "Fail deleting object", cause: Throwable? = null) : RepositoryException(message, cause)

class PutObjectFailException(message: String? = "Fail updating or inserting object", cause: Throwable? = null) : RepositoryException(message, cause)

class ObjectNotValidException(message: String? = "Object not valid", cause: Throwable? = null) : RepositoryException(message, cause)

class QueryNotSupportedException(message: String? = "Query not supported", cause: Throwable? = null) : RepositoryException(message, cause)

class OperationNotAllowedException(message: String? = "This operation is not allowed on this repository", cause: Throwable? = null) :
  RepositoryException(message, cause)

/**
 * Exception for errors on the mappers
 */
open class MappingException(message: String? = "Exception thrown during mapping", cause: Throwable? = null) : RepositoryException(message, cause)

/**
 * Exception for serialization errors on the mappers
 */
class MappingSerializationException(message: String? = "Exception thrown during serialization", cause: Throwable? = null) :
  MappingException(message, cause)

/**
 * Exception for network errors
 */
open class NetworkErrorException(val statusCode: Int, message: String?, throwable: Throwable? = null) : RepositoryException(message, throwable)
