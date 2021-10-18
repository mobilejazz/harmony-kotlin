package com.harmony.kotlin.data.error.ktor

import com.harmony.kotlin.data.error.NetworkErrorException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

open class KtorNetworkException(val status: HttpStatusCode, val httpResponse: HttpResponse) : NetworkErrorException(status.value, null, null)

class UnauthorizedKtorNetworkException(httpResponse: HttpResponse, isResolved: Boolean) : KtorNetworkException(HttpStatusCode.Unauthorized, httpResponse)
