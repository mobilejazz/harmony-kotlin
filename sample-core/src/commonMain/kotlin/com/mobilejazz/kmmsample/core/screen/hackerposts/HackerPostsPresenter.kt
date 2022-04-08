package com.mobilejazz.kmmsample.core.screen.hackerposts

import com.harmony.kotlin.common.WeakReference
import com.harmony.kotlin.common.logger.Logger
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
    fun onDisplayLoading()
    fun onDisplayHackerPostList(hackerNewsPosts: HackerNewsPosts)
    fun onFailedWithFullScreenError(t: Throwable, retryBlock: () -> Unit)
  }
}

class HackerPostsDefaultPresenter(
  private val view: WeakReference<HackerPostsPresenter.View>,
  private val getHackerNewsPostsInteractor: GetHackerNewsPostsInteractor,
  private val logger: Logger
) : HackerPostsPresenter, CoroutineScope {

  private val tag = "HackerPostsDefaultPresenter"

  override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.Main

  private val job = Job()

  override fun onViewLoaded() {
    loadPosts()
  }

  private fun loadPosts() {
    view.get()?.onDisplayLoading()
    launch {
        getHackerNewsPostsInteractor().fold(
          ifRight = {
            view.get()?.onDisplayHackerPostList(it)
          },
          ifLeft = {
            view.get()?.onFailedWithFullScreenError(it, retryBlock = { loadPosts() })
          }
        )
    }
  }

  override fun onDetachView() {
    view.clear()
  }
}
