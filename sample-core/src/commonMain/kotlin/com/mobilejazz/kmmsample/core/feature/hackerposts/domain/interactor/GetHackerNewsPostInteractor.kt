package com.mobilejazz.kmmsample.core.feature.hackerposts.domain.interactor

import com.harmony.kotlin.common.either.Either
import com.harmony.kotlin.domain.interactor.either.GetInteractor
import com.harmony.kotlin.error.HarmonyException
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.HackerNewsQuery
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPost
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetHackerNewsPostInteractor(
  private val coroutineContext: CoroutineContext,
  private val getHackerNewsPostInteractor: GetInteractor<HackerNewsPost>
) {

  suspend operator fun invoke(hackerNewsPostId: Long): Either<HarmonyException, HackerNewsPost> {
    return withContext(coroutineContext) {
      getHackerNewsPostInteractor(HackerNewsQuery.GetPost(hackerNewsPostId))
    }
  }
}
