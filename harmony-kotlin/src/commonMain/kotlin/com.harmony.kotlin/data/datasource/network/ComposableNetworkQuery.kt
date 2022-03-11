package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.data.query.Query

/**
 * Query that defines a dependency on a NetworkQuery, useful when two different DataSources (Network & any other) needs to use different queries.
 */
interface ComposableNetworkQuery : Query {
  val networkQuery: NetworkQuery
}
