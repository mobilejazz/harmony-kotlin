package com.mobilejazz.kmmsample.core.screen.hackerposts

import com.harmony.kotlin.common.WeakReference
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.interactor.GetHackerNewsPostsInteractor
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPosts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface HackerPostsPresenter {
  fun onViewLoaded()
  fun onDetachView()

  interface View {
    fun onDisplayHackerPostList(hackerNewsPosts: HackerNewsPosts)
    fun onFailedLoadHackerPostList(e: Exception)
  }
}

class HackerPostsDefaultPresenter(
  private val view: WeakReference<HackerPostsPresenter.View>,
  private val getHackerNewsPostsInteractor: GetHackerNewsPostsInteractor
) : HackerPostsPresenter, CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.Main

  private val job = Job()

  override fun onViewLoaded() {
    launch {
      try {
        view.get()?.onDisplayHackerPostList(getHackerNewsPostsInteractor())
      } catch (e: Exception) {
        view.get()?.onFailedLoadHackerPostList(e)
      }
    }
  }

  override fun onDetachView() {
    view.clear()
  }
}
