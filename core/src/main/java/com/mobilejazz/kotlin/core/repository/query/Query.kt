package com.mobilejazz.kotlin.core.repository.query

// Queries

open class Query

object VoidQuery : Query()

open class ByIdentifierQuery<out T>(val identifier: T) : StringKeyQuery(identifier.toString())

open class ByIdentifierIntegerQuery(id: Int): ByIdentifierQuery<Int>(id)

open class ByIdentifierStringQuery(id: String): ByIdentifierQuery<String>(id)

open class ByIdentifiersQuery<out T>(val identifiers: List<T>) : StringKeyQuery(identifiers.toString())

open class ByIdentifiersIntegerQuery(ids: List<Int>): ByIdentifiersQuery<Int>(ids)

open class PaginationQuery : Query()

open class PaginationOffsetLimitQuery(val offset: Int, val limit: Int) : PaginationQuery()

open class UpdateModelQuery : Query()

open class InsertModelQuery : Query()

// Key value queries

open class KeyQuery<out T>(val key: T) : Query()

open class StringKeyQuery(key: String) : KeyQuery<String>(key)