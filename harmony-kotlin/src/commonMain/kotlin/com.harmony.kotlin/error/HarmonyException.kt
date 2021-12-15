package com.harmony.kotlin.error

/**
 * Generic exception, parent of all Harmony exceptions
 */
open class HarmonyException(message: String?, cause: Throwable?) : Exception(message, cause)
