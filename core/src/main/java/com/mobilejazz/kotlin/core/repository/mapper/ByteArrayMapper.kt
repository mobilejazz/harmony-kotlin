package com.mobilejazz.kotlin.core.repository.mapper

import java.io.*

/**
 * Map a ByteArray to a class object
 */
class ByteArrayToModelMapper<out T> : ObjectToListMapper<ByteArray, T> {
  override fun map(from: ByteArray): T = from.toObject() as T

  override fun mapToList(from: ByteArray): List<T> = from.toObject()
}

/**
 * Map a class object to a ByteArray
 */
class ModelToByteArrayMapper<in T : Serializable> : ListToObjectMapper<T, ByteArray> {
  override fun map(from: T): ByteArray = from.toByteArray()

  override fun mapToObject(from: List<T>): ByteArray = from.toByteArray()
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