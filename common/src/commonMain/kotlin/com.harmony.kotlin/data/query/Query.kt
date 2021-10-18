package com.harmony.kotlin.data.query

// Queries

open class Query

object VoidQuery : Query()

// Single object query
open class ObjectQuery<out T>(val value: T) : Query()

// Collection objects query
open class ObjectsQuery<out T>(val values: Collection<T>) : Query()

// Generic all object query supporting key value
open class AllObjectsQuery : KeyQuery("all-objects-key")

open class IdQuery<out T>(val identifier: T) : KeyQuery(identifier.toString())

open class IntegerIdQuery(val id: Int) : IdQuery<Int>(id)

open class StringIdQuery(val id: String) : IdQuery<String>(id)

open class IdsQuery<out T>(val identifiers: Collection<T>) : KeyQuery(identifiers.toString())

open class IntegerIdsQuery(val ids: Collection<Int>) : IdsQuery<Int>(ids)

open class PaginationQuery(identifier: String) : KeyQuery(identifier)

open class PaginationOffsetLimitQuery(identifier: String, val offset: Int, val limit: Int) : PaginationQuery("$identifier-$offset-$limit")

// Key value queries
open class KeyQuery(val key: String /* key associated to the query */) : Query()

// Extensions
inline fun <reified T> IdQuery<*>.asTyped(): IdQuery<T>? = (this.identifier as? T)?.let { IdQuery(it) }
