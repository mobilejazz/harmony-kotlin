package com.harmony.kotlin.data.datasource.network.ktor

import com.harmony.kotlin.common.exceptions.tryOrNull
import com.harmony.kotlin.data.datasource.network.DefaultUnauthorizedResolution
import com.harmony.kotlin.data.datasource.network.UnauthorizedResolution
import com.harmony.kotlin.data.datasource.network.error.HttpException
import com.harmony.kotlin.data.error.DataException
import com.harmony.kotlin.data.error.DataNotFoundException
import com.harmony.kotlin.data.error.UnauthorizedException
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.receive
import io.ktor.client.features.HttpResponseValidator
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

fun HttpClientConfig<*>.configureExceptionErrorMapping(unauthorizedResolution: UnauthorizedResolution = DefaultUnauthorizedResolution) {
  HttpResponseValidator {
    validateResponse { response ->
      val httpStatus = response.status
      if (!httpStatus.isSuccess()) {
        throw response.toDataException(unauthorizedResolution)
      }
    }
  }
}

suspend fun HttpResponse.toDataException(unauthorizedResolution: UnauthorizedResolution): DataException {
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

suspend fun HttpResponse.contentAsString(): String? {
  return tryOrNull {
    this.receive<String>()
  }
}
