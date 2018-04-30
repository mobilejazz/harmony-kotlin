package com.mobilejazz.kotlin.core.datasource

// Operations

sealed class Operation

class DefaultOperation : Operation()

// Data stream will only use network
class NetworkOperation : Operation()

// Data stream will use network and sync with storage if needed
class NetworkSyncOperation: Operation()

// Data stream will only use storage
class StorageOperation : Operation()

// Data stream will use storage and sync with network if needed
class StorageSyncOperation: Operation()

// Data stream will only use cache
class CacheOperation : Operation()
