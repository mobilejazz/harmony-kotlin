package com.harmony.kotlin.data.mapper

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.cbor.*

@ExperimentalSerializationApi
class CBORObjectToByteArray<T>(private val cbor: Cbor, private val serializer: KSerializer<T>): Mapper<T, ByteArray> {
    override fun map(from: T): ByteArray = cbor.encodeToByteArray(serializer, from)
}

@ExperimentalSerializationApi
class CBORListObjectToByteArray<T>(private val cbor: Cbor, private val serializer: KSerializer<T>): Mapper<List<T>, ByteArray> {
    override fun map(from: List<T>): ByteArray {
        val ls = ListSerializer(serializer)
        return cbor.encodeToByteArray(ls, from)
    }
}

@ExperimentalSerializationApi
class CBORByteArrayToObject<T>(private val cbor: Cbor, private val serializer: KSerializer<T>): Mapper<ByteArray, T> {
    override fun map(from: ByteArray): T = cbor.decodeFromByteArray(serializer, from)
}

@ExperimentalSerializationApi
class CBORByteArrayToListObject<T>(private val cbor: Cbor, private val serializer: KSerializer<T>): Mapper<ByteArray, List<T>> {
    override fun map(from: ByteArray): List<T> {
        val ls = ListSerializer(serializer)
        return cbor.decodeFromByteArray(ls, from)
    }
}


