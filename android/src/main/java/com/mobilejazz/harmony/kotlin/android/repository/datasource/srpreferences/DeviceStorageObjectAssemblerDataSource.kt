package com.mobilejazz.harmony.kotlin.android.repository.datasource.srpreferences

import com.mobilejazz.harmony.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.harmony.kotlin.core.repository.mapper.Mapper
import com.mobilejazz.harmony.kotlin.core.repository.query.Query

class DeviceStorageObjectAssemblerDataSource<T>(
    private val toStringMapper: Mapper<T, String>,
    private val toModelMapper: Mapper<String, T>,
    private val toStringFromListMapper: Mapper<List<T>, String>,
    private val toModelFromListString: Mapper<String, List<T>>,
    private val deviceStorageDataSource: DeviceStorageDataSource<String>
) : GetDataSource<T>, PutDataSource<T>, DeleteDataSource {

  override suspend fun get(query: Query): T = deviceStorageDataSource.get(query).let(toModelMapper)

  override suspend fun getAll(query: Query): List<T> = deviceStorageDataSource.get(query).let(toModelFromListString)

  override suspend fun put(query: Query, value: T?): T {
    return value?.let {
      val mappedValue = toStringMapper.map(value)
      deviceStorageDataSource.put(query, mappedValue)
      return@let value
    } ?: throw IllegalArgumentException("value must be not null")
  }


  override suspend fun putAll(query: Query, value: List<T>?): List<T> {
    return value?.let {
      val mappedValue = toStringFromListMapper.map(value)
      deviceStorageDataSource.put(query, mappedValue)
      return@let value
    } ?: throw IllegalArgumentException("value must be not null")
  }

  override suspend fun delete(query: Query) = deviceStorageDataSource.delete(query)

  override suspend fun deleteAll(query: Query) = deviceStorageDataSource.deleteAll(query)

}
