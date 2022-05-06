package com.mobilejazz.kmmsample.core.feature.hackerposts.domain.interactor

import com.harmony.kotlin.common.either.Either
import com.harmony.kotlin.common.either.asSingleEither
import com.harmony.kotlin.common.either.flatMap
import com.harmony.kotlin.domain.interactor.either.GetInteractor
import com.harmony.kotlin.error.HarmonyException
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.HackerNewsQuery
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPost
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPosts
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPostsIds

class GetHackerNewsPostsInteractor(
  private val getHackerNewsIdsPostsInteractor: GetInteractor<HackerNewsPostsIds>,
  private val getHackerNewsPostInteractor: GetInteractor<HackerNewsPost>
) {
  suspend operator fun invoke(): Either<HarmonyException, HackerNewsPosts> {
    return getHackerNewsIdsPostsInteractor<HarmonyException>(
      HackerNewsQuery.GetAll
    ).flatMap {
      it.listIds.take(5).map { postId ->
        getHackerNewsPostInteractor<HarmonyException>(
          HackerNewsQuery.GetPost(postId)
        )
      }.asSingleEither()
    }
  }
}
