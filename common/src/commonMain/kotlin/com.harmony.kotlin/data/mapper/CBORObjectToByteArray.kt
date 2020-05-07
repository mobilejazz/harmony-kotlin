package com.harmony.kotlin.data.mapper

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.cbor.*

class CBORObjectToByteArray<T>(private val cbor: Cbor, private val serializer: KSerializer<T>): Mapper<T, ByteArray> {
    override fun map(from: T): ByteArray = cbor.dump(serializer, from)
}

class CBORListObjectToByteArray<T>(private val cbor: Cbor, private val serializer: KSerializer<T>): Mapper<List<T>, ByteArray> {
    override fun map(from: List<T>): ByteArray = cbor.dump(serializer.list, from)
}

class CBORByteArrayToObject<T>(private val cbor: Cbor, private val serializer: KSerializer<T>): Mapper<ByteArray, T> {
    override fun map(from: ByteArray): T = cbor.load(serializer, from)
}

class CBORByteArrayToListObject<T>(private val cbor: Cbor, private val serializer: KSerializer<T>): Mapper<ByteArray, List<T>> {
    override fun map(from: ByteArray): List<T> = cbor.load(serializer.list, from)
}


