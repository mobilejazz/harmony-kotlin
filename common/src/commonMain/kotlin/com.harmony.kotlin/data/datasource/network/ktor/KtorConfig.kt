package com.harmony.kotlin.data.datasource.network.ktor

import com.harmony.kotlin.data.datasource.network.DefaultUnauthorizedResolution
import com.harmony.kotlin.data.datasource.network.UnauthorizedResolution
import com.harmony.kotlin.data.error.DataNotFoundException
import com.harmony.kotlin.data.error.ktor.KtorNetworkException
import com.harmony.kotlin.data.error.ktor.UnauthorizedKtorNetworkException
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.HttpResponseValidator
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

fun HttpClientConfig<*>.configureExceptionErrorMapping(unauthorizedResolution: UnauthorizedResolution = DefaultUnauthorizedResolution) {
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
            throw KtorNetworkException(httpStatus, response)
          }
        }
      }
    }
  }
}
