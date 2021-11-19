package com.mobilejazz.kmmsample.core

import com.harmony.kotlin.common.logger.Logger
import com.harmony.kotlin.data.datasource.cache.CacheSQLConfiguration
import com.mobilejazz.kmmsample.core.common.dispatcher
import com.mobilejazz.kmmsample.core.feature.hackerposts.HackerNewsPostsComponent
import com.mobilejazz.kmmsample.core.feature.hackerposts.HackerNewsPostsDefaultModule
import com.mobilejazz.kmmsample.core.screen.DefaultPresenterModule
import com.mobilejazz.kmmsample.core.screen.PresenterComponent
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.cbor.Cbor

interface ApplicationComponent {
  val presenterComponent: PresenterComponent
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
    DefaultNetworkComponent()
  }

  override val presenterComponent: PresenterComponent by lazy {
    DefaultPresenterModule(hackerNewsPostsComponent)
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
