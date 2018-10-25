package com.mobilejazz.kotlin.core.repository.error

// Errors

sealed class RepositoryException(override val message: String?, override val cause: Throwable?) : Throwable(message)

class DataNotFoundException(message: String? = "Data not found", cause: Throwable? = null) : RepositoryException(message, cause)

class DeleteObjectFailException(message: String? = "Fail deleting object", cause: Throwable? = null) : RepositoryException(message, cause)

class PutObjectFailException(message: String? = "Fail updating or inserting object", cause: Throwable? = null) : RepositoryException(message, cause)

class ObjectNotValidException(message: String? = "Object not valid", cause: Throwable? = null) : RepositoryException(message, cause)

class QueryNotSupportedException(message: String? = "Query not supported", cause: Throwable? = null): RepositoryException(message, cause)