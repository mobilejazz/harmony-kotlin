package com.harmony.kotlin.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harmony.kotlin.data.error.MappingSerializationException

/**
 * Map a json string to a class object
 */
class StringToModelMapper<out T>(private val clz: Class<T>, private val gson: Gson) : Mapper<String, T> {
  override fun map(from: String): T = try {
    gson.fromJson(from, clz)
  } catch (e: Exception) {
    throw MappingSerializationException(cause = e)
  }
}

/**
 * Map a class object to a json string
 */
class ModelToStringMapper<in T>(private val gson: Gson) : Mapper<T, String> {
  override fun map(from: T): String = try {
    gson.toJson(from)
  } catch (e: Exception) {
    throw MappingSerializationException(cause = e)
  }
}

/**
 * Map a list json string to a list class object
 */
class StringToListModelMapper<out T>(private val clz: TypeToken<List<T>>, private val gson: Gson) : Mapper<String, List<T>> {
  override fun map(from: String): List<T> = try {
    gson.fromJson(from, clz.type)
  } catch (e: Exception) {
    throw MappingSerializationException(cause = e)
  }
}

/**
 * Map a list class object to a json string
 */
class ListModelToStringMapper<in T>(private val gson: Gson) : Mapper<List<T>, String> {
  override fun map(from: List<T>): String = try {
    gson.toJson(from)
  } catch (e: Exception) {
    throw MappingSerializationException(cause = e)
  }
}
