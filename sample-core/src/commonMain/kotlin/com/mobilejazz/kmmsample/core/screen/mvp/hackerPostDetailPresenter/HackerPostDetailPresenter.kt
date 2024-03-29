package com.mobilejazz.kmmsample.core.screen.mvp.hackerPostDetailPresenter

import com.harmony.kotlin.common.logger.Logger
import com.harmony.kotlin.common.presenter.PresenterViewHolder
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.interactor.GetHackerNewsPostInteractor
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface HackerPostDetailPresenter {
  fun onViewLoaded(hackerNewsPostId: Long)

  interface View {
    fun onDisplayLoading()
    fun onDisplayHackerPost(hackerNewsPost: HackerNewsPost)
    fun onFailedWithFullScreenError(t: Throwable, retryBlock: () -> Unit)
  }
}

class HackerPostDetailDefaultPresenter(
  private val view: PresenterViewHolder<HackerPostDetailPresenter.View>,
  private val getHackerNewsPostsInteractor: GetHackerNewsPostInteractor,
  private val logger: Logger
) : HackerPostDetailPresenter, CoroutineScope {

  private val tag = "HackerPostDetailDefaultPresenter"

  override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.Main

  private val job = Job()

  override fun onViewLoaded(hackerNewsPostId: Long) {
    loadPostWithId(hackerNewsPostId)
  }

  private fun loadPostWithId(hackerNewsPostId: Long) {
    launch {
      view.get()?.onDisplayLoading()
      getHackerNewsPostsInteractor(hackerNewsPostId).fold(
        ifRight = {
          view.get()?.onDisplayHackerPost(it)
        },
        ifLeft = {
          view.get()?.onFailedWithFullScreenError(it) { loadPostWithId(hackerNewsPostId) }
        }
      )
    }
  }
}
