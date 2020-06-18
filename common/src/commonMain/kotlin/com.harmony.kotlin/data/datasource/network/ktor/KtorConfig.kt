package com.harmony.kotlin.data.datasource.network.ktor

import com.harmony.kotlin.data.error.ktor.KtorNetworkException
import com.harmony.kotlin.data.error.ktor.UnauthorizedKtorNetworkException
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.HttpResponseValidator
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess


fun HttpClientConfig<*>.configureExceptionErrorMapping() {
  HttpResponseValidator {
    validateResponse { response ->
      val httpStatus = response.status
      if (!httpStatus.isSuccess()) {
        if (httpStatus == HttpStatusCode.Unauthorized) {
          throw UnauthorizedKtorNetworkException(response)
        } else {
          throw KtorNetworkException(httpStatus.value, response)
        }
      }
    }
  }
}
