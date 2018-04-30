package com.mobilejazz.kotlin.core.datasource

// Queries

open class Query

open class ByIdentifierQuery<out T>(val identifier: T) : Query()

open class ByIdentifiersQuery<out T>(val identifiers: List<T>) : Query()

open class PaginationQuery : Query()

open class PaginationOffsetLimitQuery(val offset: Int, val limit: Int) : PaginationQuery()

open class UpdateModelQuery: Query()

open class InsertModelQuery: Query()