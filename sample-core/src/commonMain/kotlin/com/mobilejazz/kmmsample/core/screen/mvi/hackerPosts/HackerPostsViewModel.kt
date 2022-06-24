package com.mobilejazz.kmmsample.core.screen.mvi.hackerPosts

import com.harmony.kotlin.application.ui.mvi.Action
import com.harmony.kotlin.application.ui.mvi.Navigation
import com.harmony.kotlin.application.ui.mvi.OneShotEvent
import com.harmony.kotlin.application.ui.mvi.ViewModel
import com.harmony.kotlin.application.ui.mvi.ViewState
import com.harmony.kotlin.application.ui.mvi.update
import com.harmony.kotlin.common.logger.Logger
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.interactor.GetHackerNewsPostsInteractor
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPosts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HackerPostsAction : Action {
  object Refresh : HackerPostsAction()
  class PostSelected(val id: Long) : HackerPostsAction()
}

sealed class HackerPostsViewState : ViewState {
  object Loading : HackerPostsViewState()
  class Error(val message: String) : HackerPostsViewState()
  data class Content(
    val posts: HackerNewsPosts,
    val navigation: OneShotEvent<HackerPostsNavigation> = OneShotEvent.Empty()
  ) : HackerPostsViewState()
}

sealed class HackerPostsNavigation: Navigation {
  data class ToDetail constructor(val id: Long) : HackerPostsNavigation()
}

class HackerPostsViewModel(
  private val getPostsInteractor: GetHackerNewsPostsInteractor,
  private val logger: Logger
) : ViewModel<HackerPostsViewState, HackerPostsAction>() {

  private val _viewState: MutableStateFlow<HackerPostsViewState> = MutableStateFlow(HackerPostsViewState.Loading)
  override val viewState: StateFlow<HackerPostsViewState> = _viewState

  init {
    loadPosts()
  }

  private fun loadPosts() {
    _viewState.value = HackerPostsViewState.Loading
    launch {

      getPostsInteractor().fold(
        ifLeft = {
          _viewState.value = HackerPostsViewState.Error("Error happened")
        },
        ifRight = {
          _viewState.value = HackerPostsViewState.Content(it)
        })
    }
  }

  override fun onAction(action: HackerPostsAction) {
    when (action) {
      is HackerPostsAction.PostSelected -> {
        _viewState.value = _viewState.value.update { state: HackerPostsViewState.Content ->
          state.copy(navigation = OneShotEvent(HackerPostsNavigation.ToDetail(action.id)))
        }
      }
      HackerPostsAction.Refresh -> {
        loadPosts()
      }
    }
  }

}
