package com.harmony.kotlin.data.error

// Errors
open class RepositoryException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

open class DataNotFoundException(message: String? = "Data not found", cause: Throwable? = null) : RepositoryException(message, cause)

class DeleteObjectFailException(message: String? = "Fail deleting object", cause: Throwable? = null) : RepositoryException(message, cause)

class PutObjectFailException(message: String? = "Fail updating or inserting object", cause: Throwable? = null) : RepositoryException(message, cause)

class ObjectNotValidException(message: String? = "Object not valid", cause: Throwable? = null) : RepositoryException(message, cause)

class QueryNotSupportedException(message: String? = "Query not supported", cause: Throwable? = null) : RepositoryException(message, cause)

open class MappingException(message: String? = "Exception thrown during mapping", cause: Throwable? = null) : RepositoryException(message, cause)

class SerializationException(message: String? = "Exception thrown during serialization", cause: Throwable? = null) : MappingException(message, cause)

open class NetworkErrorException(val statusCode: Int, message: String?, throwable: Throwable? = null) : RepositoryException(message, throwable)
