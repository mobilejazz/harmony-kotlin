package com.mobilejazz.kotlin.core.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelBuilder {
  @Binds
  abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}