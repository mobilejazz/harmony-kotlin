package com.mobilejazz.kmmsample.core.screen.hackerposts

import com.harmony.kotlin.common.WeakReference
import com.harmony.kotlin.common.logger.Logger
import com.harmony.kotlin.common.onComplete
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.interactor.GetHackerNewsPostInteractor
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface HackerPostDetailPresenter {
  fun onViewLoaded(hackerNewsPostId: Int)
  fun onDetachView()

  interface View {
    fun onDisplayLoading()
    fun onDisplayHackerPost(hackerNewsPost: HackerNewsPost)
    fun onFailedWithFullScreenError(t: Throwable, retryBlock: () -> Unit)
  }
}

class HackerPostDetailDefaultPresenter(
  private val view: WeakReference<HackerPostDetailPresenter.View>,
  private val getHackerNewsPostsInteractor: GetHackerNewsPostInteractor,
  private val logger: Logger
) : HackerPostDetailPresenter, CoroutineScope {

  private val tag = "HackerPostDetailDefaultPresenter"

  override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.Main

  private val job = Job()

  override fun onViewLoaded(hackerNewsPostId: Int) {
    loadPostWithId(hackerNewsPostId)
  }

  private fun loadPostWithId(hackerNewsPostId: Int) {
    launch {
      runCatching {
        view.get()?.onDisplayLoading()
        getHackerNewsPostsInteractor(hackerNewsPostId)
      }.onComplete(
        logger,
        tag,
        onSuccess = { hackerNewsPost ->
          view.get()?.onDisplayHackerPost(hackerNewsPost)
        },
        onFailure = {
          view.get()?.onFailedWithFullScreenError(it) { loadPostWithId(hackerNewsPostId) }
        }
      )
    }
  }

  override fun onDetachView() {
    view.clear()
  }
}
