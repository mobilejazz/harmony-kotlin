package com.harmony.kotlin.data.error.ktor

import com.harmony.kotlin.data.error.NetworkErrorException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ServerError(@SerialName("statusCode") val code: Int, val name: String, val message: String) {
  companion object {
    val UnknownServerError = ServerError(-1, "Server Error", "Unknown Error")
    val UnauthorizedServerError = ServerError(1004, "Server Error", "Unauthorized user")
  }
}


open class KtorNetworkException(val status: HttpStatusCode, val error: ServerError) : NetworkErrorException(status.value, error.message, null)

class UnauthorizedKtorNetworkException(httpResponse: HttpResponse, isResolved: Boolean) : KtorNetworkException(HttpStatusCode.Unauthorized, ServerError.UnauthorizedServerError)


