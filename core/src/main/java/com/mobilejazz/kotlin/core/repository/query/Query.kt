package com.mobilejazz.kotlin.core.repository.query

// Queries

open class Query

object EmptyQuery : Query()

open class ByIdentifierQuery<out T>(val identifier: T) : Query()

open class ByIdentifiersQuery<out T>(val identifiers: List<T>) : Query()

open class PaginationQuery : Query()

open class PaginationOffsetLimitQuery(val offset: Int, val limit: Int) : PaginationQuery()

open class UpdateModelQuery : Query()

open class InsertModelQuery : Query()

// Key value queries

open class KeyQuery<out T>(val key: T) : Query()

open class StringKeyQuery(key: String) : KeyQuery<String>(key)