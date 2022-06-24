package com.mobilejazz.kmmsample.mvi.application.ui.screens.hackerPosts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPost
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPosts
import com.mobilejazz.kmmsample.core.screen.mvi.hackerPosts.HackerPostsAction
import com.mobilejazz.kmmsample.core.screen.mvi.hackerPosts.HackerPostsNavigation
import com.mobilejazz.kmmsample.core.screen.mvi.hackerPosts.HackerPostsViewModel
import com.mobilejazz.kmmsample.core.screen.mvi.hackerPosts.HackerPostsViewState
import com.mobilejazz.kmmsample.mvi.R
import com.mobilejazz.kmmsample.mvi.application.collectAsStateLifecycleAware
import com.mobilejazz.kmmsample.mvi.application.ui.screens.ViewModelFactory
import com.mobilejazz.kmmsample.mvi.application.ui.screens.destinations.HackerPostDetailScreenDestination
import com.mobilejazz.kmmsample.mvi.application.ui.views.ErrorUI
import com.mobilejazz.kmmsample.mvi.application.ui.views.FullErrorView
import com.mobilejazz.kmmsample.mvi.application.ui.views.FullLoadingView
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

@RootNavGraph(start = true) // sets this as the start destination of the default nav graph
@Destination
@Composable
fun HackerPostsScreen(
  navigator: DestinationsNavigator,
  viewModel: HackerPostsViewModel = viewModel(factory = ViewModelFactory.HackerPosts)
) {
  Scaffold(topBar = {
    TopAppBar(title = { Text(stringResource(id = R.string.hacker_news)) })
  }) { padding ->
    Box(modifier = Modifier.padding(padding)) {
      when (val viewState = viewModel.viewState.collectAsStateLifecycleAware().value) {
        is HackerPostsViewState.Loading -> FullLoadingView()
        is HackerPostsViewState.Content -> {
          HackerPostsView(hackerNewsPosts = viewState.posts, onPostClick = {
            viewModel.onAction(HackerPostsAction.PostSelected(it.id))
          })
          viewState.navigation.consume {
            when (it) {
              is HackerPostsNavigation.ToDetail -> navigator.navigate(HackerPostDetailScreenDestination(hackerPostId = it.id))
            }
          }
        }
        is HackerPostsViewState.Error ->
          FullErrorView(
            errorUI = ErrorUI(
              message = viewState.message,
              retryButton = ErrorUI.RetryButton {
                viewModel.onAction(action = HackerPostsAction.Refresh)
              }
            )
          )
      }
    }
  }
}

@Composable
fun HackerPostsView(hackerNewsPosts: HackerNewsPosts, onPostClick: ((post: HackerNewsPost) -> Unit)) {
  val listState = rememberLazyListState()

  LazyColumn(state = listState) {
    items(items = hackerNewsPosts, key = { it.id }) { post ->
      Column(
        modifier = Modifier
          .clickable { onPostClick(post) }
          .fillParentMaxWidth()
          .padding(16.dp)
      ) {
        Text(
          text = post.time.toString(),
          style = MaterialTheme.typography.overline
        )
        Text(
          text = post.title,
          style = MaterialTheme.typography.h6
        )
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun HackerPostsPreview() {
  HackerPostsView(
    hackerNewsPosts = listOf(
      HackerNewsPost(
        1,
        "A. Simpson",
        0,
        null,
        0,
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        "Old man yells at cloud",
        null,
        null
      ),
      HackerNewsPost(
        2,
        "H. Simpson",
        0,
        null,
        0,
        Clock.System.now().minus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault()),
        "Human blimp sees flying saucer",
        null,
        null
      )
    ),
    onPostClick = {}
  )
}
