package com.mobilejazz.kotlin.core.repository.mapper


class VoidMapper<in From, out To> : Mapper<From, To> {
  override fun map(from: From): To {
    throw UnsupportedOperationException()
  }
}