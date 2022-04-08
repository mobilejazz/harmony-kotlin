package com.mobilejazz.kmmsample.core.feature.hackerposts.domain.interactor

import com.harmony.kotlin.common.either.Either
import com.harmony.kotlin.domain.interactor.either.GetInteractor
import com.harmony.kotlin.error.HarmonyException
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.HackerNewsQuery
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPost

class GetHackerNewsPostInteractor(
  private val getHackerNewsPostInteractor: GetInteractor<HackerNewsPost>
) {

  suspend operator fun invoke(hackerNewsPostId: Int): Either<HarmonyException, HackerNewsPost> {
    return getHackerNewsPostInteractor(HackerNewsQuery.GetPost(hackerNewsPostId))
  }
}
