package com.harmony.kotlin.data.mapper

import com.harmony.kotlin.data.error.MappingException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class ObjectToByteArrayMapper<T>(
  private val serializer: KSerializer<T>,
  private val json: Json
) : Mapper<T, ByteArray> {
  override fun map(from: T): ByteArray =
    try {
      json.encodeToString(serializer, from).encodeToByteArray()
    } catch (e: SerializationException) {
      throw MappingException(cause = e)
    }
}

class ByteArrayToObjectMapper<T>(
  private val serializer: KSerializer<T>,
  private val json: Json
) : Mapper<ByteArray, T> {
  override fun map(from: ByteArray): T =
    try {
      json.decodeFromString(serializer, from.decodeToString())
    } catch (e: SerializationException) {
      throw MappingException(cause = e)
    }
}

class ObjectListToByteArrayMapper<T>(
  private val serializer: KSerializer<T>,
  private val json: Json
) : Mapper<List<T>, ByteArray> {
  override fun map(from: List<T>): ByteArray =
    try {
      json.encodeToString(ListSerializer(serializer), from).encodeToByteArray()
    } catch (e: SerializationException) {
      throw MappingException(cause = e)
    }
}

class ByteArrayToObjectListMapper<T>(
  private val serializer: KSerializer<T>,
  private val json: Json
) : Mapper<ByteArray, List<T>> {
  override fun map(from: ByteArray): List<T> =
    try {
      json.decodeFromString(ListSerializer(serializer), from.decodeToString())
    } catch (e: SerializationException) {
      throw MappingException(cause = e)
    }
}
