package com.mobilejazz.kmmsample.core.screen.mvp

import com.harmony.kotlin.common.logger.Logger
import com.harmony.kotlin.common.presenter.PresenterViewHolder
import com.mobilejazz.kmmsample.core.feature.hackerposts.HackerNewsPostsComponent
import com.mobilejazz.kmmsample.core.screen.mvp.hackerPostDetailPresenter.HackerPostDetailDefaultPresenter
import com.mobilejazz.kmmsample.core.screen.mvp.hackerPostDetailPresenter.HackerPostDetailPresenter
import com.mobilejazz.kmmsample.core.screen.mvp.hackerPosts.HackerPostsDefaultPresenter
import com.mobilejazz.kmmsample.core.screen.mvp.hackerPosts.HackerPostsPresenter

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
