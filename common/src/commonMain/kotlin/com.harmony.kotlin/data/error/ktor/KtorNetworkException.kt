package com.harmony.kotlin.data.error.ktor

import com.harmony.kotlin.data.error.NetworkErrorException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

open class KtorNetworkException(status: HttpStatusCode, httpResponse: HttpResponse) : NetworkErrorException(status.value, null, null)

class UnauthorizedKtorNetworkException(httpResponse: HttpResponse) : KtorNetworkException(HttpStatusCode.Unauthorized, httpResponse)


