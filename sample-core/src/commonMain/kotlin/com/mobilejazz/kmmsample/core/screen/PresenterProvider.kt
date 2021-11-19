package com.mobilejazz.kmmsample.core.screen

import com.harmony.kotlin.common.WeakReference
import com.mobilejazz.kmmsample.core.feature.hackerposts.HackerNewsPostsComponent
import com.mobilejazz.kmmsample.core.screen.hackerposts.HackerPostsDefaultPresenter
import com.mobilejazz.kmmsample.core.screen.hackerposts.HackerPostsPresenter

interface PresenterComponent {
  fun getHackerPostsPresenter(view: HackerPostsPresenter.View): HackerPostsPresenter
}

class DefaultPresenterModule(private val hackerNewsPostsComponent: HackerNewsPostsComponent) : PresenterComponent {
  override fun getHackerPostsPresenter(view: HackerPostsPresenter.View): HackerPostsPresenter {
    return HackerPostsDefaultPresenter(
      WeakReference(view),
      hackerNewsPostsComponent.getHackerNewsPostsInteractor()
    )
  }
}
