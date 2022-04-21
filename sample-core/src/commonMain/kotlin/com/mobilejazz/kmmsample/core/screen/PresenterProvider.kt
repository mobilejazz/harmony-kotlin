package com.mobilejazz.kmmsample.core.screen

import com.harmony.kotlin.common.logger.Logger
import com.harmony.kotlin.common.presenter.PresenterViewHolder
import com.mobilejazz.kmmsample.core.feature.hackerposts.HackerNewsPostsComponent
import com.mobilejazz.kmmsample.core.screen.hackerposts.HackerPostDetailDefaultPresenter
import com.mobilejazz.kmmsample.core.screen.hackerposts.HackerPostDetailPresenter
import com.mobilejazz.kmmsample.core.screen.hackerposts.HackerPostsDefaultPresenter
import com.mobilejazz.kmmsample.core.screen.hackerposts.HackerPostsPresenter

interface PresenterComponent {
  fun getHackerPostsPresenter(view: HackerPostsPresenter.View): HackerPostsPresenter
  fun getHackerPostDetailPresenter(view: HackerPostDetailPresenter.View): HackerPostDetailPresenter
}

class PresenterDefaultModule(
  private val logger: Logger,
  private val hackerNewsPostsComponent: HackerNewsPostsComponent
) : PresenterComponent {
  override fun getHackerPostsPresenter(view: HackerPostsPresenter.View): HackerPostsPresenter {
    return HackerPostsDefaultPresenter(
      PresenterViewHolder(view),
      hackerNewsPostsComponent.getHackerNewsPostsInteractor(),
      logger
    )
  }

  override fun getHackerPostDetailPresenter(view: HackerPostDetailPresenter.View): HackerPostDetailPresenter {
    return HackerPostDetailDefaultPresenter(
      PresenterViewHolder(view),
      hackerNewsPostsComponent.getHackerNewsPostInteractor(),
      logger
    )
  }
}
