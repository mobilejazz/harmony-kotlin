package com.harmony.kotlin.data.utilities

import com.harmony.kotlin.common.randomInt
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.data.query.anyKeyQuery

fun anyInsertionValue() = InsertionValue(anyKeyQuery(randomString()), randomString())

fun anyInsertionValues(): InsertionValues<String> {
  val elements = randomInt(0, 10)
  val randomValues = (0..elements).map { randomString() }.toList()
  return InsertionValues(anyKeyQuery(randomString()), randomValues)
}
