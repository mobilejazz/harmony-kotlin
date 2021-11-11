package com.harmony.kotlin.data.operation

// Operations

open class Operation

object DefaultOperation : Operation()

/**
 * Data stream will only use the main data source
 */
object MainOperation : Operation()

/**
 *  Data stream will use the main data source and then sync result with the cache data source
 */
object MainSyncOperation : Operation()

/**
 * Data stream will only use the cache data source
 * @param fallback function that receives a Throwable and return a boolean flag indicating if we should fallback to cache without validating the object
 */
class CacheOperation(val fallback: (cacheOperationThrowable: Throwable) -> Boolean = { false }) : Operation()

/**
 * Data stream will use the cache data source and if fails it will sync with the main data source
 * @param fallback function that receives a Throwable and return a boolean flag indicating if we should fallback to cache without validating the object
 */
class CacheSyncOperation(val fallback: (mainOperationThrowable: Throwable) -> Boolean = { false }) : Operation()
