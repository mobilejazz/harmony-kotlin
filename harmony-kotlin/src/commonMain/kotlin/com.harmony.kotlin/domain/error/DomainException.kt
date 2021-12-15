package com.harmony.kotlin.domain.error

import com.harmony.kotlin.error.HarmonyException

/**
 * Generic exception for domain errors
 */
open class DomainException(override val message: String? = null, override val cause: Throwable? = null) : HarmonyException(message, cause)
