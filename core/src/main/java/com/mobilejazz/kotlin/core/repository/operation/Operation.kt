package com.mobilejazz.kotlin.core.repository.operation

// Operations

sealed class Operation

object DefaultOperation : Operation()

// Data stream will only use network
object NetworkOperation : Operation()

// Data stream will use network and sync with storage if needed
object NetworkSyncOperation: Operation()

// Data stream will only use storage
object StorageOperation : Operation()

// Data stream will use storage and sync with network if needed
object StorageSyncOperation: Operation()

// Data stream will only use cache
object CacheOperation : Operation()
