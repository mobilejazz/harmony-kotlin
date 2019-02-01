package com.mobilejazz.kotlin.core.repository.datasource.file

import com.mobilejazz.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.threading.extensions.Future
import java.io.EOFException
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


class FileStreamValueDataStorage<T>(val file: File) : GetDataSource<T>, PutDataSource<T>, DeleteDataSource {
  override fun get(query: Query): Future<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getAll(query: Query): Future<List<T>> {
    return Future {
      val values = mutableListOf<T>()
      val fin = file.inputStream()
      val ois = ObjectInputStream(fin)
      try {
        while (true) {
          val value = ois.readObject() as T
          values.add(value)
        }
      } catch (e: EOFException) {
        ois.close()
        fin.close()
      }

      return@Future values
    }
  }

  override fun put(query: Query, value: T?): Future<T> {
    return Future {
      return@Future value?.let {
        putAll(query, listOf(value)).get()[0]
      } ?: throw IllegalArgumentException("FileStreamValueDataStorage: value must be not null")
    }
  }

  override fun putAll(query: Query, value: List<T>?): Future<List<T>> {
    return Future {
      value?.let {
        val fos = file.outputStream()
        val oos = ObjectOutputStream(fos)
        for (obj in value) {
          oos.writeObject(obj)
        }
        oos.flush()
        oos.close()
        fos.close()
        return@Future value ?: notSupportedQuery()
      } ?: throw IllegalArgumentException("FileStreamValueDataStorage: value must be not null")
    }
  }

  override fun delete(query: Query): Future<Unit> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun deleteAll(query: Query): Future<Unit> {
    return Future {
      val outputStream = file.outputStream()
      val objectOutputStream = ObjectOutputStream(outputStream)
      objectOutputStream.reset()
    }
  }

}