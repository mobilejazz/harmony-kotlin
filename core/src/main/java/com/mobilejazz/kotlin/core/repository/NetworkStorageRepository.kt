package com.mobilejazz.kotlin.core.repository

import com.mobilejazz.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.kotlin.core.repository.error.ObjectNotValidException
import com.mobilejazz.kotlin.core.repository.operation.*
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.threading.Future
import com.mobilejazz.kotlin.core.threading.extensions.flatMap
import com.mobilejazz.kotlin.core.threading.extensions.recoverWith
import javax.inject.Inject


class NetworkStorageRepository<V> @Inject constructor(private val getStorage: GetDataSource<V>,
                                                      private val putStorage: PutDataSource<V>,
                                                      private val deleteStorage: DeleteDataSource,
                                                      private val getNetwork: GetDataSource<V>,
                                                      private val putNetwork: PutDataSource<V>,
                                                      private val deleteNetwork: DeleteDataSource) : GetRepository<V>, PutRepository<V>, DeleteRepository {

    override fun get(query: Query, operation: Operation): Future<V> = when (operation) {
        is StorageOperation -> getStorage.get(query)
        is NetworkOperation -> getNetwork.get(query)
        is NetworkSyncOperation -> getNetwork.get(query).flatMap { putStorage.put(query, it) }
        is StorageSyncOperation ->
            getStorage.get(query).recoverWith {
                when (it) {
                    is ObjectNotValidException -> get(query, NetworkSyncOperation())
                    else -> throw it
                }
            }
        else -> notSupportedOperation()
    }

    override fun getAll(query: Query, operation: Operation): Future<List<V>> = when (operation) {
        is StorageOperation -> getStorage.getAll(query)
        is NetworkOperation -> getNetwork.getAll(query)
        is NetworkSyncOperation -> getNetwork.getAll(query).flatMap { putStorage.putAll(query, it) }
        is StorageSyncOperation ->
            getStorage.getAll(query).recoverWith {
                when (it) {
                    is ObjectNotValidException -> getAll(query, NetworkSyncOperation())
                    else -> throw it
                }
            }
        else -> notSupportedOperation()
    }

    override fun put(query: Query, value: V, operation: Operation): Future<V> = when (operation) {
        is StorageOperation -> putStorage.put(query, value)
        is NetworkOperation -> putNetwork.put(query, value)
        is NetworkSyncOperation -> putNetwork.put(query, value).flatMap { putStorage.put(query, value) }
        is StorageSyncOperation -> putStorage.put(query, value).flatMap { putNetwork.put(query, value) }
        else -> notSupportedOperation()
    }

    override fun putAll(query: Query, value: List<V>, operation: Operation): Future<List<V>> = when (operation) {
        is StorageOperation -> putStorage.putAll(query, value)
        is NetworkOperation -> putNetwork.putAll(query, value)
        is NetworkSyncOperation -> putNetwork.putAll(query, value).flatMap { putStorage.putAll(query, value) }
        is StorageSyncOperation -> putStorage.putAll(query, value).flatMap { putNetwork.putAll(query, value) }
        else -> notSupportedOperation()
    }

    override fun delete(query: Query, operation: Operation): Future<Void> = when (operation) {
        is StorageOperation -> deleteStorage.delete(query)
        is NetworkOperation -> deleteNetwork.delete(query)
        is NetworkSyncOperation -> deleteNetwork.delete(query).flatMap { deleteStorage.delete(query) }
        is StorageSyncOperation -> deleteStorage.delete(query).flatMap { deleteNetwork.delete(query) }
        else -> notSupportedOperation()
    }

    override fun deleteAll(query: Query, operation: Operation): Future<Void> = when (operation) {
        is StorageOperation -> deleteStorage.deleteAll(query)
        is NetworkOperation -> deleteNetwork.deleteAll(query)
        is NetworkSyncOperation -> deleteNetwork.deleteAll(query).flatMap { deleteStorage.deleteAll(query) }
        is StorageSyncOperation -> deleteStorage.deleteAll(query).flatMap { deleteNetwork.deleteAll(query) }
        else -> notSupportedOperation()
    }
}