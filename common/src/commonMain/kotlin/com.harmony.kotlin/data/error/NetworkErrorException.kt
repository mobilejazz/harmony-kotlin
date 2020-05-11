package com.harmony.kotlin.data.error

class NetworkErrorException(val statusCode: Int, message: String?, throwable: Throwable? = null) :
    RuntimeException(message, throwable)
