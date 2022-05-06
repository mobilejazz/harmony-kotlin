package com.harmony.kotlin.common.sqldelight

import com.harmony.kotlin.error.DataNotFoundException
import com.squareup.sqldelight.Query

fun <T : Any> Query<T>.executeAsListWithDataNotFound(): List<T> {
  return this.executeAsList().ifEmpty {
    throw DataNotFoundException()
  }
}

fun <T : Any> Query<T>.executeAsOneWithDataNotFound(): T {
  try {
    return executeAsOne()
  } catch (e: NullPointerException) {
    throw DataNotFoundException()
  }
}
