package com.mobilejazz.kmmsample.core.screen.mvi.hackerPostDetail

import com.harmony.kotlin.application.ui.mvi.Action
import com.harmony.kotlin.application.ui.mvi.ViewModel
import com.harmony.kotlin.application.ui.mvi.ViewState
import com.harmony.kotlin.common.logger.Logger
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.interactor.GetHackerNewsPostInteractor
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HackerPostDetailAction : Action {
  object Refresh : HackerPostDetailAction()
}

sealed class HackerPostDetailViewState : ViewState {
  object Loading : HackerPostDetailViewState()
  class Error(val message: String) : HackerPostDetailViewState()
  data class Content(val post: HackerNewsPost) : HackerPostDetailViewState()
}

class HackerPostDetailViewModel(
  private val postId: Long,
  private val getPostInteractor: GetHackerNewsPostInteractor,
  private val logger: Logger
) : ViewModel<HackerPostDetailViewState, HackerPostDetailAction>() {

  private val _viewState: MutableStateFlow<HackerPostDetailViewState> = MutableStateFlow(HackerPostDetailViewState.Loading)
  override val viewState: StateFlow<HackerPostDetailViewState> = _viewState

  init {
    loadPost()
  }

  private fun loadPost() {
    _viewState.value = HackerPostDetailViewState.Loading
    launch {

      getPostInteractor(postId).fold(
        ifLeft = {
          _viewState.value = HackerPostDetailViewState.Error("Error happened")
        },
        ifRight = {
          _viewState.value = HackerPostDetailViewState.Content(it)
        }
      )
    }
  }

  override fun onAction(action: HackerPostDetailAction) {
    when (action) {
      HackerPostDetailAction.Refresh -> {
        loadPost()
      }
    }
  }
}
