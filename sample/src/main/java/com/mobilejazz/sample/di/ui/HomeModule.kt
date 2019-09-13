package com.mobilejazz.sample.di.ui

import com.mobilejazz.harmony.kotlin.android.di.ActivityScope
import com.mobilejazz.harmony.kotlin.core.domain.interactor.GetInteractor
import com.mobilejazz.harmony.kotlin.core.repository.GetRepository
import com.mobilejazz.sample.core.domain.model.ItemIds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module
class HomeModule {

  @Provides
  @ActivityScope
  fun provideGetAllStories(scope: CoroutineScope, getRepository: GetRepository<ItemIds>): GetInteractor<ItemIds> {
    return GetInteractor(scope, getRepository)
  }
}