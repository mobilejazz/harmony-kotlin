package com.mobilejazz.kmmsample.mvi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.mobilejazz.kmmsample.mvi.application.ui.screens.NavGraphs
import com.mobilejazz.kmmsample.mvi.application.ui.theme.HarmonyKotlinTheme
import com.ramcosta.composedestinations.DestinationsNavHost

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      HarmonyKotlinTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
          DestinationsNavHost(navGraph = NavGraphs.root)
        }
      }
    }
  }
}
