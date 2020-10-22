package com.harmony.kotlin.data.mapper

import com.harmony.kotlin.data.error.MappingSerializationException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.cbor.*

class CBORObjectToByteArray<T>(private val cbor: Cbor, private val serializer: KSerializer<T>) : Mapper<T, ByteArray> {
  override fun map(from: T): ByteArray = try {
    cbor.dump(serializer, from)
  } catch (e: Exception) {
    throw MappingSerializationException(cause = e)
  }
}

class CBORListObjectToByteArray<T>(private val cbor: Cbor, private val serializer: KSerializer<T>) : Mapper<List<T>, ByteArray> {
  override fun map(from: List<T>): ByteArray = try {
    cbor.dump(serializer.list, from)
  } catch (e: Exception) {
    throw MappingSerializationException(cause = e)
  }
}

class CBORByteArrayToObject<T>(private val cbor: Cbor, private val serializer: KSerializer<T>) : Mapper<ByteArray, T> {
  override fun map(from: ByteArray): T = try {
    cbor.load(serializer, from)
  } catch (e: Exception) {
    throw MappingSerializationException(cause = e)
  }
}

class CBORByteArrayToListObject<T>(private val cbor: Cbor, private val serializer: KSerializer<T>) : Mapper<ByteArray, List<T>> {

  override fun map(from: ByteArray): List<T> = try {
    cbor.load(serializer.list, from)
  } catch (e: Exception) {
    throw MappingSerializationException(cause = e)
  }
}


