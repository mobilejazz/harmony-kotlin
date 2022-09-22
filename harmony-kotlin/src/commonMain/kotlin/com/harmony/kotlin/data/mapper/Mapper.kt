package com.harmony.kotlin.data.mapper

/**
 * Interface to map an object type to another object type
 */
interface Mapper<in From, out To> {

  fun map(from: From): To
}

class ClosureMapper<in From, out To>(val closure: (from: From) -> To) : Mapper<From, To> {

  override fun map(from: From): To = closure(from)
}

/**
 * Mapping method for lists
 */
fun <From, To> Mapper<From, To>.map(values: List<From>): List<To> = values.map { map(it) }

/**
 * Mapping method for Maps
 *
 * @param value A Map<K, From> of ket-value, where value is typed as "From"
 * @return A Map<K, To> of mapped values
 */
fun <From, To, K> Mapper<From, To>.map(value: Map<K, From>): Map<K, To> {
  return value.mapValues { map(it.value) }
}
