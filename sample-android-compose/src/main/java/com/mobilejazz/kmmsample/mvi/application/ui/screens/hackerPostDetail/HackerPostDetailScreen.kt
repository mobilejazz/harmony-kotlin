package com.mobilejazz.kmmsample.mvi.application.ui.screens.hackerPostDetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPost
import com.mobilejazz.kmmsample.core.screen.mvi.hackerPostDetail.HackerPostDetailAction
import com.mobilejazz.kmmsample.core.screen.mvi.hackerPostDetail.HackerPostDetailViewModel
import com.mobilejazz.kmmsample.core.screen.mvi.hackerPostDetail.HackerPostDetailViewState
import com.mobilejazz.kmmsample.mvi.R
import com.mobilejazz.kmmsample.mvi.application.collectAsStateLifecycleAware
import com.mobilejazz.kmmsample.mvi.application.ui.screens.ViewModelFactory
import com.mobilejazz.kmmsample.mvi.application.ui.views.ErrorUI
import com.mobilejazz.kmmsample.mvi.application.ui.views.FullErrorView
import com.mobilejazz.kmmsample.mvi.application.ui.views.FullLoadingView
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Destination
@Composable
fun HackerPostDetailScreen(
  hackerPostId: Long,
  navigator: DestinationsNavigator,
  viewModel: HackerPostDetailViewModel = viewModel(factory = ViewModelFactory.HackerPostDetail(hackerPostId))
) {
  Scaffold(topBar = {
    TopAppBar(
      title = { Text(stringResource(id = R.string.hacker_news)) },
      navigationIcon = {
        IconButton(onClick = { navigator.navigateUp() }) {
          Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = null
          )
        }
      }
    )
  }) { padding ->
    BoxWithConstraints {
      Box(
        modifier = Modifier
          .padding(padding)
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
      ) {
        when (val viewState = viewModel.viewState.collectAsStateLifecycleAware().value) {
          is HackerPostDetailViewState.Loading -> FullLoadingView(modifier = Modifier.height(this@BoxWithConstraints.maxHeight))
          is HackerPostDetailViewState.Content -> HackerPostView(hackerNewsPost = viewState.post)
          is HackerPostDetailViewState.Error ->
            FullErrorView(
              errorUI = ErrorUI(
                message = viewState.message,
                retryButton = ErrorUI.RetryButton {
                  viewModel.onAction(action = HackerPostDetailAction.Refresh)
                }
              )
            )
        }
      }
    }
  }
}

@Composable
fun HackerPostView(
  hackerNewsPost: HackerNewsPost,
) {
  Column(modifier = Modifier.padding(16.dp)) {
    Text(
      text = hackerNewsPost.time.toString(),
      style = MaterialTheme.typography.overline,
      modifier = Modifier.fillMaxWidth(),
      textAlign = TextAlign.Center
    )
    Text(
      text = hackerNewsPost.title,
      style = MaterialTheme.typography.h6,
      modifier = Modifier.fillMaxWidth(),
      textAlign = TextAlign.Center
    )
    Text(
      text = stringResource(id = R.string.ls_author, hackerNewsPost.by),
      style = MaterialTheme.typography.overline,
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      textAlign = TextAlign.Center
    )
    Text(
      text = stringResource(id = R.string.ls_lorem),
      style = MaterialTheme.typography.body1,
      modifier = Modifier.fillMaxWidth(),
      textAlign = TextAlign.Left
    )
  }
}

@Composable
@Preview(showBackground = true)
fun HackerPostDetailPreview() {
  HackerPostView(
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
  )
}
