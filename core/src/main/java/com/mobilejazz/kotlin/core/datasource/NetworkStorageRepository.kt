package com.mobilejazz.kotlin.core.datasource

import com.mobilejazz.kotlin.core.threading.extensions.flatMap


class NetworkStorageRepository<V>(private val getStorage: GetDataSource<V>,
                                  private val putStorage: PutDataSource<V>,
                                  private val deleteStorage: DeleteDataSource,
                                  private val getNetwork: GetDataSource<V>,
                                  private val putNetwork: PutDataSource<V>,
                                  private val deleteNetwork: DeleteDataSource) : GetRepository<V>, PutRepository<V>, DeleteRepository {

    override fun get(query: Query, operation: Operation): Future<V> = when (operation) {
        is StorageOperation -> getStorage.get(query)
        is NetworkOperation -> getNetwork.get(query)
        is NetworkSyncOperation -> getNetwork.get(query).flatMap { putStorage.put(query, it) }
        else -> notSupportedOperation()
    }

    override fun getAll(query: Query, operation: Operation): Future<List<V>> = when (operation) {
        is StorageOperation -> getStorage.getAll(query)
        is NetworkOperation -> getNetwork.getAll(query)
        is NetworkSyncOperation -> getNetwork.getAll(query).flatMap { putStorage.putAll(query, it) }
        else -> notSupportedOperation()
    }

    override fun put(query: Query, value: V, operation: Operation): Future<V> = when (operation) {
        is StorageOperation -> putStorage.put(query, value)
        is NetworkOperation -> putNetwork.put(query, value)
        else -> notSupportedOperation()
    }

    override fun putAll(query: Query, value: List<V>, operation: Operation): Future<List<V>> = when (operation) {
        is StorageOperation -> putStorage.putAll(query, value)
        is NetworkOperation -> putNetwork.putAll(query, value)
        else -> notSupportedOperation()
    }

    override fun delete(query: Query, operation: Operation): Future<Void> = when (operation) {
        is StorageOperation -> deleteStorage.delete(query)
        is NetworkOperation -> deleteNetwork.delete(query)
        else -> notSupportedOperation()
    }

    override fun deleteAll(query: Query, operation: Operation): Future<Void> = when (operation) {
        is StorageOperation -> deleteStorage.deleteAll(query)
        is NetworkOperation -> deleteNetwork.deleteAll(query)
        else -> notSupportedOperation()
    }
}