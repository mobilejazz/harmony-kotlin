package com.mobilejazz.harmony.kotlin.android.repository.datasource.srpreferences

import com.mobilejazz.harmony.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.harmony.kotlin.core.repository.mapper.Mapper
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.threading.extensions.Future

class DeviceStorageObjectAssemblerDataSource<T>(
    private val toStringMapper: Mapper<T, String>,
    private val toModelMapper: Mapper<String, T>,
    private val toStringFromListMapper: Mapper<List<T>, String>,
    private val toModelFromListString: Mapper<String, List<T>>,
    private val deviceStorageDataSource: DeviceStorageDataSource<String>
) : GetDataSource<T>, PutDataSource<T>, DeleteDataSource {

  override fun get(query: Query): Future<T> {
    return Future {
      val stringValue = deviceStorageDataSource.get(query).get()
      return@Future toModelMapper.map(stringValue)
    }
  }

  override fun getAll(query: Query): Future<List<T>> {
    return Future {
      val stringValue = deviceStorageDataSource.get(query).get()
      return@Future toModelFromListString.map(stringValue)
    }
  }

  override fun put(query: Query, value: T?): Future<T> {
    return Future {
      value?.let {
        val mappedValue = toStringMapper.map(value)
        deviceStorageDataSource.put(query, mappedValue).get()

        return@let value
      } ?: throw IllegalArgumentException("value must be not null")
    }
  }

  override fun putAll(query: Query, value: List<T>?): Future<List<T>> {
    return Future {
      value?.let {
        val mappedValue = toStringFromListMapper.map(value)
        deviceStorageDataSource.put(query, mappedValue).get()
        return@let value
      } ?: throw IllegalArgumentException("value must be not null")
    }
  }

  override fun delete(query: Query): Future<Unit> = deviceStorageDataSource.delete(query)

  override fun deleteAll(query: Query): Future<Unit> = deviceStorageDataSource.deleteAll(query)

}
