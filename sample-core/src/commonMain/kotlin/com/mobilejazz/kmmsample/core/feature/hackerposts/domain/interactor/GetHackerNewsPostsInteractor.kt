package com.mobilejazz.kmmsample.core.feature.hackerposts.domain.interactor

import com.harmony.kotlin.data.datasource.network.NetworkQuery
import com.harmony.kotlin.data.error.DataNotFoundException
import com.harmony.kotlin.domain.interactor.GetInteractor
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPost
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPosts
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPostsIds

class GetHackerNewsPostsInteractor(
  private val getHackerNewsIdsPostsInteractor: GetInteractor<HackerNewsPostsIds>,
  private val getHackerNewsPostInteractor: GetInteractor<HackerNewsPost>
) {
  suspend operator fun invoke(): HackerNewsPosts {
    return runCatching {
      getHackerNewsIdsPostsInteractor(
        NetworkQuery(
          NetworkQuery.Method.Get,
          "askstories.json",
          key = "hacker-news-ids"
        )
        // To speed up first load. Next iteration, pagination.
      ).listIds.take(5).map { postId ->
        getHackerNewsPostInteractor(
          NetworkQuery(
            NetworkQuery.Method.Get,
            "item/$postId.json",
            key = "item-$postId"
          )
        )
      }
    }.onSuccess {
      return it
    }.onFailure {
      throw DataNotFoundException()
    }.getOrThrow()
  }
}
