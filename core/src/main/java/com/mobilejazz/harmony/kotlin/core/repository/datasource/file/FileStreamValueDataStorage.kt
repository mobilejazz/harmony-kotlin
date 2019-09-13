package com.mobilejazz.harmony.kotlin.core.repository.datasource.file

import com.mobilejazz.harmony.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import java.io.EOFException
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class FileStreamValueDataStorage<T>(val file: File) : GetDataSource<T>, PutDataSource<T>, DeleteDataSource {

  override suspend fun get(query: Query): T = TODO("not implemented")

  override suspend fun getAll(query: Query): List<T> {
    val values = mutableListOf<T>()
    val fin = file.inputStream()
    var ois: ObjectInputStream? = null
    try {
      ois = ObjectInputStream(fin)

      while (true) {
        val value = ois.readObject() as T
        values.add(value)
      }
    } catch (e: EOFException) {
      ois?.close()
      fin.close()
    }

    return values
  }

  override suspend fun put(query: Query, value: T?): T = value?.let {
    putAll(query, listOf(value))[0]
  } ?: throw IllegalArgumentException("FileStreamValueDataStorage: value must be not null")

  override suspend fun putAll(query: Query, value: List<T>?): List<T> =
      value?.let {
        val allCurrentValues = getAll(query)
        val fos = file.outputStream()
        val oos = ObjectOutputStream(fos)

        val allValues = value.toMutableList()
        allValues.addAll(allCurrentValues)

        for (obj in allValues) {
          oos.writeObject(obj)
        }
        oos.flush()
        oos.close()
        fos.close()
        return value
      } ?: throw IllegalArgumentException("FileStreamValueDataStorage: value must be not null")

  override suspend fun delete(query: Query) = TODO("not implemented")

  override suspend fun deleteAll(query: Query) {
    val outputStream = file.outputStream()
    val objectOutputStream = ObjectOutputStream(outputStream)
    objectOutputStream.reset()
  }
}