package com.mobilejazz.kmmsample.core.feature.hackerposts.domain.interactor

import com.harmony.kotlin.domain.interactor.GetInteractor
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.HackerNewsQuery
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPost

class GetHackerNewsPostInteractor(
  private val getHackerNewsPostInteractor: GetInteractor<HackerNewsPost>
) {

  suspend operator fun invoke(hackerNewsPostId: Int): HackerNewsPost {
    return getHackerNewsPostInteractor(HackerNewsQuery.GetPost(hackerNewsPostId))
  }
}
