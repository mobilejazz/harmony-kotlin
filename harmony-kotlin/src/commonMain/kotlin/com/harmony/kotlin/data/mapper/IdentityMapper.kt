package com.harmony.kotlin.data.mapper

/**
 * Mapper that returns the same object obtained
 */
class IdentityMapper<T> : Mapper<T, T> {
  override fun map(from: T): T {
    return from
  }
}
