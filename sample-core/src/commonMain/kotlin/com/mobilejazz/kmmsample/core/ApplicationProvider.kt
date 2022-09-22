package com.mobilejazz.kmmsample.core

import com.harmony.kotlin.common.logger.Logger
import com.harmony.kotlin.data.datasource.cache.CacheSQLConfiguration
import com.mobilejazz.kmmsample.core.common.dispatcher
import com.mobilejazz.kmmsample.core.feature.hackerposts.HackerNewsPostsComponent
import com.mobilejazz.kmmsample.core.feature.hackerposts.HackerNewsPostsDefaultModule
import com.mobilejazz.kmmsample.core.screen.mvi.ViewModelComponent
import com.mobilejazz.kmmsample.core.screen.mvi.ViewModelDefaultModule
import com.mobilejazz.kmmsample.core.screen.mvp.PresenterComponent
import com.mobilejazz.kmmsample.core.screen.mvp.PresenterDefaultModule
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.cbor.Cbor

interface ApplicationComponent {
  val presenterComponent: PresenterComponent
  val viewModelComponent: ViewModelComponent
}

expect val engine: HttpClientEngine

class ApplicationDefaultModule(
  private val coreLogger: Logger,
  private val cacheSQLConfiguration: CacheSQLConfiguration
) : ApplicationComponent {
  private val coroutineDispatcher: CoroutineDispatcher by lazy {
    dispatcher()
  }

  private val networkComponent by lazy {
    NetworkDefaultModule(coreLogger)
  }

  override val presenterComponent: PresenterComponent by lazy {
    PresenterDefaultModule(coreLogger, hackerNewsPostsComponent)
  }
  override val viewModelComponent: ViewModelComponent by lazy {
    ViewModelDefaultModule(coreLogger, hackerNewsPostsComponent)
  }

  private val hackerNewsPostsComponent: HackerNewsPostsComponent by lazy {
    HackerNewsPostsDefaultModule(
      networkComponent.mainNetworkConfiguration,
      coroutineDispatcher,
      cacheSQLConfiguration,
      Cbor
    )
  }
}
