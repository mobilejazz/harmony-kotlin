package com.harmony.kotlin.data.utilities

import com.harmony.kotlin.common.randomByteArray
import com.harmony.kotlin.common.randomByteArrayList
import com.harmony.kotlin.common.randomInt
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.data.query.anyKeyQuery

fun <T> anyInsertionValue(key: String = randomString(), value: T) = InsertionValue(anyKeyQuery(key), value)

fun anyInsertionValue() = InsertionValue(anyKeyQuery(randomString()), randomString())

fun anyByteArrayInsertionValue() = anyInsertionValue(value = randomByteArray())

fun anyInsertionValues(): InsertionValues<String> {
  val elements = randomInt(0, 10)
  val randomValues = (0..elements).map { randomString() }.toList()
  return anyInsertionValues(values = randomValues)
}

fun <T> anyInsertionValues(key: String = randomString(), values: List<T>): InsertionValues<T> {
  return InsertionValues(anyKeyQuery(key), values)
}

fun anyByteArrayInsertionValues(values: List<ByteArray> = randomByteArrayList()): InsertionValues<ByteArray> {
  return anyInsertionValues(values = values)
}
