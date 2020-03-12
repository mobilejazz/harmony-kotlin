package com.harmony.kotlin.data.error

class NetworkErrorException(val statusCode: Int, message: String?, throwable: Throwable?) :
    RuntimeException(message, throwable)
