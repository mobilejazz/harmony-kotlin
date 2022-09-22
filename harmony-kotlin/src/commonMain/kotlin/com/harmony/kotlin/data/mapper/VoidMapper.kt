package com.harmony.kotlin.data.mapper

class VoidMapper<in From, out To> : Mapper<From, To> {
  override fun map(from: From): To {
    throw UnsupportedOperationException("VoidMapper is not implemented!")
  }
}
