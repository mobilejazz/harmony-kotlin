package com.harmony.kotlin.data.mapper

import com.harmony.kotlin.error.DataSerializationException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * Map a json string to a class object
 */
class ModelToJsonStringMapper<in T>(
  private val serializer: KSerializer<T>,
  private val json: Json = Json
) : Mapper<T, String> {
  override fun map(from: T): String = try {
    json.encodeToString(serializer, from)
  } catch (e: SerializationException) {
    throw DataSerializationException(cause = e)
  }
}

@Deprecated(message = "Replaced by ModelToJsonStringMapper")
class ListModelToJsonStringMapper<in T>(
  private val serializer: KSerializer<T>,
  private val json: Json = Json
) : Mapper<List<T>, String> {
  override fun map(from: List<T>): String = try {
    json.encodeToString(ListSerializer(serializer), from)
  } catch (e: SerializationException) {
    throw DataSerializationException(cause = e)
  }
}

/**
 * Map a json string to a class object
 */
class JsonStringToModelMapper<out T>(
  private val serializer: KSerializer<T>,
  private val json: Json = Json
) : Mapper<String, T> {
  override fun map(from: String): T = try {
    json.decodeFromString(serializer, from)
  } catch (e: SerializationException) {
    throw DataSerializationException(cause = e)
  }
}

/**
 * Map a list json string to a list class object
 */
@Deprecated(message = "Replaced by JsonStringToModelMapper")
class JsonStringToListModelMapper<out T>(
  private val serializer: KSerializer<T>,
  private val json: Json = Json
) : Mapper<String, List<T>> {
  override fun map(from: String): List<T> = try {
    json.decodeFromString(ListSerializer(serializer), from)
  } catch (e: SerializationException) {
    throw DataSerializationException(cause = e)
  }
}
