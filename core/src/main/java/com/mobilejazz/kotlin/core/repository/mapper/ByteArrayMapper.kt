package com.mobilejazz.kotlin.core.repository.mapper

import java.io.*

/**
 * Map a byte array to a class object
 */
class ByteArrayToModelMapper<out T : Serializable> : Mapper<ByteArray, T> {
  override fun map(from: ByteArray): T = from.toObject()
}

/**
 * Map a class object to a byte array
 */
class ModelToByteArrayMapper<in T : Serializable> : Mapper<T, ByteArray> {
  override fun map(from: T): ByteArray = from.toByteArray()
}

/**
 * Map a byte array to a list class object
 */
class ByteArrayToListModelMapper<out T : Serializable> : Mapper<ByteArray, List<T>> {
  override fun map(from: ByteArray): List<T> {
    return from.toObject()
  }
}

/**
 * Map a list class object to a byte array
 */
class ListModelToByteArrayMapper<in T : Serializable> : Mapper<List<T>, ByteArray> {
  override fun map(from: List<T>): ByteArray = from.toByteArray()
}

private fun Collection<Serializable>.toByteArray(): ByteArray {
  val bos = ByteArrayOutputStream()
  return ObjectOutputStream(bos).use { oos ->
    oos.writeObject(this)
    oos.flush()
    bos.toByteArray()
  }
}

private fun Serializable.toByteArray(): ByteArray {
  val bos = ByteArrayOutputStream()
  return ObjectOutputStream(bos).use { oos ->
    oos.writeObject(this)
    oos.flush()
    bos.toByteArray()
  }
}

private fun <T> ByteArray.toObject(): T {
  return ObjectInputStream(ByteArrayInputStream(this)).use { ois ->
    ois.readObject() as T
  }
}