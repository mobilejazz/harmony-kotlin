package com.mobilejazz.kmmsample.core

import com.harmony.kotlin.common.logger.KtorHarmonyLogger
import com.harmony.kotlin.common.logger.Logger
import com.harmony.kotlin.data.datasource.network.ktor.configureExceptionErrorMapping
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

data class NetworkConfiguration(
  val httpClient: HttpClient,
  val json: Json,
  val baseUrl: String
)

interface NetworkComponent {
  val mainNetworkConfiguration: NetworkConfiguration
}

class NetworkDefaultModule(coreLogger: Logger) : NetworkComponent {
  private val hackerNewsApiUrl = "https://hacker-news.firebaseio.com/v0/"

  override val mainNetworkConfiguration: NetworkConfiguration by lazy { NetworkConfiguration(httpClient, JsonDefaultModule.json, hackerNewsApiUrl) }

  private object JsonDefaultModule {
    val json: Json
      get() {
        return Json {
          ignoreUnknownKeys = true
        }
      }
  }

  private val httpClient by lazy {
    HttpClient(engine) {
      install(ContentNegotiation) {
        json(json = JsonDefaultModule.json)
      }

      install(Logging) {
        logger = KtorHarmonyLogger(logger = coreLogger)
        level = LogLevel.ALL
      }

      install(HttpTimeout) {
        connectTimeoutMillis = 30_000
        socketTimeoutMillis = 30_000
      }
      configureExceptionErrorMapping()
      expectSuccess = false
    }
  }
}
