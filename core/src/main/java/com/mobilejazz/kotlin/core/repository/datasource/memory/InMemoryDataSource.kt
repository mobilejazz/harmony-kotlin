package com.mobilejazz.kotlin.core.repository.datasource.memory

import com.mobilejazz.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.kotlin.core.repository.error.DataNotFoundException
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.repository.query.StringKeyQuery
import com.mobilejazz.kotlin.core.threading.extensions.Future
import com.mobilejazz.kotlin.core.threading.extensions.emptyFuture
import javax.inject.Inject

class InMemoryDataSource<T> @Inject constructor() : GetDataSource<T>, PutDataSource<T>, DeleteDataSource {

    private val objects: MutableMap<String, T> = mutableMapOf()
    private val arrays: MutableMap<String, List<T>> = mutableMapOf()

    override fun get(query: Query): Future<T> = Future {
        when (query) {
            is StringKeyQuery -> {
                objects[query.key].run { this ?: throw DataNotFoundException() }
            }
            else -> notSupportedQuery()
        }
    }

    override fun getAll(query: Query): Future<List<T>> {
        return Future {
            when (query) {
                is StringKeyQuery -> {
                    arrays[query.key].run { this ?: throw DataNotFoundException() }
                }
                else -> notSupportedQuery()
            }
        }
    }


    @Suppress("USELESS_CAST")
    override fun put(query: Query, value: T?): Future<T> = Future {
        when (query) {
            is StringKeyQuery -> {
                if (value == null) {
                    throw IllegalArgumentException("InMemoryDataSource: value must be not null")
                } else {
                    objects[query.key] = value
                    return@Future value as T
                }
            }
            else -> notSupportedQuery()
        }
    }

    override fun putAll(query: Query, value: List<T>?): Future<List<T>> = Future {
        when (query) {
            is StringKeyQuery -> {
                if (value == null) {
                    throw IllegalArgumentException("InMemoryDataSource: values must be not null")
                }
                else {
                    return@Future arrays.put(query.key, value).run { value as List<T> }
                }
            }
            else -> notSupportedQuery()
        }
    }

    override fun delete(query: Query): Future<Unit> = when (query) {
        is StringKeyQuery -> objects.remove(query.key).run { emptyFuture() }
        else -> notSupportedQuery()
    }

    override fun deleteAll(query: Query): Future<Unit> = when (query) {
        is StringKeyQuery -> arrays.remove(query.key).run { emptyFuture() }
        else -> notSupportedQuery()
    }
}