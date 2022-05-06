package com.harmony.kotlin.data.datasource.network.error

import com.harmony.kotlin.error.HarmonyException

/**
 * Exception for network connectivity problems
 */
class NetworkConnectivityException(message: String? = null, cause: Throwable? = null) : HarmonyException(message, cause)

/**
 * Exception for http client(40X) & server(50X) errors
 */
class HttpException(val statusCode: Int, val response: String?) : HarmonyException()
