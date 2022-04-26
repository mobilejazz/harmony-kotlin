package com.harmony.kotlin.error

/**
 * Parent exception for all controlled Exceptions on Harmony and apps
 */
abstract class HarmonyException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

