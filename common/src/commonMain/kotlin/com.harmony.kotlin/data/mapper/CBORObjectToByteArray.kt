package com.harmony.kotlin.data.mapper

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.*
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.decodeFromByteArray

@ExperimentalSerializationApi
class CBORObjectToByteArray<T>(private val cbor: Cbor, private val serializer: KSerializer<T>): Mapper<T, ByteArray> {
    override fun map(from: T): ByteArray = cbor.encodeToByteArray(serializer, from)
}

@ExperimentalSerializationApi
class CBORListObjectToByteArray<T>(private val cbor: Cbor): Mapper<List<T>, ByteArray> {
    override fun map(from: List<T>): ByteArray = cbor.encodeToByteArray(from)
}

@ExperimentalSerializationApi
class CBORByteArrayToObject<T>(private val cbor: Cbor, private val serializer: KSerializer<T>): Mapper<ByteArray, T> {
    override fun map(from: ByteArray): T = cbor.decodeFromByteArray(serializer, from)
}

@ExperimentalSerializationApi
class CBORByteArrayToListObject<T>(private val cbor: Cbor): Mapper<ByteArray, List<T>> {
    override fun map(from: ByteArray): List<T> = cbor.decodeFromByteArray(from)
}


