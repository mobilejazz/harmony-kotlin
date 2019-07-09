package com.mobilejazz.sample.di.ui

import com.mobilejazz.harmony.kotlin.android.di.ActivityScope
import com.mobilejazz.harmony.kotlin.core.interactor.GetInteractor
import com.mobilejazz.harmony.kotlin.core.repository.GetRepository
import com.mobilejazz.harmony.kotlin.core.threading.Executor
import com.mobilejazz.sample.core.domain.model.ItemIds
import dagger.Module
import dagger.Provides

@Module
class HomeModule {

  @Provides
  @ActivityScope
  fun provideGetAllStories(executor: Executor, getRepository: GetRepository<ItemIds>): GetInteractor<ItemIds> {
    return GetInteractor(executor, getRepository)
  }
}