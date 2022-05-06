package com.harmony.kotlin.error

/**
 * Not authorized to access the data
 */
class UnauthorizedException(message: String? = null, cause: Throwable, isResolved: Boolean) : HarmonyException(message, cause)
