package com.mobilejazz.kmmsample.mvi.application.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mobilejazz.kmmsample.mvi.application.HarmonySampleApp

sealed class ViewModelFactory : ViewModelProvider.Factory {

  object HackerPosts : ViewModelFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return HarmonySampleApp.appProvider.viewModelComponent.getHackerPostsViewModel() as T
    }
  }

  class HackerPostDetail(private val postId: Long) : ViewModelFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return HarmonySampleApp.appProvider.viewModelComponent.getHackerPostDetailViewModel(postId) as T
    }
  }
}
