package com.harmony.kotlin.data.datasource.network.ktor

import com.harmony.kotlin.common.logger.ConsoleLogger
import com.harmony.kotlin.common.logger.Logger
import com.harmony.kotlin.data.datasource.network.DefaultUnauthorizedResolution
import com.harmony.kotlin.data.datasource.network.UnauthorizedResolution
import com.harmony.kotlin.data.error.DataNotFoundException
import com.harmony.kotlin.data.error.ktor.KtorNetworkException
import com.harmony.kotlin.data.error.ktor.ServerError
import com.harmony.kotlin.data.error.ktor.UnauthorizedKtorNetworkException
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.HttpResponseValidator
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.utils.io.charsets.Charsets
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


fun HttpClientConfig<*>.configureExceptionErrorMapping(unauthorizedResolution: UnauthorizedResolution = DefaultUnauthorizedResolution,
                                                       json: Json = Json(JsonConfiguration.Stable),
                                                       logger: Logger = ConsoleLogger()) {
  HttpResponseValidator {
    validateResponse { response ->
      val httpStatus = response.status
      if (!httpStatus.isSuccess()) {
        when (httpStatus.value) {
          HttpStatusCode.Unauthorized.value -> {
            val resolve = unauthorizedResolution.resolve()
            throw UnauthorizedKtorNetworkException(response, resolve)
          }
          HttpStatusCode.NotFound.value -> {
            throw DataNotFoundException()
          }
          else -> {
            val error = parseServerError(json, logger, response)
            throw KtorNetworkException(httpStatus, error)
          }
        }
      }
    }
  }
}


internal suspend fun parseServerError(json: Json, logger: Logger, response: HttpResponse): ServerError {
  return try {
    val content = response.readText(Charsets.UTF_8)
    json.parse(ServerError.serializer(), content)
  } catch (e: Exception) {
    logger.e(tag = "Network", message = "Error parsing the error body message: $e")
    ServerError.UnknownServerError
  }
}

