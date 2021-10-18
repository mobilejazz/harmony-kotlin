package com.harmony.kotlin.data.mapper

import com.harmony.kotlin.data.error.MappingSerializationException
import java.io.*

/**
 * Map a byte array to a class object
 */
class ByteArrayToModelMapper<out T : Serializable> : Mapper<ByteArray, T> {
  override fun map(from: ByteArray): T = try {
    from.toObject()
  } catch (e: Exception) {
    throw MappingSerializationException(cause = e)
  }
}

/**
 * Map a class object to a byte array
 */
class ModelToByteArrayMapper<in T : Serializable> : Mapper<T, ByteArray> {
  override fun map(from: T): ByteArray = try {
    from.toByteArray()
  } catch (e: Exception) {
    throw MappingSerializationException(cause = e)
  }
}

/**
 * Map a byte array to a list class object
 */
class ByteArrayToListModelMapper<out T : Serializable> : Mapper<ByteArray, List<T>> {
  override fun map(from: ByteArray): List<T> = try {
    from.toObject()
  } catch (e: Exception) {
    throw MappingSerializationException(cause = e)
  }
}

/**
 * Map a list class object to a byte array
 */
class ListModelToByteArrayMapper<in T : Serializable> : Mapper<List<T>, ByteArray> {
  override fun map(from: List<T>): ByteArray = try {
    from.toByteArray()
  } catch (e: Exception) {
    throw MappingSerializationException(cause = e)
  }
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
