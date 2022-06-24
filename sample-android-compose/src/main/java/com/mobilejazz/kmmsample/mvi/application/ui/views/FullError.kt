package com.mobilejazz.kmmsample.mvi.application.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class ErrorUI(val message: String, val retryButton: RetryButton? = null) {
  data class RetryButton(val text: String = "Retry", val onClick: () -> Unit)
}

@Composable
fun FullErrorView(errorUI: ErrorUI, modifier: Modifier = Modifier) {
  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = errorUI.message)
      errorUI.retryButton?.let {
        Button(
          onClick = { it.onClick() },
          modifier = Modifier.padding(vertical = 16.dp)
        ) {
          Text(
            text = it.text,
            modifier = Modifier.padding(horizontal = 8.dp)
          )
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun ErrorPreview(modifier: Modifier = Modifier) {
  FullErrorView(ErrorUI("An error happened", ErrorUI.RetryButton("Retry") {}))
}
