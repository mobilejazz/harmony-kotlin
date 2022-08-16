package com.harmony.kotlin.data.datasource.network.ktor

import com.harmony.kotlin.common.exceptions.tryOrNull
import com.harmony.kotlin.data.datasource.network.DefaultUnauthorizedResolution
import com.harmony.kotlin.data.datasource.network.UnauthorizedResolution
import com.harmony.kotlin.data.datasource.network.error.HttpException
import com.harmony.kotlin.data.datasource.network.error.NetworkConnectivityException
import com.harmony.kotlin.error.DataNotFoundException
import com.harmony.kotlin.error.HarmonyException
import com.harmony.kotlin.error.UnauthorizedException
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.utils.io.errors.IOException

/**
 * Helper that configures the error mapping by:
 *  - Setting expectSuccess to false
 *  - Adding a HttpResponseValidator used to map errors
 */
fun HttpClientConfig<*>.configureExceptionErrorMapping(unauthorizedResolution: UnauthorizedResolution = DefaultUnauthorizedResolution) {
  expectSuccess = false

  HttpResponseValidator {
    validateResponse { response ->
      val httpStatus = response.status
      if (!httpStatus.isSuccess()) {
        throw response.toDataException(unauthorizedResolution)
      }
    }

    handleResponseExceptionWithRequest { cause, request ->
      if (cause is IOException) {
        throw NetworkConnectivityException(cause = cause)
      }
    }
  }
}

suspend fun HttpResponse.toDataException(unauthorizedResolution: UnauthorizedResolution): HarmonyException {
  if (status.isSuccess()) {
    throw IllegalStateException("Cannot generate a Exception from a successful response")
  }

  val httpException = HttpException(status.value, contentAsString())

  return when (status.value) {
    HttpStatusCode.NotFound.value -> DataNotFoundException(cause = httpException)
    HttpStatusCode.Unauthorized.value -> UnauthorizedException(cause = httpException, isResolved = unauthorizedResolution.resolve())
    else -> httpException
  }
}

private suspend fun HttpResponse.contentAsString(): String? {
  return tryOrNull {
    this.bodyAsText()
  }
}
