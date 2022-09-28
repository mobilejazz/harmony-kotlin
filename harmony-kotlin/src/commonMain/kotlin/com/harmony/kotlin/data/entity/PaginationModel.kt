package com.harmony.kotlin.data.entity

open class Pagination<T>(val values: List<T>)

class PaginationOffsetLimit<T>(
  values: List<T>,
  val offset: Int = 0,
  val limit: Int = 10,
  val size: Int = 10
) : Pagination<T>(values)

class PaginationPage<T>(values: List<T>) : Pagination<T>(values)
