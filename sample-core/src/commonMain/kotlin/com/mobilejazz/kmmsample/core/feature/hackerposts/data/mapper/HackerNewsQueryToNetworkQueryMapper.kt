package com.mobilejazz.kmmsample.core.feature.hackerposts.data.mapper

import com.harmony.kotlin.data.datasource.network.NetworkQuery
import com.harmony.kotlin.data.mapper.Mapper
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.HackerNewsQuery

class HackerNewsQueryToNetworkQueryMapper : Mapper<HackerNewsQuery, NetworkQuery> {
  override fun map(from: HackerNewsQuery): NetworkQuery {
    return when (from) {
      HackerNewsQuery.GetAll -> {
        NetworkQuery(
          NetworkQuery.Method.Get,
          "askstories.json"
        )
      }
      is HackerNewsQuery.GetPost -> {
        NetworkQuery(
          NetworkQuery.Method.Get,
          "item/${from.postId}.json",
        )
      }
    }
  }
}
