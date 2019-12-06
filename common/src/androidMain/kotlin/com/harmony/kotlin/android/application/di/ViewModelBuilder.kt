package com.harmony.kotlin.android.application.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelBuilder {
  @Binds
  abstract fun bindViewModelFactory(factory: com.harmony.kotlin.android.application.di.ViewModelFactory): ViewModelProvider.Factory
}