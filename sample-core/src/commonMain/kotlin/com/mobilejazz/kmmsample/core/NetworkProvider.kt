package com.mobilejazz.kmmsample.core

import com.harmony.kotlin.common.logger.KtorHarmonyLogger
import com.harmony.kotlin.common.logger.Logger
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import kotlinx.serialization.json.Json

data class NetworkConfiguration(
  val httpClient: HttpClient,
  val json: Json,
  val baseUrl: String
)

interface NetworkComponent {
  val mainNetworkConfiguration: NetworkConfiguration
}

class NetworkDefaultModule(private val coreLogger: Logger) : NetworkComponent {

  override val mainNetworkConfiguration: NetworkConfiguration by lazy { NetworkConfiguration(httpClient, json, hackerNewsApiUrl) }

  private val hackerNewsApiUrl = "https://hacker-news.firebaseio.com/v0/"

  // Check behavior when iOS sample
  private val json by lazy {
    Json {
      isLenient = true
      ignoreUnknownKeys = true
    }
  }

  private val httpClient: HttpClient by lazy {
    HttpClient(engine) {
      install(JsonFeature) {
        serializer = KotlinxSerializer(json)
      }

      install(Logging) {
        logger = KtorHarmonyLogger(coreLogger)
        level = LogLevel.HEADERS
      }

      expectSuccess = false
    }
  }
}
