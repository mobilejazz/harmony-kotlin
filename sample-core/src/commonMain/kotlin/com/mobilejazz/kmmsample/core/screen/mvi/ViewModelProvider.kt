package com.mobilejazz.kmmsample.core.screen.mvi

import com.harmony.kotlin.common.logger.Logger
import com.mobilejazz.kmmsample.core.feature.hackerposts.HackerNewsPostsComponent
import com.mobilejazz.kmmsample.core.screen.mvi.hackerPostDetail.HackerPostDetailViewModel
import com.mobilejazz.kmmsample.core.screen.mvi.hackerPosts.HackerPostsViewModel

interface ViewModelComponent {
  fun getHackerPostsViewModel(): HackerPostsViewModel
  fun getHackerPostDetailViewModel(postId: Long): HackerPostDetailViewModel
}

class ViewModelDefaultModule(
  private val logger: Logger,
  private val hackerNewsPostsComponent: HackerNewsPostsComponent
) : ViewModelComponent {
  override fun getHackerPostsViewModel(): HackerPostsViewModel {
    return HackerPostsViewModel(
      hackerNewsPostsComponent.getHackerNewsPostsInteractor(),
      logger
    )
  }

  override fun getHackerPostDetailViewModel(postId: Long): HackerPostDetailViewModel {
    return HackerPostDetailViewModel(postId, hackerNewsPostsComponent.getHackerNewsPostInteractor(), logger)
  }
}
