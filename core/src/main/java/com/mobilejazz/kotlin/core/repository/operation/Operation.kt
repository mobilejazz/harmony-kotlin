package com.mobilejazz.kotlin.core.repository.operation

// Operations

sealed class Operation

object DefaultOperation : Operation()

// Data stream will only use network
object MainOperation : Operation()

// Data stream will use network and sync with storage if needed
object MainSyncOperation: Operation()

// Data stream will only use storage
object CacheOperation : Operation()

// Data stream will use storage and sync with network if needed
object CacheSyncOperation: Operation()

